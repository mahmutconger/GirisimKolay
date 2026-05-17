package com.anlarsinsoftware.girisimkolay.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
            Text(
                text = "Hesap Oluştur",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Girişim yolculuğunuza başlayın",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 36.dp)
            )

            // Full name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; viewModel.resetState() },
                label = { Text("Ad Soyad", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF4FC3F7)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; viewModel.resetState() },
                label = { Text("E-posta", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF4FC3F7)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.resetState() },
                label = { Text("Şifre (en az 6 karakter)", color = Color.White.copy(alpha = 0.7f)) },
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
                colors = authFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Error
            AnimatedVisibility(visible = uiState is AuthUiState.Error) {
                Text(
                    text = (uiState as? AuthUiState.Error)?.message ?: "",
                    color = Color(0xFFFF5252),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Register button
            Button(
                onClick = { viewModel.signUp(email, password, fullName) },
                enabled = uiState !is AuthUiState.Loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E676),
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
                    Text("Kayıt Ol ve Başla", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateBack) {
                Text(
                    "← Giriş ekranına dön",
                    color = Color(0xFF4FC3F7),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF4FC3F7),
    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color(0xFF4FC3F7)
)
