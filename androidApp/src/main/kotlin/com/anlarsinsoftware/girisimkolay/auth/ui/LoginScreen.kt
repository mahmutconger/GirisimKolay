package com.anlarsinsoftware.girisimkolay.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.auth.viewmodel.AuthUiState
import com.anlarsinsoftware.girisimkolay.auth.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Auth state change is handled by NavHost observer — no manual navigation here
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            // Error message displayed inline; reset after short delay
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A1628), Color(0xFF1A237E))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand
            Icon(
                imageVector = Icons.Default.Email, // placeholder icon
                contentDescription = null,
                tint = Color(0xFF4FC3F7),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "GirişimKolay",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Yapay Zeka Destekli Girişimcilik",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; viewModel.resetState() },
                label = { Text("E-posta", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF4FC3F7)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4FC3F7),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.resetState() },
                label = { Text("Şifre", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF4FC3F7)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null, tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4FC3F7),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Error message
            AnimatedVisibility(visible = uiState is AuthUiState.Error) {
                Text(
                    text = (uiState as? AuthUiState.Error)?.message ?: "",
                    color = Color(0xFFFF5252),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login button
            Button(
                onClick = { viewModel.signIn(email, password) },
                enabled = uiState !is AuthUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FC3F7),
                    contentColor = Color(0xFF0A1628)
                )
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color(0xFF0A1628),
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Giriş Yap", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigate to register
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Hesabınız yok mu? Kayıt Olun →",
                    color = Color(0xFF4FC3F7),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
