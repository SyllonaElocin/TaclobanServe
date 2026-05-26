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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventMapScreen(
    event: TaclobanEvent,
    joinedMissions: List<Pair<String, TaclobanEvent>>,
    checkIns: List<CheckInRequest>,
    onNavigateBack: () -> Unit
) {
    val themeOrange = Color(0xFFF4511E)
    
    val mapView = rememberMapViewWithLifecycle()

    val enrolledCount = joinedMissions.count { it.second == event }
    val verifiedCheckIns = checkIns.count { it.eventTitle == event.title && it.status == "VERIFIED" }
    val totalHours = verifiedCheckIns * 2 // Simulating 2 hours per verified session

    // Map area string to coordinates (simulated for demo)
    val eventCoords = when {
        event.area.contains("Downtown") -> GeoPoint(11.2433, 125.0012)
        event.area.contains("San Jose") -> GeoPoint(11.2268, 125.0275)
        event.area.contains("Abucay") -> GeoPoint(11.2445, 124.9812)
        event.area.contains("Sagkahan") -> GeoPoint(11.2312, 125.0050)
        event.area.contains("Marasbaras") -> GeoPoint(11.2155, 125.0035)
        else -> GeoPoint(11.2433, 125.0012)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mission Live Monitor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", fontSize = 24.sp)
                    }
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
                        controller.setCenter(eventCoords)
                        
                        // Clear existing overlays to avoid duplicates on recomposition
                        overlays.clear()

                        // Geofence Circle
                        Polygon(this).apply {
                            points = Polygon.pointsAsCircle(eventCoords, 200.0)
                            fillPaint.color = themeOrange.copy(alpha = 0.15f).toArgb()
                            outlinePaint.color = themeOrange.toArgb()
                            outlinePaint.strokeWidth = 2f
                            overlays.add(this)
                        }

                        // Event Marker
                        Marker(this).apply {
                            position = eventCoords
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = event.title
                            snippet = event.area
                            overlays.add(this)
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Live Status Dashboard for Admin
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFF4CAF50), shape = CircleShape, modifier = Modifier.size(8.dp)) {}
                        Spacer(Modifier.width(8.dp))
                        Text("Live Project: ${event.title}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                    Text("📍 Geofence active at ${event.area}", color = Color.Gray, fontSize = 13.sp)
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        LiveStatBox("Active Volunteers", "$enrolledCount / ${event.volunteers}")
                        LiveStatBox("Hours Logged", "${totalHours}h")
                    }

                    Spacer(Modifier.height(16.dp))
                    
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeOrange),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Return to Hub", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LiveStatBox(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
    }
}
