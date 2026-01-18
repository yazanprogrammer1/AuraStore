package com.example.aurastore.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurastore.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    viewModel: com.example.aurastore.ui.viewmodel.LoginViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Handle UI Events (Navigation, Errors)
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is com.example.aurastore.ui.viewmodel.LoginViewModel.UiEvent.NavigateToHome -> {
                    onLoginClick()
                }
                is com.example.aurastore.ui.viewmodel.LoginViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    // Luxury Animated Background
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    // ... (Keep existing background animation logic if possible, or simplified for brevity in replace) ...
    // Note: Re-implementing background logic to ensure context is preserved as replace_file_content replaces the block.

     val colorOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorOffset"
    )

    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    // Luxury Animated Background (Keep existing)
    // ...

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AuraDarkBg,
                            AuraMidnight,
                            Color(0xFF000000)
                        )
                    )
                )
                .padding(padding)
        ) {
            // (Keep background circles - implied, but for replace we focus on Column content)
             // Aesthetic Decorative Circles
            Box(
                modifier = Modifier
                    .offset(x = (-100).dp, y = (-100).dp)
                    .size(300.dp)
                    .blur(80.dp)
                    .background(AuraMidnight.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 100.dp, y = 100.dp)
                    .size(350.dp)
                    .blur(100.dp)
                    .background(AuraGold.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
            )

            // Glassmorphism Card
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .background(AuraSurface.copy(alpha = 0.7f)) // Semi-transparent
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isRegisterMode) "Create Account" else "Welcome Back",
                    color = AuraTextPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRegisterMode) "Join the Aura of luxury" else "Sign in to continue your journey",
                    color = AuraTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Name InputFor Register Mode
                if (isRegisterMode) {
                    AuraTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        icon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Email Input
                AuraTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                AuraTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordToggle = { passwordVisible = !passwordVisible }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action Button
                Button(
                    onClick = { 
                        if (email.isBlank() || password.isBlank()) {
                             android.widget.Toast.makeText(context, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                             return@Button
                        }
                        if (isRegisterMode) {
                             if (name.isBlank()) {
                                 android.widget.Toast.makeText(context, "Please enter your name", android.widget.Toast.LENGTH_SHORT).show()
                                 return@Button
                             }
                             viewModel.onEvent(com.example.aurastore.ui.viewmodel.LoginViewModel.LoginEvent.Register(email, password, name))
                        } else {
                             viewModel.onEvent(com.example.aurastore.ui.viewmodel.LoginViewModel.LoginEvent.Login(email, password))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuraGold,
                        contentColor = AuraMidnight
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    ),
                    enabled = loginState !is com.example.aurastore.common.Resource.Loading
                ) {
                    
                    if (loginState is com.example.aurastore.common.Resource.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AuraMidnight
                        )
                    } else {
                        Text(
                            text = if (isRegisterMode) "REGISTER" else "LOG IN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Button
                TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                    Text(
                        text = if (isRegisterMode) "Already have an account? Log In" else "Don't have an account? Sign Up",
                        color = AuraGold
                    )
                }
            }
        }
    }
}

@Composable
fun AuraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    modifier: Modifier = Modifier,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = AuraTextSecondary) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = AuraGold) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onPasswordToggle) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = AuraTextSecondary
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AuraGold,
            unfocusedBorderColor = AuraTextSecondary.copy(alpha = 0.5f),
            focusedTextColor = AuraTextPrimary,
            unfocusedTextColor = AuraTextPrimary,
            cursorColor = AuraGold
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}
