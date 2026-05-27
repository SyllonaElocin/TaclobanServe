@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.taclobanserve.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taclobanserve.TaclobanEvent
import com.example.taclobanserve.TaclobanUser
import com.example.taclobanserve.CheckInRequest
import com.example.taclobanserve.JoinProjectRequest
import com.example.taclobanserve.ui.theme.TaclobanServeTheme
import com.example.taclobanserve.ui.utils.*

@Composable
fun AdminDashboard(
    events: SnapshotStateList<TaclobanEvent>,
    users: SnapshotStateList<TaclobanUser>,
    checkIns: SnapshotStateList<CheckInRequest>,
    joinRequests: SnapshotStateList<JoinProjectRequest>,
    joinedMissions: SnapshotStateList<Pair<String, TaclobanEvent>>, // Changed to track user-event pairs
    userGpsSim: Map<String, Boolean>, // Global simulation state
    onLogout: () -> Unit,
    onApproveJoin: (JoinProjectRequest) -> Unit,
    onNavigateToUserProfile: (TaclobanUser) -> Unit,
    onViewEventMap: (TaclobanEvent) -> Unit,
    onEditEvent: (TaclobanEvent) -> Unit,
    onQuickUpdateEvent: (TaclobanEvent) -> Unit // Added for End Mission action
) {
    var selectedBottomTab by remember { mutableIntStateOf(0) }
    var selectedEventTab by remember { mutableIntStateOf(0) }
    val themeOrange = Color(0xFFF4511E)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = when(selectedBottomTab) {
                            0 -> if (selectedEventTab == 0) "Event Creation" else "Existing Events"
                            1 -> "User Management Hub"
                            2 -> "Attendance Verification"
                            else -> "System Settings"
                        }, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Text("↩️", fontSize = 24.sp)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        Text("🔔", fontSize = 24.sp)
                        Surface(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.TopEnd),
                            color = themeOrange,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.White)
                        ) {}
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(selected = selectedBottomTab == 0, onClick = { selectedBottomTab = 0 }, icon = { Text("📝", fontSize = 20.sp) }, label = { Text("Events") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = themeOrange, selectedTextColor = themeOrange, indicatorColor = Color(0xFFFFEBE8)))
                NavigationBarItem(selected = selectedBottomTab == 1, onClick = { selectedBottomTab = 1 }, icon = { Text("👥", fontSize = 20.sp) }, label = { Text("Users") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = themeOrange, selectedTextColor = themeOrange, indicatorColor = Color(0xFFFFEBE8)))
                NavigationBarItem(selected = selectedBottomTab == 2, onClick = { selectedBottomTab = 2 }, icon = { 
                    BadgedBox(badge = { if(joinRequests.isNotEmpty()) Badge { Text(joinRequests.size.toString()) } }) {
                        Text("🛡️", fontSize = 20.sp) 
                    }
                }, label = { Text("Verification") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = themeOrange, selectedTextColor = themeOrange, indicatorColor = Color(0xFFFFEBE8)))
                NavigationBarItem(selected = selectedBottomTab == 3, onClick = { selectedBottomTab = 3 }, icon = { Text("⚙️", fontSize = 20.sp) }, label = { Text("Settings") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = themeOrange, selectedTextColor = themeOrange, indicatorColor = Color(0xFFFFEBE8)))
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().background(Color(0xFFFAFAFA))) {
            if (selectedBottomTab == 0) {
                TabRow(selectedTabIndex = selectedEventTab, containerColor = Color.White, contentColor = themeOrange, indicator = { tabPositions -> TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedEventTab]), color = themeOrange) }) {
                    Tab(selected = selectedEventTab == 0, onClick = { selectedEventTab = 0 }, text = { Text("Create Event", fontWeight = FontWeight.Bold) })
                    Tab(selected = selectedEventTab == 1, onClick = { selectedEventTab = 1 }, text = { Text("View Events", fontWeight = FontWeight.Bold) })
                }
            }

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
                when (selectedBottomTab) {
                    0 -> {
                        if (selectedEventTab == 0) {
                            EventCreationConsole(themeOrange = themeOrange, onEventCreated = { newEvent -> events.add(0, newEvent); selectedEventTab = 1 })
                        } else {
                            EventViewHub(themeOrange, events, joinedMissions, users, checkIns, userGpsSim, onViewEventMap, onEditEvent, onQuickUpdateEvent)
                        }
                    }
                    1 -> UsersContent(users, themeOrange, onNavigateToUserProfile)
                    2 -> AttendanceVerification(themeOrange, checkIns, joinRequests, events, userGpsSim, onApproveJoin)
                    3 -> SettingsContent()
                }
            }
        }
    }
}

