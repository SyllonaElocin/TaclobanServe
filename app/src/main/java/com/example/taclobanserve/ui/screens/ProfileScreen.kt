package com.example.taclobanserve.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taclobanserve.CheckInRequest
import com.example.taclobanserve.TaclobanUser
import com.example.taclobanserve.ui.utils.exportProfileToPdf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: TaclobanUser?,
    checkIns: List<CheckInRequest>,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val themeOrange = Color(0xFFF4511E)
    val navyDark = Color(0xFF0F172A)
    
    // Fallback default user if null
    val displayUser = user ?: TaclobanUser("Juan Dela Cruz", "juan@example.com", "Medical & Healthcare", listOf("First Aid", "CPR"), "password123", "2021")
    val verifiedCheckIns = checkIns.filter { it.userName == displayUser.name && it.status == "VERIFIED" }
    val totalHours = verifiedCheckIns.sumOf { it.earnedHours }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bayanihan Transcript", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", fontSize = 24.sp)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📥", fontSize = 18.sp)
                            }
                        }
                    }
                    IconButton(onClick = {}) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            color = themeOrange,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🔗", fontSize = 18.sp, color = Color.White)
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Verified Badge at the top
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = themeOrange,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🛡️", fontSize = 24.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Official Service\nTranscript",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = navyDark,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Document ID: BT-2023-${(1000..9999).random()}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = themeOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    "Generated on October 24, 2023",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Profile Image with Verification Ring
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                    ) {
                        // Placeholder for image
                        Text("👤", modifier = Modifier.align(Alignment.Center), fontSize = 64.sp)
                    }
                    Surface(
                        modifier = Modifier.size(32.dp).padding(2.dp),
                        color = themeOrange,
                        shape = CircleShape,
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✔️", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(displayUser.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = navyDark)
                Text(
                    "Volunteer ID: ${displayUser.volunteerId}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    "Verified Volunteer since ${displayUser.joinedYear}",
                    fontSize = 14.sp,
                    color = themeOrange,
                    fontWeight = FontWeight.ExtraBold
                )
                Text("Tacloban, Philippines", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(40.dp))

                // Stats in big cards
                StatCardRow(label = "TOTAL HOURS", value = String.format("%.1f", totalHours))
                Spacer(modifier = Modifier.height(12.dp))
                StatCardRow(label = "IMPACT SCORE", value = if(verifiedCheckIns.isEmpty()) "0.0" else "8.5")
                Spacer(modifier = Modifier.height(12.dp))
                StatCardRow(label = "PROJECTS", value = verifiedCheckIns.size.toString())

                Spacer(modifier = Modifier.height(40.dp))

                // Service Summary Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(4.dp).height(24.dp).background(themeOrange))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Service Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = navyDark)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (verifiedCheckIns.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No verified service records yet.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    verifiedCheckIns.forEach { req ->
                        ServiceDetailItem(
                            title = req.eventTitle,
                            date = req.timestamp.split(",").first(),
                            desc = "Successfully completed mission tasks in Tacloban City.",
                            hours = "${req.earnedHours} Service Hours",
                            icon = "✅"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Institutional Authentication Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // QR Code Simulation
                        Surface(
                            modifier = Modifier.size(120.dp),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("QR", fontSize = 40.sp, color = Color.LightGray)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "Institutional Authentication",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = navyDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "This document is a certified digital record of volunteer service. Scan the QR code to verify its authenticity via the Bayanihan Blockchain Registry.",
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            AuthTag("🔒 ENCRYPTED")
                            AuthTag("🏛️ GOV-COMPLIANT")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AuthTag("🔄 IMMUTABLE")
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    "BAYANIHAN VOLUNTEER NETWORK - OFFICIAL\nDIGITAL CERTIFICATE • SERIES 2023",
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { exportProfileToPdf(context, displayUser, verifiedCheckIns) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📄", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Export as PDF", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCardRow(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 28.sp, color = Color(0xFFF4511E), fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ServiceDetailItem(title: String, date: String, desc: String, hours: String, icon: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Surface(
            modifier = Modifier.size(44.dp),
            color = Color(0xFFFFF3E0),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 22.sp)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                Text(date, fontSize = 11.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(hours, fontSize = 12.sp, color = Color(0xFFF4511E), fontWeight = FontWeight.ExtraBold)
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun AuthTag(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.LightGray,
        letterSpacing = 0.5.sp
    )
}
