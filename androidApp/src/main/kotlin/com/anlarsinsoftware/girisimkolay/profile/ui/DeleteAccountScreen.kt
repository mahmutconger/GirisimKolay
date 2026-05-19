package com.anlarsinsoftware.girisimkolay.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.ErrorColor
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(
    onNavigateBack: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    var isConfirmed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Settings", fontWeight = FontWeight.Bold, color = NavyPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = NavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBF9FB))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Warning Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFDAD6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Uyarı",
                    tint = ErrorColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Warning Title & Subtitle
            Text(
                text = "Hesabınızı silmek istediğinizden emin misiniz?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hesabımı Sil",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ErrorColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Consequences List Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ConsequenceRow(
                        icon = Icons.Default.DeleteForever,
                        text = "Tüm kayıtlı raporlarınız ve analizleriniz kalıcı olarak silinecektir."
                    )
                    HorizontalDivider(color = Color(0xFFF0EFF1))
                    ConsequenceRow(
                        icon = Icons.Default.HistoryToggleOff,
                        text = "AI asistan geçmişinize erişiminizi kaybedeceksiniz."
                    )
                    HorizontalDivider(color = Color(0xFFF0EFF1))
                    ConsequenceRow(
                        icon = Icons.Default.VisibilityOff,
                        text = "Uzman mesajlarınız ve topluluk paylaşımlarınız anonimleştirilecektir."
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirmation Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isConfirmed = !isConfirmed }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = isConfirmed,
                    onCheckedChange = { isConfirmed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ErrorColor,
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verilerimin kalıcı olarak silineceğini anlıyorum.",
                    fontSize = 14.sp,
                    color = NavyPrimary,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.weight(1.2f))

            // Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onConfirmDelete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isConfirmed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorColor,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFEFEDF0),
                        disabledContentColor = OutlineVariant
                    )
                ) {
                    Text(text = "Devam Et", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyPrimary)
                ) {
                    Text(text = "Vazgeç", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ConsequenceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ErrorColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = NavyPrimary,
            lineHeight = 20.sp
        )
    }
}