data class TaclobanArea(val name: String, val coords: String)

@Composable
fun EventCreationConsole(themeOrange: Color, onEventCreated: (TaclobanEvent) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var volunteers by remember { mutableStateOf("") }
    var durationHours by remember { mutableStateOf("2") } // Default 2 hours
    var selectedProfessions by remember { mutableStateOf(setOf<String>()) }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    
    val professionOptions = listOf(
        "Logistics & Supply Chain", 
        "Medical & Healthcare", 
        "Education & Training", 
        "Information Technology", 
        "Manufacturing & Engineering"
    )
    val skillOptions = listOf(
        "Driving", "Heavy Lifting", "Inventory Management", "Route Planning",
        "First Aid", "CPR", "Nursing", "Psychological Support", "Emergency Triage",
        "Teaching", "Tutoring", "Storytelling", "Childcare", "Curriculum Dev",
        "Networking", "Hardware Repair", "Data Entry", "Software Dev", "Radio Comms",
        "Welding", "Machining", "CAD", "Safety Inspection", "Assembly"
    )

    val taclobanAreas = listOf(TaclobanArea("Downtown (City Hall Area)", "11.2433, 125.0012"), TaclobanArea("San Jose (Airport District)", "11.2268, 125.0275"), TaclobanArea("Abucay (New Bus Terminal)", "11.2445, 124.9812"), TaclobanArea("Sagkahan (Astrodome)", "11.2312, 125.0050"), TaclobanArea("Marasbaras (Robinsons Area)", "11.2155, 125.0035"), TaclobanArea("V&G Subdivision", "11.2290, 124.9780"), TaclobanArea("Caibaan", "11.2100, 124.9850"), TaclobanArea("Utap", "11.2380, 124.9890"))
    var expanded by remember { mutableStateOf(false) }
    var selectedArea by remember { mutableStateOf<TaclobanArea?>(null) }
    var showSuccess by remember { mutableStateOf(false) }

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val selectedDateText = datePickerState.selectedDateMillis?.let { 
        java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(it))
    } ?: "Select Date"

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri?.toString() }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showSuccess) {
        AlertDialog(onDismissRequest = { showSuccess = false }, confirmButton = { TextButton(onClick = { 
            showSuccess = false 
            val baseTime = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
            val startTime = baseTime 
            val durationMillis = (durationHours.toLongOrNull() ?: 2) * 3600000
            onEventCreated(TaclobanEvent(
                title = title, 
                description = description, 
                hours = hours, 
                volunteers = volunteers, 
                area = selectedArea?.name ?: "Unknown", 
                professions = selectedProfessions.toList(),
                tags = selectedSkills.toList(), 
                imageUri = selectedImageUri, 
                startTime = startTime, 
                endTime = startTime + durationMillis
            ))
            title = ""; description = ""; hours = ""; volunteers = ""; selectedArea = null; selectedProfessions = emptySet(); selectedSkills = emptySet(); selectedImageUri = null
        }) { Text("OK") } }, title = { Text("Success") }, text = { Text("Event '$title' published and geofenced at ${selectedArea?.name}. Duration: $durationHours hours.") })
    }

    Text("New Community Project", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp)); Text("Fill in the details to deploy a new mission.", color = Color.Gray, fontSize = 14.sp)
    Spacer(Modifier.height(24.dp))

    Text("Event Cover Image", fontSize = 14.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1)),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (selectedImageUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷", fontSize = 32.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Tap to add photo", fontSize = 12.sp, color = Color.Gray)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().background(themeOrange.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Text("✅ Image Selected", color = themeOrange, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    Spacer(Modifier.height(24.dp))
    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project Title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)
    Spacer(Modifier.height(16.dp))
    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(10.dp))
    Spacer(Modifier.height(16.dp))
    Text("Schedule Details", fontSize = 14.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    
    OutlinedTextField(
        value = selectedDateText,
        onValueChange = {},
        readOnly = true,
        label = { Text("Start Date") },
        leadingIcon = { Text("📅") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        enabled = false, 
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
    // Overlay Box to capture clicks
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .offset(y = (-64).dp)
            .clickable { showDatePicker = true }
    )
    
    Spacer(Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = durationHours, onValueChange = { durationHours = it }, label = { Text("Duration (Hours)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), leadingIcon = { Text("⏱️") })
        OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Hrs/Wk Goal") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
    }
    Spacer(Modifier.height(16.dp))
    Text("Target Profession Tags", fontSize = 14.sp, fontWeight = FontWeight.Bold)
    FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        professionOptions.forEach { prof ->
            val isSelected = selectedProfessions.contains(prof)
            FilterChip(selected = isSelected, onClick = { selectedProfessions = if (isSelected) selectedProfessions - prof else selectedProfessions + prof }, label = { Text(prof) })
        }
    }
    Spacer(Modifier.height(16.dp))
    Text("Required Skill Tags", fontSize = 14.sp, fontWeight = FontWeight.Bold)
    FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        skillOptions.forEach { skill ->
            val isSelected = selectedSkills.contains(skill)
            FilterChip(selected = isSelected, onClick = { selectedSkills = if (isSelected) selectedSkills - skill else selectedSkills + skill }, label = { Text(skill) })
        }
    }
    Spacer(Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = volunteers, onValueChange = { volunteers = it }, label = { Text("Volunteers Needed") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Box(modifier = Modifier.weight(1f)) // Placeholder
    }
    Spacer(Modifier.height(16.dp))
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedArea?.name ?: "", 
            onValueChange = {}, 
            readOnly = true, 
            label = { Text("Geofence Target Area") }, 
            placeholder = { Text("Select an area in Tacloban") }, 
            leadingIcon = { Text("📍") }, 
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, 
            colors = OutlinedTextFieldDefaults.colors(), 
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(), 
            shape = RoundedCornerShape(10.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            taclobanAreas.forEach { area -> DropdownMenuItem(text = { Column { Text(area.name, fontWeight = FontWeight.Bold); Text(area.coords, fontSize = 11.sp, color = Color.Gray) } }, onClick = { selectedArea = area; expanded = false }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding) }
        }
    }
    Spacer(Modifier.height(32.dp))
    Button(onClick = { showSuccess = true }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = themeOrange), shape = RoundedCornerShape(12.dp), enabled = title.isNotBlank() && selectedArea != null) {
        Row(verticalAlignment = Alignment.CenterVertically) { Text("🛰️", fontSize = 18.sp); Spacer(Modifier.width(8.dp)); Text("Publish & Geofence Event", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
    }
}

@Composable
fun EventViewHub(
    themeOrange: Color, 
    events: SnapshotStateList<TaclobanEvent>, 
    joinedMissions: SnapshotStateList<Pair<String, TaclobanEvent>>,
    users: List<TaclobanUser>,
    checkIns: List<CheckInRequest>,
    userGpsSim: Map<String, Boolean>,
    onViewMap: (TaclobanEvent) -> Unit, 
    onEditEvent: (TaclobanEvent) -> Unit,
    onQuickUpdateEvent: (TaclobanEvent) -> Unit
) {
    Text("Active Missions Hub", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp)); Text("Manage and monitor all live geofenced projects.", color = Color.Gray, fontSize = 14.sp)
    Spacer(Modifier.height(24.dp))
    events.forEach { event -> 
        val enrolledVolunteers = joinedMissions.filter { it.second == event }
        EventItemCard(event, enrolledVolunteers, users, checkIns, userGpsSim, themeOrange, onViewMap, onEditEvent, onQuickUpdateEvent)
        Spacer(Modifier.height(16.dp)) 
    }
}

@Composable
fun EventItemCard(
    event: TaclobanEvent, 
    enrolledVolunteers: List<Pair<String, TaclobanEvent>>,
    users: List<TaclobanUser>,
    checkIns: List<CheckInRequest>,
    userGpsSim: Map<String, Boolean>,
    themeOrange: Color, 
    onViewMap: (TaclobanEvent) -> Unit, 
    onEditEvent: (TaclobanEvent) -> Unit,
    onQuickUpdateEvent: (TaclobanEvent) -> Unit
) {
    val now = System.currentTimeMillis()
    val isOngoing = now in event.startTime..event.endTime
    val hasEnded = now > event.endTime

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, if (hasEnded) Color.LightGray else Color(0xFFEEEEEE))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(event.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (hasEnded) Color.Gray else Color(0xFF212121))
                Surface(
                    color = when {
                        hasEnded -> Color(0xFFF5F5F5)
                        isOngoing -> Color(0xFFE8F5E9)
                        else -> Color(0xFFFFF3E0)
                    }, 
                    shape = RoundedCornerShape(8.dp)
                ) { 
                    Text(
                        text = when {
                            hasEnded -> "ENDED"
                            isOngoing -> "LIVE"
                            else -> "SCHEDULED"
                        }, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                        color = when {
                            hasEnded -> Color.Gray
                            isOngoing -> Color(0xFF2E7D32)
                            else -> Color(0xFFE65100)
                        }, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold
                    ) 
                }
            }
            Spacer(Modifier.height(8.dp)); Text(event.description, fontSize = 14.sp, color = Color.Gray)
            
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🕒", fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Text("${event.startTime.toTimeString()} - ${event.endTime.toTimeString()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = themeOrange)
            }

            if (enrolledVolunteers.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("LIVE MONITORING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                
                enrolledVolunteers.forEach { (email, _) ->
                    val user = users.find { it.email == email }
                    val currentSimInRange = userGpsSim[user?.name] ?: false
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(24.dp), color = Color(0xFFF5F5F5), shape = CircleShape) {
                                Box(contentAlignment = Alignment.Center) { Text("👤", fontSize = 12.sp) }
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(user?.name ?: "Unknown User", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        
                        Surface(
                            color = if (currentSimInRange) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (currentSimInRange) "IN RANGE" else "OUT OF RANGE",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (currentSimInRange) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            if (event.imageUri != null) {
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF1F1F1)), contentAlignment = Alignment.Center) { Text("🖼️ Event Image", color = Color.Gray) }
            }
            Spacer(Modifier.height(12.dp))
            Text("Target Professions:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeOrange)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { 
                event.professions.forEach { prof -> AssistChip(onClick = {}, label = { Text(prof, fontSize = 10.sp) }, shape = CircleShape) } 
            }
            Spacer(Modifier.height(8.dp))
            Text("Required Skills:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { 
                event.tags.forEach { skill -> AssistChip(onClick = {}, label = { Text(skill, fontSize = 10.sp) }, shape = CircleShape) } 
            }
            Spacer(Modifier.height(16.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Text("📍", fontSize = 14.sp); Spacer(Modifier.width(8.dp)); Text(event.area, fontSize = 13.sp, color = Color.DarkGray) }
            Spacer(Modifier.height(8.dp)); Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text("🕒", fontSize = 14.sp); Spacer(Modifier.width(4.dp)); Text(event.hours + "/week", fontSize = 13.sp, color = Color.Gray) }; Row(verticalAlignment = Alignment.CenterVertically) { Text("👥", fontSize = 14.sp); Spacer(Modifier.width(4.dp)); Text(event.volunteers + " spots", fontSize = 13.sp, color = Color.Gray) } }
            Spacer(Modifier.height(16.dp)); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { 
                if (!hasEnded) {
                    TextButton(onClick = { onQuickUpdateEvent(event.copy(endTime = System.currentTimeMillis())) }) { Text("End Mission", color = Color.Red) }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { onEditEvent(event) }) { Text("Edit Details", color = themeOrange) }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onViewMap(event) }, colors = ButtonDefaults.buttonColors(containerColor = themeOrange), shape = RoundedCornerShape(8.dp)) { Text("Live Map", fontSize = 12.sp) }
                } else {
                    Text("Mission Completed", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AttendanceVerification(
    themeOrange: Color, 
    checkIns: SnapshotStateList<CheckInRequest>,
    joinRequests: SnapshotStateList<JoinProjectRequest>,
    events: List<TaclobanEvent>,
    userGpsSim: Map<String, Boolean>,
    onApproveJoin: (JoinProjectRequest) -> Unit
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    
    Column {
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color.White,
            contentColor = themeOrange,
            indicator = { tabPositions -> TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]), color = themeOrange) }
        ) {
            Tab(selected = selectedSubTab == 0, onClick = { selectedSubTab = 0 }, text = { Text("Attendance", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedSubTab == 1, onClick = { selectedSubTab = 1 }, text = { 
                BadgedBox(badge = { if(joinRequests.isNotEmpty()) Badge { Text(joinRequests.size.toString()) } }) {
                    Text("Join Requests", fontWeight = FontWeight.Bold)
                }
            })
        }

        Column(modifier = Modifier.padding(top = 24.dp)) {
            if (selectedSubTab == 0) {
                Text("Verification Queue", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Spacer(Modifier.height(8.dp)); Text("Process manual overrides and verified records.", color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))
                
                ManualOverrideWithJustification(themeOrange) { newRequest ->
                    checkIns.add(0, newRequest)
                }
                
                Spacer(Modifier.height(32.dp))
                Text("PENDING VERIFICATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF607D8B), letterSpacing = 1.sp)
                Spacer(Modifier.height(16.dp))
                
                val pendingCheckIns = checkIns.filter { it.status != "VERIFIED" }

                if (pendingCheckIns.isEmpty()) { 
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { 
                        Text("No pending attendance requests.", color = Color.Gray) 
                    } 
                } else {
                    pendingCheckIns.forEach { req -> 
                        val event = events.find { it.title == req.eventTitle }
                        val currentInRange = userGpsSim[req.userName] ?: req.isWithinRange
                        PendingRequestItem(
                            req = req, 
                            event = event, 
                            currentSimInRange = currentInRange,
                            themeOrange = themeOrange,
                            onVerify = {
                                val index = checkIns.indexOf(req)
                                if (index != -1) {
                                    val earned = event?.let { calculateServiceHours(it) } ?: 0.0
                                    if (req.isManualOverride && req.status == "PENDING") {
                                        // First admin approved, needs secondary
                                        checkIns[index] = req.copy(status = "REQUIRES_SECONDARY", firstAdminId = "Admin_01")
                                    } else {
                                        // Automatic verified or secondary approved
                                        checkIns[index] = req.copy(
                                            status = "VERIFIED", 
                                            secondAdminId = if (req.isManualOverride) "Admin_02" else null,
                                            earnedHours = earned
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(Modifier.height(12.dp)) 
                    }
                }
            } else {
                Text("Mission Enrollment", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Spacer(Modifier.height(8.dp)); Text("Approve volunteers waiting to join missions.", color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))
                if (joinRequests.isEmpty()) { Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Text("No pending join requests.", color = Color.Gray) } } else {
                    joinRequests.forEach { req -> JoinRequestItem(req, themeOrange, onApproveJoin); Spacer(Modifier.height(12.dp)) }
                }
            }
        }
    }
}

@Composable
fun ManualOverrideWithJustification(themeOrange: Color, onSubmit: (CheckInRequest) -> Unit) {
    var isOverrideInitiated by remember { mutableStateOf(false) }
    var volunteerName by remember { mutableStateOf("") }
    var eventTitle by remember { mutableStateOf("") }
    var referenceId by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf("GPS Failure") }
    var justificationText by remember { mutableStateOf("") }
    
    val reasons = listOf("GPS Failure", "Hardware Breakdown", "Battery Depleted", "Signal Deadzone")
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Admin Manual Override", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp)); Text("Bypass GPS geofence in case of technical failure. Requires dual-admin approval.", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(16.dp))
            
            if (!isOverrideInitiated) { 
                Button(onClick = { isOverrideInitiated = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5), contentColor = Color.DarkGray)) { 
                    Text("Initiate Manual Override") 
                } 
            } else {
                OutlinedTextField(value = volunteerName, onValueChange = { volunteerName = it }, label = { Text("Volunteer Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = eventTitle, onValueChange = { eventTitle = it }, label = { Text("Mission Title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = referenceId, onValueChange = { referenceId = it }, label = { Text("Reference ID (e.g. #VOL-123)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedReason, 
                        onValueChange = {}, 
                        readOnly = true, 
                        label = { Text("Reason for Override") }, 
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, 
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(), 
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        reasons.forEach { reason -> DropdownMenuItem(text = { Text(reason) }, onClick = { selectedReason = reason; expanded = false }) }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                Text("Mandatory Justification Note", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = themeOrange)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = justificationText, onValueChange = { justificationText = it }, placeholder = { Text("Provide technical justification for this override...") }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(10.dp))
                
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { 
                        val now = java.text.SimpleDateFormat("MMM dd, h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
                        onSubmit(CheckInRequest(
                            userName = volunteerName,
                            eventTitle = eventTitle,
                            timestamp = now,
                            isWithinRange = false,
                            isManualOverride = true,
                            referenceId = referenceId,
                            overrideReason = selectedReason,
                            justification = justificationText,
                            status = "PENDING"
                        ))
                        isOverrideInitiated = false
                        volunteerName = ""; eventTitle = ""; referenceId = ""; justificationText = ""
                    }, 
                    enabled = volunteerName.isNotBlank() && eventTitle.isNotBlank() && referenceId.isNotBlank() && justificationText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(), 
                    colors = ButtonDefaults.buttonColors(containerColor = themeOrange)
                ) { 
                    Text("Submit for Secondary Approval") 
                }
                TextButton(onClick = { isOverrideInitiated = false }, modifier = Modifier.fillMaxWidth()) { Text("Cancel", color = Color.Gray) }
            }
        }
    }
}

@Composable
fun JoinRequestItem(req: JoinProjectRequest, themeOrange: Color, onApprove: (JoinProjectRequest) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), color = Color.LightGray, shape = CircleShape) { Box(contentAlignment = Alignment.Center) { Text("👤", fontSize = 24.sp) } }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(req.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                Text("Wants to join:", fontSize = 11.sp, color = Color.Gray)
                Text(req.event.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = themeOrange)
            }
            Button(onClick = { onApprove(req) }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Text("Approve", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UsersContent(users: List<TaclobanUser>, themeOrange: Color, onViewProfile: (TaclobanUser) -> Unit) {
    var selectedProfession by remember { mutableStateOf("All Professions") }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    val professions = listOf("All Professions", "Logistics & Supply Chain", "Medical & Healthcare", "Education & Training", "Information Technology", "Manufacturing & Engineering")
    val allSkills = listOf("First Aid", "CPR", "Nursing", "Driving", "Heavy Lifting", "Teaching", "Tutoring", "Software Dev", "Networking")
    var profExpanded by remember { mutableStateOf(false) }
    var skillExpanded by remember { mutableStateOf(false) }
    Text("User Management", fontSize = 24.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(16.dp))
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Filter by Profession", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            ExposedDropdownMenuBox(expanded = profExpanded, onExpandedChange = { profExpanded = !profExpanded }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedProfession, 
                    onValueChange = {}, 
                    readOnly = true, 
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = profExpanded) }, 
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(), 
                    shape = RoundedCornerShape(10.dp), 
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themeOrange)
                )
                ExposedDropdownMenu(expanded = profExpanded, onDismissRequest = { profExpanded = false }) { professions.forEach { prof -> DropdownMenuItem(text = { Text(prof) }, onClick = { selectedProfession = prof; profExpanded = false }) } }
            }
            Spacer(Modifier.height(16.dp)); Text("Filter by Skills", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            ExposedDropdownMenuBox(expanded = skillExpanded, onExpandedChange = { skillExpanded = !skillExpanded }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (selectedSkills.isEmpty()) "Select Skills" else "${selectedSkills.size} Selected", 
                    onValueChange = {}, 
                    readOnly = true, 
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = skillExpanded) }, 
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(), 
                    shape = RoundedCornerShape(10.dp), 
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themeOrange)
                )
                ExposedDropdownMenu(expanded = skillExpanded, onDismissRequest = { skillExpanded = false }) { allSkills.forEach { skill -> DropdownMenuItem(text = { Text(skill) }, onClick = { selectedSkills = if (selectedSkills.contains(skill)) selectedSkills - skill else selectedSkills + skill; skillExpanded = false }) } }
            }
            if (selectedSkills.isNotEmpty()) { Spacer(Modifier.height(12.dp)); FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { selectedSkills.forEach { skill -> InputChip(selected = true, onClick = { selectedSkills = selectedSkills - skill }, label = { Text(skill, fontSize = 12.sp) }, trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp)) }) } } }
        }
    }
    Spacer(Modifier.height(24.dp))
    val filteredUsers = users.filter { user -> (selectedProfession == "All Professions" || user.profession == selectedProfession) && (selectedSkills.isEmpty() || selectedSkills.all { user.skills.contains(it) }) }
    if (filteredUsers.isEmpty()) { Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Text("No volunteers match your criteria.", color = Color.Gray) } } else { filteredUsers.forEach { user -> UserListItem(user, themeOrange, onViewProfile); Spacer(Modifier.height(12.dp)) } }
}

