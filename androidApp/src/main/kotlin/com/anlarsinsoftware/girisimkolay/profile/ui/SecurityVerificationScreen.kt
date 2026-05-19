package com.anlarsinsoftware.girisimkolay.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.ErrorColor
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityVerificationScreen(
    onNavigateBack: () -> Unit,
    onDeleteConfirmed: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF9FB))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Lock Clock Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFDAD6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LockClock,
                contentDescription = "Güvenlik",
                tint = ErrorColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Header Title
        Text(
            text = "Güvenlik Doğrulaması",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "İşlemi tamamlamak için lütfen şifrenizi girin.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Password Input
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "ŞİFRE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••", color = OutlineVariant) },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Şifre Göster",
                            tint = Color.Gray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary,
                    unfocusedBorderColor = OutlineVariant,
                    focusedContainerColor = Color(0xFFF5F3F6),
                    unfocusedContainerColor = Color(0xFFF5F3F6)
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Warning Callout Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFFFDAD6), RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDAD6).copy(alpha = 0.3f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ErrorColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Bu işlem geri alınamaz. Hesabınız ve tüm verileriniz kalıcı olarak silinecektir.",
                    fontSize = 13.sp,
                    color = ErrorColor,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.2f))

        // Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { if (password.isNotEmpty()) onDeleteConfirmed(password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                enabled = password.isNotEmpty()
            ) {
                Text(text = "Hesabımı Kalıcı Olarak Sil", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Text(text = "İptal", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
