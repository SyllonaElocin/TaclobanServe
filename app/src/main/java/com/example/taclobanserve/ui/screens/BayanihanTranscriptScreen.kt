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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BayanihanTranscriptScreen(
    onNavigateBack: () -> Unit
) {
    val themeOrange = Color(0xFFF4511E)

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
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📥", fontSize = 16.sp)
                            }
                        }
                    }
                    IconButton(onClick = {}) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            color = themeOrange,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🔗", fontSize = 16.sp, color = Color.White)
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
                // Verified Badge
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = themeOrange,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🛡️", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Official Service\nTranscript",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF101828),
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Document ID: BT-2023-8821",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = themeOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    "Generated on October 24, 2023",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    // Placeholder for image
                    Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxSize()) {
                        Surface(
                            modifier = Modifier.size(24.dp).padding(2.dp),
                            color = themeOrange,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✔️", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Juan Dela Cruz", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Verified Volunteer since 2021",
                    fontSize = 14.sp,
                    color = themeOrange,
                    fontWeight = FontWeight.Medium
                )
                Text("Manila, Philippines", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(40.dp))

                // Stats
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(label = "TOTAL HOURS", value = "428", modifier = Modifier.weight(1f))
                    StatCard(label = "IMPACT SCORE", value = "9.8", modifier = Modifier.weight(1f))
                    StatCard(label = "PROJECTS", value = "14", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Service Summary
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.width(4.dp).height(24.dp).background(themeOrange))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Service Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ServiceItem(
                        title = "Typhoon Egay Relief Ops",
                        date = "JUL 2023",
                        description = "Lead coordinator for logistics and distribution in Northern Luzon. Managed a team of 15 volunteers.",
                        hours = "84 Service Hours",
                        icon = "🌊"
                    )

                    ServiceItem(
                        title = "Digital Literacy Program",
                        date = "JAN - MAY 2023",
                        description = "Conducted weekly basic computer classes for elderly citizens in Barangay 143 community center.",
                        hours = "120 Service Hours",
                        icon = "🎓"
                    )

                    ServiceItem(
                        title = "Pasig River Clean-up",
                        date = "MAR 2023",
                        description = "Participated in the annual environmental rehabilitation project. Collected 200kg of waste.",
                        hours = "16 Service Hours",
                        icon = "🍃"
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Institutional Authentication
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // QR Code Placeholder
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("QR", fontSize = 40.sp, color = Color.LightGray)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "Institutional Authentication",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF101828)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "This document is a certified digital record of volunteer service. Scan the QR code to verify its authenticity via the Bayanihan Blockchain Registry.",
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = Color(0xFF475467),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AuthBadge("🔒 ENCRYPTED")
                            AuthBadge("🏛️ GOV-COMPLIANT")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        AuthBadge("🔄 IMMUTABLE")
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    "BAYANIHAN VOLUNTEER NETWORK - OFFICIAL\nDIGITAL CERTIFICATE • SERIES 2023",
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📄 Export as PDF", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(80.dp),
        color = Color(0xFFFFF3E0).copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 24.sp, color = Color(0xFFF4511E), fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ServiceItem(
    title: String,
    date: String,
    description: String,
    hours: String,
    icon: String
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Surface(
            modifier = Modifier.size(40.dp),
            color = Color(0xFFFFF3E0),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(date, fontSize = 11.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(hours, fontSize = 12.sp, color = Color(0xFFF4511E), fontWeight = FontWeight.Bold)
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun AuthBadge(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.LightGray,
        letterSpacing = 0.5.sp
    )
}
