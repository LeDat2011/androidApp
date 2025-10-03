package com.example.composeapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.R
import com.example.composeapp.ui.design.spacing
import com.example.composeapp.ui.design.shapes
import com.example.composeapp.ui.design.elevation
import com.example.composeapp.ui.design.components
import com.example.composeapp.ui.design.ButtonStyles
import com.example.composeapp.ui.design.CardStyles
import com.example.composeapp.ui.design.AppGradients
import com.example.composeapp.viewmodels.AuthViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val authViewModel: AuthViewModel = viewModel()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Design System
    val spacing = spacing()
    val shapes = shapes()
    val elevation = elevation()
    val components = components()
    
    // Observe auth state
    val authState by authViewModel.authState.collectAsState()
    
    // Process auth state
    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Success -> {
                isLoading = false
                onLoginSuccess()
            }
            is AuthViewModel.AuthState.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is AuthViewModel.AuthState.Error -> {
                isLoading = false
                errorMessage = (authState as AuthViewModel.AuthState.Error).message
            }
            else -> {
                isLoading = false
            }
        }
    }
    
    // Check if user is already signed in
    val currentUser by authViewModel.currentUser.collectAsState()
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onLoginSuccess()
        }
    }
    
    // Animation for logo
    val scale by animateFloatAsState(
        targetValue = if (isLoading) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo and Title
            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(components.avatarSize)
                    .clip(shapes.circular)
                    .background(
                        brush = Brush.linearGradient(
                            colors = AppGradients.primaryGradient
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // App logo
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "App Logo",
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(spacing.lg))
            
            Text(
                text = "Ứng dụng học tiếng Nhật",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Đăng nhập để tiếp tục",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(spacing.xxl))
            
            // Error message
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                errorMessage?.let {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = spacing.md),
                        shape = shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = elevation.low)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(spacing.md)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(components.iconSize)
                            )
                            
                            Spacer(modifier = Modifier.width(spacing.md))
                            
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(components.textFieldHeight),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(components.iconSize)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                shape = shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(components.textFieldHeight),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(components.iconSize)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(components.iconSize)
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            authViewModel.signIn(email, password)
                        } else {
                            errorMessage = "Vui lòng nhập đầy đủ email và mật khẩu"
                        }
                    }
                ),
                shape = shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            // "Quên mật khẩu" Text Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = { /* Handle forgot password */ }) {
                    Text(
                        text = "Quên mật khẩu?",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.lg))
            
            // Login button
            Button(
                onClick = {
                    keyboardController?.hide()
                    authViewModel.signIn(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(components.buttonHeight),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                shape = shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(components.iconSize),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Đăng nhập")
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.md))
            
            // Anonymous login button
            OutlinedButton(
                onClick = {
                    keyboardController?.hide()
                    authViewModel.signInAnonymously()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(components.buttonHeight),
                enabled = !isLoading,
                shape = shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(components.iconSize),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Tiếp tục với tư cách khách")
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.lg))
            
            // Register link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chưa có tài khoản? ",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text("Đăng ký ngay")
                }
            }
        }
    }
} 