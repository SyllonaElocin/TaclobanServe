package com.example.taclobanserve.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taclobanserve.TaclobanUser
import com.example.taclobanserve.ui.theme.TaclobanServeTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: (TaclobanUser) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedExpertise by remember { mutableStateOf("") }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }

    val themeOrange = Color(0xFFF4511E)

    val expertiseOptions = listOf(
        ExpertiseOption("Logistics & Supply Chain", "Operations, warehousing, and transportation."),
        ExpertiseOption("Medical & Healthcare", "Clinical care, nursing, and administration."),
        ExpertiseOption("Education & Training", "Teaching, curriculum design, and coaching."),
        ExpertiseOption("Information Technology", "Software, infrastructure, and cybersecurity."),
        ExpertiseOption("Manufacturing & Engineering", "Industrial design, robotics, and production.")
    )
    
    val skillsMap = mapOf(
        "Logistics & Supply Chain" to listOf("Driving", "Heavy Lifting", "Inventory Management", "Route Planning"),
        "Medical & Healthcare" to listOf("First Aid", "CPR", "Nursing", "Psychological Support", "Emergency Triage"),
        "Education & Training" to listOf("Teaching", "Tutoring", "Storytelling", "Childcare", "Curriculum Dev"),
        "Information Technology" to listOf("Networking", "Hardware Repair", "Data Entry", "Software Dev", "Radio Comms"),
        "Manufacturing & Engineering" to listOf("Welding", "Machining", "CAD", "Safety Inspection", "Assembly")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Skill Profiling", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (currentStep > 1) currentStep-- else onNavigateToLogin()
                    }) {
                        Text("←", fontSize = 24.sp)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Progress Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Profile Setup", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Step $currentStep of 3", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { currentStep / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = themeOrange,
                trackColor = Color(0xFFFFE0B2)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(targetState = currentStep, label = "SignUpStepAnimation") { step ->
                Column {
                    when (step) {
                        1 -> AccountInfoStep(
                            name = name, onNameChange = { name = it },
                            email = email, onEmailChange = { email = it },
                            password = password, onPasswordChange = { password = it }
                        )
                        2 -> ExpertiseStep(
                            selectedExpertise = selectedExpertise,
                            onExpertiseSelected = { 
                                selectedExpertise = it
                                selectedSkills = emptySet()
                            },
                            options = expertiseOptions,
                            themeOrange = themeOrange
                        )
                        3 -> {
                            val allUniqueSkills = skillsMap.values.flatten().distinct()
                            val relatedSkills = skillsMap[selectedExpertise] ?: emptyList()
                            val otherSkills = allUniqueSkills.filter { !relatedSkills.contains(it) }

                            SkillsStep(
                                selectedSkills = selectedSkills,
                                onSkillToggle = { skill ->
                                    selectedSkills = if (selectedSkills.contains(skill)) {
                                        selectedSkills - skill
                                    } else {
                                        selectedSkills + skill
                                    }
                                },
                                relatedSkills = relatedSkills,
                                otherSkills = otherSkills,
                                themeOrange = themeOrange
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Buttons
            Button(
                onClick = {
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        onSignUpSuccess(
                            TaclobanUser(
                                name = name,
                                email = email,
                                profession = selectedExpertise,
                                skills = selectedSkills.toList(),
                                password = password // Pass the actual password
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeOrange),
                shape = RoundedCornerShape(10.dp),
                enabled = when (currentStep) {
                    1 -> name.isNotBlank() && email.isNotBlank() && 
                         android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && 
                         password.isNotBlank()
                    2 -> selectedExpertise.isNotBlank()
                    3 -> true // Allow moving forward even with no skills if needed, though disabled previously
                    else -> false
                }
            ) {
                Text(
                    text = if (currentStep == 1) "Continue" else if (currentStep == 2) "Continue to Skills" else "Complete Registration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { 
                    onSignUpSuccess(
                        TaclobanUser(
                            name = name.ifBlank { "New Volunteer" },
                            email = email.ifBlank { "anonymous@example.com" },
                            profession = "Not Specified",
                            skills = emptyList()
                        )
                    ) 
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip for now", color = Color(0xFF607D8B), fontWeight = FontWeight.Medium)
            }
        }
    }
}

data class ExpertiseOption(val title: String, val description: String)

@Composable
fun AccountInfoStep(
    name: String, onNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit
) {
    Column {
        Text("Personal Information", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Let's start with your basic details.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            isError = email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
            supportingText = {
                if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Text("Invalid email format", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
fun ExpertiseStep(
    selectedExpertise: String,
    onExpertiseSelected: (String) -> Unit,
    options: List<ExpertiseOption>,
    themeOrange: Color
) {
    Column {
        Text("What is your expertise?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Select your primary industry to help us personalize your career recommendations and skill assessments.",
            color = Color.Gray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(modifier = Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { option ->
                val isSelected = selectedExpertise == option.title
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            onClick = { onExpertiseSelected(option.title) },
                            role = Role.RadioButton
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFFFF3E0).copy(alpha = 0.5f) else Color.White
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) themeOrange else Color(0xFFEEEEEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = themeOrange)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(option.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF212121))
                            Text(option.description, fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsStep(
    selectedSkills: Set<String>,
    onSkillToggle: (String) -> Unit,
    relatedSkills: List<String>,
    otherSkills: List<String>,
    themeOrange: Color
) {
    Column {
        Text("Skills Selection", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Select skills that match your expertise and interests.", color = Color.Gray, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(24.dp))

        if (relatedSkills.isNotEmpty()) {
            Text("Recommended for your Expertise", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = themeOrange)
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                relatedSkills.forEach { skill ->
                    SkillSurface(skill, selectedSkills.contains(skill), onSkillToggle, themeOrange)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text("Other Skills", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            otherSkills.forEach { skill ->
                SkillSurface(skill, selectedSkills.contains(skill), onSkillToggle, themeOrange)
            }
        }
    }
}

@Composable
fun SkillSurface(
    skill: String,
    isSelected: Boolean,
    onSkillToggle: (String) -> Unit,
    themeOrange: Color
) {
    Surface(
        onClick = { onSkillToggle(skill) },
        shape = CircleShape,
        color = if (isSelected) themeOrange else Color.White,
        border = BorderStroke(
            1.dp,
            if (isSelected) themeOrange else Color(0xFFEEEEEE)
        ),
    ) {
        Text(
            text = skill,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF424242)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    TaclobanServeTheme {
        SignUpScreen(onSignUpSuccess = {}, onNavigateToLogin = {})
    }
}
