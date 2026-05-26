package com.example.taclobanserve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import com.example.taclobanserve.TaclobanEvent
import com.example.taclobanserve.CheckInRequest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResilientMapScreen(
    userName: String,
    event: TaclobanEvent, // New prop
    isSimulatingInRange: Boolean,
    onGpsSimToggle: (Boolean) -> Unit,
    onCheckIn: (CheckInRequest) -> Unit,
    onNavigateBack: () -> Unit
) {
    val themeOrange = Color(0xFFF4511E)
    
    // Map area string to coordinates
    val missionPoint = when {
        event.area.contains("Downtown") -> GeoPoint(11.2433, 125.0012)
        event.area.contains("San Jose") -> GeoPoint(11.2268, 125.0275)
        event.area.contains("Abucay") -> GeoPoint(11.2445, 124.9812)
        event.area.contains("Sagkahan") -> GeoPoint(11.2312, 125.0050)
        event.area.contains("Marasbaras") -> GeoPoint(11.2155, 125.0035)
        else -> GeoPoint(11.2433, 125.0012)
    }

    // State based on Technical Diagram
    var isOfflineMode by remember { mutableStateOf(true) }
    var syncQueueCount by remember { mutableIntStateOf(0) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var checkInSuccess by remember { mutableStateOf(false) }

    val mapView = rememberMapViewWithLifecycle()

    if (showSyncDialog) {
        AlertDialog(
            onDismissRequest = { showSyncDialog = false },
            confirmButton = { TextButton(onClick = { showSyncDialog = false }) { Text("OK") } },
            title = { Text("Resilience Mode: Queue Sync") },
            text = { Text("Your check-ins are timestamped and queued locally using SharedPrefs. They will auto-push to Firebase once online.") }
        )
    }
    
    if (checkInSuccess) {
        AlertDialog(
            onDismissRequest = { checkInSuccess = false },
            confirmButton = { TextButton(onClick = { checkInSuccess = false }) { Text("OK") } },
            title = { Text("Check-in Successful") },
            text = { Text("Timestamped action recorded. Request sent to Admin Hub.") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resilient Map Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Text("←", fontSize = 24.sp) } },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("GPS SIM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Switch(
                            checked = isSimulatingInRange,
                            onCheckedChange = onGpsSimToggle,
                            colors = SwitchDefaults.colors(checkedThumbColor = themeOrange)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))
                    mapView.apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null) // Disable HW acceleration for stability on emulators
                        controller.setZoom(16.0)
                        controller.setCenter(missionPoint)
                        
                        // Clear existing overlays to avoid duplicates on recomposition
                        overlays.clear()

                        Polygon(this).apply {
                            points = Polygon.pointsAsCircle(missionPoint, 200.0)
                            fillPaint.color = themeOrange.copy(alpha = 0.15f).toArgb()
                            outlinePaint.color = themeOrange.toArgb()
                            outlinePaint.strokeWidth = 2f
                            overlays.add(this)
                        }
                        Marker(this).apply {
                            position = missionPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = event.title
                            snippet = event.area
                            overlays.add(this)
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Indicators
            Column(modifier = Modifier.padding(16.dp).align(Alignment.TopStart), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = Color(0xFF37474F), shape = RoundedCornerShape(24.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isOfflineMode) "☁️" else "📶", fontSize = 12.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isOfflineMode) "Resilience Mode" else "Online", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(24.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🔋", fontSize = 12.sp); Spacer(Modifier.width(8.dp))
                        Text("Battery-Saving GPS Active", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                if (syncQueueCount > 0) {
                    Surface(color = themeOrange, shape = RoundedCornerShape(24.dp), onClick = { showSyncDialog = true }) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🔄", fontSize = 12.sp); Spacer(Modifier.width(8.dp))
                            Text("$syncQueueCount Action Queued", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            // Geofence Control
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomCenter), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(8.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = if (isSimulatingInRange) Color(0xFF4CAF50) else Color.Gray, shape = CircleShape, modifier = Modifier.size(8.dp)) {}
                        Spacer(Modifier.width(8.dp))
                        Text(text = if (isSimulatingInRange) "Within 200m Verification Radius" else "Outside Radius", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSimulatingInRange) Color(0xFF4CAF50) else Color.Gray)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(event.title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text("📍 ${event.area}", color = Color.Gray, fontSize = 14.sp)
                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { 
                            val now = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date())
                            onCheckIn(CheckInRequest(userName, event.title, now, isWithinRange = isSimulatingInRange))
                            if (isOfflineMode) syncQueueCount++
                            checkInSuccess = true
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSimulatingInRange) themeOrange else Color(0xFFE0E0E0), contentColor = if (isSimulatingInRange) Color.White else Color(0xFF9E9E9E)),
                        enabled = isSimulatingInRange,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = if (isSimulatingInRange) "Automatic GPS Check-In ✅" else "GPS Verification Locked 🔒", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
