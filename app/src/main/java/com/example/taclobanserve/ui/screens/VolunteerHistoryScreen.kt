package com.example.taclobanserve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.taclobanserve.CheckInRequest
import com.example.taclobanserve.TaclobanUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerHistoryScreen(
    user: TaclobanUser?,
    checkIns: List<CheckInRequest>,
    onNavigateToHome: () -> Unit,
    onNavigateToTranscript: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val themeOrange = Color(0xFFF4511E)
    val backgroundColor = Color(0xFFF9F9F9)
    val darkCardBackground = Color(0xFF0F172A)
    val verifiedGreen = Color(0xFFE8F5E9)
    val verifiedTextGreen = Color(0xFF2E7D32)
    
    val verifiedCheckIns = checkIns.filter { it.userName == user?.name && it.status == "VERIFIED" }
    val totalHours = verifiedCheckIns.sumOf { it.earnedHours }
    
    var showAllBadges by remember { mutableStateOf(false) }

    // Dynamic Milestone Badges
    val badges = listOf(
        BadgeData("Bronze Milestone", (totalHours / 10.0).coerceAtMost(1.0).toFloat(), "🥉"),
        BadgeData("Silver Milestone", (totalHours / 100.0).coerceAtMost(1.0).toFloat(), "🥈"),
        BadgeData("Gold Milestone", (totalHours / 1000.0).coerceAtMost(1.0).toFloat(), "🥇"),
        BadgeData("Community Leader", (verifiedCheckIns.size / 10f).coerceAtMost(1f), "🎖️"),
        BadgeData("Rescue Rookie", (verifiedCheckIns.size / 3f).coerceAtMost(1f), "🛶")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("🕒", fontSize = 20.sp) },
                    label = { Text("Activity") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = themeOrange,
                        selectedTextColor = themeOrange,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(selected = false, onClick = onNavigateToProfile, icon = { Text("👤", fontSize = 20.sp) }, label = { Text("Profile") })
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Audit Trail & Activity",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Top side-by-side containers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Events Joined",
                    value = verifiedCheckIns.size.toString(),
                    icon = "✅",
                    themeOrange = themeOrange
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Hours",
                    value = String.format("%.1f", totalHours),
                    icon = "🕒",
                    themeOrange = themeOrange
                )
            }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Digital Badge Progress Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Digital Badge Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF101828)
                )
                TextButton(onClick = { showAllBadges = !showAllBadges }) {
                    Text(
                        text = if (showAllBadges) "Show Less" else "View All",
                        color = themeOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val sortedBadges = badges.sortedByDescending { it.progress }
                    val displayBadges = if (showAllBadges) {
                        // All sorted: In progress first, then completed at bottom
                        val inProgress = sortedBadges.filter { it.progress < 1.0f }
                        val completed = sortedBadges.filter { it.progress >= 1.0f }
                        inProgress + completed
                    } else {
                        // Max 3, highest progress first, excluding 100%
                        sortedBadges.filter { it.progress < 1.0f }.take(3)
                    }

                    displayBadges.forEachIndexed { index, badge ->
                        BadgeProgressItem(badge, themeOrange)
                        if (index < displayBadges.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFEEEEEE)
                            )
                        }
                    }
                    
                    if (displayBadges.isEmpty() && !showAllBadges) {
                        Text("All milestones reached! View all to see your trophies.", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Recent Activity Section (Previously "History")
            Text(
                text = "Recent Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (verifiedCheckIns.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No recent activity found.", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                verifiedCheckIns.forEach { req ->
                    ActivityCard(
                        title = req.eventTitle,
                        location = "Verified Tacloban Area",
                        dateTime = "${req.timestamp} • ${req.earnedHours} hrs",
                        icon = "✔️",
                        iconBg = Color(0xFFFFF3F0),
                        iconTint = themeOrange,
                        status = "VERIFIED",
                        statusBg = verifiedGreen,
                        statusText = verifiedTextGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer "How it works"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = darkCardBackground)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "📖",
                        fontSize = 120.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 20.dp, y = 20.dp)
                            .graphicsLayer(alpha = 0.15f)
                    )

                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "How it works",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Earn Bayanihan Credits by participating in verified community services. Credits can be redeemed for local rewards or used to upgrade your profile status.",
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNavigateToTranscript,
                            colors = ButtonDefaults.buttonColors(containerColor = themeOrange),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Read User Guide ↗️", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class BadgeData(val name: String, val progress: Float, val icon: String)

@Composable
fun SummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: String,
    themeOrange: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = themeOrange.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(text = title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BadgeProgressItem(badge: BadgeData, themeOrange: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(badge.icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = badge.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF101828)
                )
            }
            Text(
                text = "${(badge.progress * 100).toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (badge.progress >= 1f) Color(0xFF2E7D32) else themeOrange
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { badge.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (badge.progress >= 1f) Color(0xFF4CAF50) else themeOrange,
            trackColor = Color(0xFFEEEEEE)
        )
    }
}

@Composable
fun ActivityCard(
    title: String,
    location: String,
    dateTime: String,
    icon: String,
    iconBg: Color,
    iconTint: Color,
    status: String,
    statusBg: Color,
    statusText: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = iconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = iconTint
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = icon, fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = status,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = location,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateTime,
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}
