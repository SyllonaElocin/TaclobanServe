package com.example.taclobanserve.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taclobanserve.TaclobanEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditEventScreen(
    event: TaclobanEvent,
    onSave: (TaclobanEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf(event.title) }
    var description by remember { mutableStateOf(event.description) }
    var hours by remember { mutableStateOf(event.hours) }
    var volunteers by remember { mutableStateOf(event.volunteers) }
    var selectedProfessions by remember { mutableStateOf(event.professions.toSet()) }
    var selectedSkills by remember { mutableStateOf(event.tags.toSet()) }
    var selectedImageUri by remember { mutableStateOf(event.imageUri) }
    
    val themeOrange = Color(0xFFF4511E)
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
    val taclobanAreas = listOf(
        TaclobanArea("Downtown (City Hall Area)", "11.2433, 125.0012"),
        TaclobanArea("San Jose (Airport District)", "11.2268, 125.0275"),
        TaclobanArea("Abucay (New Bus Terminal)", "11.2445, 124.9812"),
        TaclobanArea("Sagkahan (Astrodome)", "11.2312, 125.0050"),
        TaclobanArea("Marasbaras (Robinsons Area)", "11.2155, 125.0035"),
        TaclobanArea("V&G Subdivision", "11.2290, 124.9780"),
        TaclobanArea("Caibaan", "11.2100, 124.9850"),
        TaclobanArea("Utap", "11.2380, 124.9890")
    )
    
    var expanded by remember { mutableStateOf(false) }
    var selectedArea by remember { mutableStateOf(taclobanAreas.find { it.name == event.area } ?: taclobanAreas[0]) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri?.toString() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Event Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .background(Color(0xFFFAFAFA))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("Update Mission Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = themeOrange)
            Spacer(Modifier.height(24.dp))

            // Image Selector
            Text("Update Cover Image", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (selectedImageUri == null) {
                        Text("📷 Tap to add photo", fontSize = 14.sp, color = Color.Gray)
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(themeOrange.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Text("✅ Image Attached", color = themeOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project Title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(10.dp))
            
            Spacer(Modifier.height(16.dp))
            Text("Target Professions", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                professionOptions.forEach { prof ->
                    val isSelected = selectedProfessions.contains(prof)
                    FilterChip(selected = isSelected, onClick = { selectedProfessions = if (isSelected) selectedProfessions - prof else selectedProfessions + prof }, label = { Text(prof) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Required Skills", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skillOptions.forEach { skill ->
                    val isSelected = selectedSkills.contains(skill)
                    FilterChip(selected = isSelected, onClick = { selectedSkills = if (isSelected) selectedSkills - skill else selectedSkills + skill }, label = { Text(skill) })
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Hours / Week") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = volunteers, onValueChange = { volunteers = it }, label = { Text("Spots") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            Spacer(Modifier.height(16.dp))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = selectedArea.name, onValueChange = {}, readOnly = true, label = { Text("Geofence Area") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, colors = OutlinedTextFieldDefaults.colors(), modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    taclobanAreas.forEach { area -> DropdownMenuItem(text = { Text(area.name) }, onClick = { selectedArea = area; expanded = false }) }
                }
            }

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { 
                    onSave(event.copy(
                        title = title, 
                        description = description, 
                        hours = hours, 
                        volunteers = volunteers, 
                        area = selectedArea.name, 
                        professions = selectedProfessions.toList(),
                        tags = selectedSkills.toList(),
                        imageUri = selectedImageUri
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeOrange),
                shape = RoundedCornerShape(12.dp),
                enabled = title.isNotBlank()
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            TextButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                Text("Discard Changes", color = Color.Gray)
            }
        }
    }
}
