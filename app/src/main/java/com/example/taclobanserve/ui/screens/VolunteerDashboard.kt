package com.example.taclobanserve.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taclobanserve.TaclobanEvent
import com.example.taclobanserve.JoinProjectRequest
import com.example.taclobanserve.ui.utils.toTimeString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VolunteerDashboard(
    skills: List<String> = listOf("Medical", "Logistics"), // Demo skills
    events: List<TaclobanEvent>,
    joinedEvents: List<TaclobanEvent>,
    pendingJoinRequests: List<JoinProjectRequest>,
    joinedMissions: List<Pair<String, TaclobanEvent>>, // Added to calculate spots
    onJoinEvent: (TaclobanEvent) -> Unit,
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToMap: (TaclobanEvent) -> Unit, // Updated to take event
    onNavigateToProfile: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Smart Matches", "Active Events", "Joined Events")
    val themeOrange = Color(0xFFF4511E) 

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(themeOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏠", color = Color.White, fontSize = 16.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Smart Dashboard", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Text("↩️", fontSize = 24.sp)
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        color = Color(0xFFFFF3E0),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🔔", fontSize = 18.sp)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.size(40.dp),
                        color = Color.LightGray,
                        shape = CircleShape,
                        onClick = onNavigateToProfile
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("👤", fontSize = 18.sp)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Text("🏠", fontSize = 20.sp) }, label = { Text("Home") })
                NavigationBarItem(selected = false, onClick = onNavigateToHistory, icon = { Text("🕒", fontSize = 20.sp) }, label = { Text("Activity") })
                NavigationBarItem(selected = false, onClick = onNavigateToProfile, icon = { Text("👤", fontSize = 20.sp) }, label = { Text("Profile") })
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = themeOrange,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = themeOrange
                        )
                    }
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                text = title,
                                color = if (selectedTab == index) themeOrange else Color.Gray,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> SmartMatchesContent(skills, events, joinedEvents, pendingJoinRequests, joinedMissions, onJoinEvent, themeOrange)
                    1 -> ActiveEventsContent(events, joinedEvents, pendingJoinRequests, joinedMissions, onJoinEvent, themeOrange)
                    2 -> JoinedEventsContent(joinedEvents, onNavigateToMap, themeOrange)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmartMatchesContent(
    userSkills: List<String>, 
    allEvents: List<TaclobanEvent>,
    joinedEvents: List<TaclobanEvent>,
    pendingJoinRequests: List<JoinProjectRequest>,
    joinedMissions: List<Pair<String, TaclobanEvent>>,
    onJoinEvent: (TaclobanEvent) -> Unit,
    themeOrange: Color
) {
    Text(
        "YOUR SKILL PROFILE",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF424242),
        letterSpacing = 0.5.sp
    )
    Spacer(Modifier.height(12.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        userSkills.forEach { skill ->
            SkillChip(skill, themeOrange)
        }
    }

    Spacer(Modifier.height(28.dp))

    val matchedEvents = allEvents.filter { event ->
        event.tags.any { tag -> userSkills.contains(tag) }
    }.sortedByDescending { event -> 
        event.tags.count { tag -> userSkills.contains(tag) }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Prioritized for You", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
        if (matchedEvents.isNotEmpty()) {
            Surface(color = Color(0xFFFFEBE8), shape = RoundedCornerShape(6.dp)) {
                Text(
                    "${matchedEvents.size} NEW MATCHES",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = themeOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    if (matchedEvents.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
        ) {
            Text(
                "No direct matches yet. Try adding more skills to your profile!",
                modifier = Modifier.padding(20.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    } else {
        matchedEvents.forEach { event ->
            val isJoined = joinedEvents.contains(event)
            val isPending = pendingJoinRequests.any { it.event == event }
            val now = System.currentTimeMillis()
            val hasEnded = now > event.endTime
            
            // Calculate dynamic spots remaining
            val initialSpots = event.volunteers.toIntOrNull() ?: 0
            val approvedCount = joinedMissions.count { it.second == event }
            val spotsLeft = (initialSpots - approvedCount).coerceAtLeast(0)

            ProjectCard(
                title = event.title,
                description = event.description,
                distance = "Tacloban: ${event.area}",
                duration = "${event.startTime.toTimeString()} - ${event.endTime.toTimeString()}",
                time = "${event.hours}h / week",
                spots = if (isJoined) "Joined" else if (isPending) "Pending Confirmation" else if (hasEnded) "Completed" else "$spotsLeft spots left",
                matchPercent = "SKILL MATCH",
                status = when {
                    isJoined -> "ENROLLED"
                    isPending -> "REQUESTED"
                    hasEnded -> "COMPLETED"
                    else -> "RECOMMENDED"
                },
                statusColor = when {
                    isJoined -> Color(0xFF388E3C)
                    isPending -> Color(0xFFFFA000)
                    hasEnded -> Color.Gray
                    else -> themeOrange
                },
                themeColor = themeOrange,
                isJoinable = !isJoined && !isPending && !hasEnded,
                imageUri = event.imageUri,
                onJoin = { onJoinEvent(event) }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ActiveEventsContent(
    events: List<TaclobanEvent>, 
    joinedEvents: List<TaclobanEvent>,
    pendingJoinRequests: List<JoinProjectRequest>,
    joinedMissions: List<Pair<String, TaclobanEvent>>,
    onJoinEvent: (TaclobanEvent) -> Unit,
    themeOrange: Color
) {
    Text("Available Missions", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
    Spacer(Modifier.height(16.dp))
    
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
            Text("No active missions currently.", color = Color.Gray)
        }
    } else {
        events.forEach { event ->
            val isJoined = joinedEvents.contains(event)
            val isPending = pendingJoinRequests.any { it.event == event }
            val now = System.currentTimeMillis()
            val hasEnded = now > event.endTime

            // Calculate dynamic spots remaining
            val initialSpots = event.volunteers.toIntOrNull() ?: 0
            val approvedCount = joinedMissions.count { it.second == event }
            val spotsLeft = (initialSpots - approvedCount).coerceAtLeast(0)

            ProjectCard(
                title = event.title,
                description = event.description,
                distance = "Tacloban: ${event.area}",
                duration = "${event.startTime.toTimeString()} - ${event.endTime.toTimeString()}",
                time = "${event.hours}h / week",
                spots = if (isJoined) "Joined" else if (isPending) "Pending Confirmation" else if (hasEnded) "Completed" else "$spotsLeft spots left",
                matchPercent = "GENERAL",
                status = when {
                    isJoined -> "ENROLLED"
                    isPending -> "REQUESTED"
                    hasEnded -> "COMPLETED"
                    else -> "ACTIVE"
                },
                statusColor = when {
                    isJoined -> Color(0xFF388E3C)
                    isPending -> Color(0xFFFFA000)
                    hasEnded -> Color.Gray
                    else -> Color(0xFF1976D2)
                },
                themeColor = themeOrange,
                isJoinable = !isJoined && !isPending && !hasEnded,
                imageUri = event.imageUri,
                onJoin = { onJoinEvent(event) }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun JoinedEventsContent(joinedEvents: List<TaclobanEvent>, onCheckIn: (TaclobanEvent) -> Unit, themeOrange: Color) {
    Text("Your Commitments", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
    Spacer(Modifier.height(16.dp))
    
    if (joinedEvents.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
            Text("You haven't joined any events yet.", color = Color.Gray)
        }
    } else {
        joinedEvents.forEach { event ->
            ProjectCard(
                title = event.title,
                description = event.description,
                distance = "Tacloban: ${event.area}",
                duration = "${event.startTime.toTimeString()} - ${event.endTime.toTimeString()}",
                time = "${event.hours}h / week",
                spots = "Joined",
                matchPercent = "ENROLLED",
                status = "VERIFIED",
                statusColor = Color(0xFF388E3C),
                themeColor = themeOrange,
                isJoinable = true, // Set to true so button is enabled
                imageUri = event.imageUri,
                onJoin = { onCheckIn(event) },
                buttonText = "Check In at Site"
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun SkillChip(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProjectCard(
    title: String,
    description: String,
    distance: String,
    duration: String, // Added duration
    time: String,
    spots: String,
    matchPercent: String,
    status: String,
    statusColor: Color,
    themeColor: Color,
    isJoinable: Boolean,
    imageUri: String? = null,
    onJoin: () -> Unit,
    buttonText: String? = null // New prop
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFE0E0E0))
            ) {
                // Image Placeholder or Actual Image
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        Text("🖼️", fontSize = 48.sp) // Representing selected image
                    } else {
                        Text("🏞️", fontSize = 48.sp) // Standard placeholder
                    }
                }
                
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(status, statusColor)
                    StatusBadge(matchPercent, Color(0xFFE64A19))
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF212121))
                    Text(distance, color = themeColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                Spacer(Modifier.height(10.dp))
                Text(
                    description, 
                    color = Color(0xFF757575), 
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏱️", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(duration, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = themeColor)
                }
                
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🕒", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(time, fontSize = 13.sp, color = Color(0xFF616161))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👥", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(spots, fontSize = 13.sp, color = Color(0xFF616161))
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = onJoin,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isJoinable) themeColor else Color(0xFFE1E8ED)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isJoinable
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isJoinable) {
                            Text("👤✔", fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                        } else {
                            Text("🔒", fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            buttonText ?: (if (isJoinable) "Join Project" else if (spots == "Joined") "Joined" else if (status == "REQUESTED") "Requested" else if (status == "COMPLETED") "Completed" else "Join Project (Locked)"),
                            color = if (isJoinable) Color.White else Color(0xFF90A4AE),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
