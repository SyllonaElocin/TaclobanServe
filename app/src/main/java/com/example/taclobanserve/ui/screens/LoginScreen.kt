package com.example.taclobanserve.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taclobanserve.TaclobanUser
import com.example.taclobanserve.ui.theme.TaclobanServeTheme

@Composable
fun LoginScreen(
    users: List<TaclobanUser>,
    onLoginSuccess: (TaclobanUser?, Boolean) -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val themeOrange = Color(0xFFF4511E)
    val lightBackground = Color(0xFFF8F9FA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color.White)
                )
            )
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(themeOrange),
            contentAlignment = Alignment.Center
        ) {
            Text("🤝❤️", fontSize = 32.sp) // Placeholder for the heart-hand icon
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TaclobanServe",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Text(
            text = "Empowering the heart of Leyte",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF757575)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Role Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(4.dp)
                ) {
                    TabItem(
                        text = "Volunteer",
                        isSelected = !isAdmin,
                        onClick = { isAdmin = false },
                        modifier = Modifier.weight(1f),
                        activeColor = themeOrange
                    )
                    TabItem(
                        text = "Org Admin",
                        isSelected = isAdmin,
                        onClick = { isAdmin = true },
                        modifier = Modifier.weight(1f),
                        activeColor = themeOrange
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Email Address",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            if (errorMessage == "Invalid email format") errorMessage = null
                        },
                        placeholder = { Text("name@example.com", color = Color.LightGray) },
                        leadingIcon = { Text("📧") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = themeOrange
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = errorMessage == "Invalid email format"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Password",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )
                        Text(
                            "Forgot?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeOrange,
                            modifier = Modifier.clickable { }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••", color = Color.LightGray) },
                        leadingIcon = { Text("🔒") },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "👁️" else "👁️‍🗨️")
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = themeOrange
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Remember Me
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = themeOrange)
                    )
                    Text(
                        "Remember me for 30 days",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Sign In Button
                Button(
                    onClick = { 
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            errorMessage = "Invalid email format"
                        } else if (isAdmin) {
                            if (email == "admin@taclobanserve.ph" && password == "admin123") {
                                onLoginSuccess(null, true)
                            } else {
                                errorMessage = "Invalid Admin Credentials"
                            }
                        } else {
                            val matchedUser = users.find { it.email.lowercase() == email.lowercase().trim() }
                            if (matchedUser != null && password == matchedUser.password) {
                                onLoginSuccess(matchedUser, false)
                            } else {
                                errorMessage = "User not found or incorrect password"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("→", fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                    Text(
                        "OR CONTINUE WITH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Google Login
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("G", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Blue) // Placeholder for Google Icon
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Google", color = Color(0xFF424242), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Footer
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .clickable { onNavigateToSignUp() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account? ", color = Color(0xFF757575), fontSize = 14.sp)
            Text(
                "Sign up for free",
                color = themeOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) activeColor else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TaclobanServeTheme {
        LoginScreen(users = emptyList(), onLoginSuccess = { _, _ -> }, onNavigateToSignUp = {})
    }
}
