package com.anlarsinsoftware.girisimkolay.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.automirrored.outlined.BrandingWatermark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.ErrorColor
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPreferencesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToDeleteAccount: () -> Unit,
    onLogout: () -> Unit
) {
    var pushNotificationsEnabled by remember { mutableStateOf(true) }
    var emailReportsEnabled by remember { mutableStateOf(true) }
    var expertMessageAlertsEnabled by remember { mutableStateOf(false) }
    var brandingEnabled by remember { mutableStateOf(true) }
    var cloudBackupEnabled by remember { mutableStateOf(true) }
    var defaultFormat by remember { mutableStateOf("PDF") }
    var isFormatDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar", fontWeight = FontWeight.Bold, color = NavyPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = NavyPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Save, contentDescription = "Kaydet", tint = NavyPrimary)
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
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Hesap Bölümü ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "HESAP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Default.Person,
                            title = "Profil Ayarları",
                            onClick = {}
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            icon = Icons.Default.Security,
                            title = "Güvenlik",
                            onClick = {}
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            icon = Icons.Default.WorkspacePremium,
                            title = "Abonelik Planı",
                            subtitle = "Mevcut: Premium",
                            onClick = onNavigateToSubscription
                        )
                    }
                }
            }

            // --- Bildirimler Bölümü ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BİLDİRİMLER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        SettingsSwitchRow(
                            icon = Icons.Default.Notifications,
                            title = "Anlık Bildirimler",
                            checked = pushNotificationsEnabled,
                            onCheckedChange = { pushNotificationsEnabled = it }
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsSwitchRow(
                            icon = Icons.Default.Mail,
                            title = "E-posta Raporları",
                            checked = emailReportsEnabled,
                            onCheckedChange = { emailReportsEnabled = it }
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsSwitchRow(
                            icon = Icons.Default.Forum,
                            title = "Uzman Mesajı Uyarıları",
                            checked = expertMessageAlertsEnabled,
                            onCheckedChange = { expertMessageAlertsEnabled = it }
                        )
                    }
                }
            }

            // --- Dışa Aktarma Tercihleri Bölümü ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DIŞA AKTARMA TERCİHLERİ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        // Dropdown format selector
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Varsayılan Format", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box {
                                    TextButton(
                                        onClick = { isFormatDropdownExpanded = true },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(text = defaultFormat, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    }
                                    DropdownMenu(
                                        expanded = isFormatDropdownExpanded,
                                        onDismissRequest = { isFormatDropdownExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("PDF") },
                                            onClick = {
                                                defaultFormat = "PDF"
                                                isFormatDropdownExpanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Excel (.xlsx)") },
                                            onClick = {
                                                defaultFormat = "Excel (.xlsx)"
                                                isFormatDropdownExpanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("JSON") },
                                            onClick = {
                                                defaultFormat = "JSON"
                                                isFormatDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        HorizontalDivider(color = Color(0xFFF0EFF1), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsSwitchRow(
                            icon = Icons.AutoMirrored.Outlined.BrandingWatermark,
                            title = "Rapor Markalama",
                            subtitle = "Dışa aktarmalarda girişim logosu/adını dahil et",
                            checked = brandingEnabled,
                            onCheckedChange = { brandingEnabled = it }
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsSwitchRow(
                            icon = Icons.Default.CloudSync,
                            title = "Buluta Otomatik Yedekleme",
                            checked = cloudBackupEnabled,
                            onCheckedChange = { cloudBackupEnabled = it }
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            icon = Icons.Default.PrivacyTip,
                            title = "Veri Gizliliği",
                            subtitle = "Raporlarda paylaşılan verileri yönet",
                            onClick = {}
                        )
                    }
                }
            }

            // --- Destek & Yasal Bölümü ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DESTEK & YASAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.AutoMirrored.Filled.Help,
                            title = "Yardım Merkezi",
                            onClick = {}
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            icon = Icons.Default.Policy,
                            title = "Gizlilik Politikası",
                            onClick = {}
                        )
                    }
                }
            }

            // --- Hesap İşlemleri ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToDeleteAccount,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorColor)
                ) {
                    Text(text = "Hesabımı Sil", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Çıkış Yap", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 13.sp, color = Color.Gray)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = "Git", tint = OutlineVariant)
    }
}

@Composable
fun SettingsSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 13.sp, color = Color.Gray)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NavyPrimary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFE4E2E5)
            )
        )
    }
}