@Composable
fun UserListItem(user: TaclobanUser, themeOrange: Color, onViewProfile: (TaclobanUser) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(52.dp), color = Color.LightGray, shape = CircleShape) { Box(contentAlignment = Alignment.Center) { Text("👤", fontSize = 28.sp) } }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF212121))
                    Spacer(Modifier.width(8.dp))
                    Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(4.dp)) {
                        Text(user.volunteerId, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
                Text(user.profession, fontSize = 12.sp, color = themeOrange, fontWeight = FontWeight.Bold)
                Text(user.skills.joinToString(", "), fontSize = 12.sp, color = Color.Gray)
            }
            TextButton(onClick = { onViewProfile(user) }) { Text("Profile", color = Color.Gray, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun SettingsContent() {
    Text("System Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(16.dp)); Text("Configure Firebase Sync and API endpoints.", color = Color.Gray)
}

@Composable
fun PendingRequestItem(req: CheckInRequest, event: TaclobanEvent?, currentSimInRange: Boolean, themeOrange: Color, onVerify: () -> Unit) {
    val now = System.currentTimeMillis()
    val isOnTime = event?.let { now in it.startTime..it.endTime } ?: false
    val isVerifiable = (currentSimInRange && isOnTime) || req.status == "REQUIRES_SECONDARY"
    
    val statusText = when {
        req.status == "REQUIRES_SECONDARY" -> "PENDING SECONDARY APPROVAL"
        req.isManualOverride -> "MANUAL OVERRIDE"
        else -> "AUTOMATIC GPS"
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, if(req.status == "REQUIRES_SECONDARY") themeOrange else Color(0xFFEEEEEE))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(48.dp), color = Color.LightGray, shape = CircleShape) { Box(contentAlignment = Alignment.Center) { Text("👤", fontSize = 24.sp) } }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(req.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                    Text(req.eventTitle, fontSize = 12.sp, color = Color.Gray)
                    
                    Surface(color = if(req.isManualOverride) Color(0xFFFFF3E0) else Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                        Text(statusText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = if(req.isManualOverride) themeOrange else Color(0xFF2E7D32))
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            if (req.isManualOverride) {
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp)).padding(12.dp)) {
                    Text("Ref ID: ${req.referenceId}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Reason: ${req.overrideReason}", fontSize = 11.sp)
                    Text("Note: ${req.justification}", fontSize = 11.sp, color = Color.Gray)
                    if (req.status == "REQUIRES_SECONDARY") {
                        Text("1st Approval by: ${req.firstAdminId}", fontSize = 10.sp, color = themeOrange, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (currentSimInRange) "📍 In Range" else "📍 Out of Range", color = if (currentSimInRange) Color(0xFF2E7D32) else Color(0xFFD32F2F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text(text = if (isOnTime) "⏱️ On Time" else "⏱️ Outside Schedule", color = if (isOnTime) Color(0xFF2E7D32) else Color(0xFFD32F2F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(req.timestamp, fontSize = 10.sp, color = Color.LightGray)
                }
                
                Button(
                    onClick = onVerify, 
                    enabled = isVerifiable, 
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if(req.status == "REQUIRES_SECONDARY") Color(0xFF4CAF50) else themeOrange)
                ) { 
                    Text(
                        text = when {
                            req.status == "REQUIRES_SECONDARY" -> "Final Approve"
                            req.isManualOverride -> "Approve (1/2)"
                            isVerifiable -> "Verify"
                            else -> "Locked"
                        }, 
                        fontWeight = FontWeight.Bold
                    ) 
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    TaclobanServeTheme {
        AdminDashboard(onLogout = {}, events = remember { mutableStateListOf() }, users = remember { mutableStateListOf() }, checkIns = remember { mutableStateListOf() }, joinRequests = remember { mutableStateListOf() }, joinedMissions = remember { mutableStateListOf() }, userGpsSim = emptyMap(), onApproveJoin = {}, onNavigateToUserProfile = {}, onViewEventMap = {}, onEditEvent = {}, onQuickUpdateEvent = {})
    }
}
