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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Verified
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
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionManagementScreen(
    onNavigateBack: () -> Unit
) {
    var isAnnualBilling by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Abonelik Yönetimi", fontWeight = FontWeight.Bold, color = NavyPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = NavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFFBF9FB))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .padding(bottom = 140.dp), // Space for sticky footer
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // --- Current Plan Summary ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Mevcut Plan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB6C7EB),
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Premium",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(EmeraldSecondary)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Text("Aktif", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFF374765).copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Aylık Tutar", fontSize = 14.sp, color = Color(0xFFB6C7EB))
                            Text("₺599", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sonraki Fatura Tarihi", fontSize = 14.sp, color = Color(0xFFB6C7EB))
                            Text("15 Temmuz 2024", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // --- Billing Toggle ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0xFFE9E7EA))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (!isAnnualBilling) Color.White else Color.Transparent)
                            .clickable { isAnnualBilling = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aylık",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isAnnualBilling) NavyPrimary else Color.Gray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (isAnnualBilling) Color.White else Color.Transparent)
                            .clickable { isAnnualBilling = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Yıllık",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAnnualBilling) NavyPrimary else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF6CF8BB))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("2 ay bedava", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF002113))
                            }
                        }
                    }
                }

                // --- Plan Comparison List ---
                Text("Planları Karşılaştır", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)

                // Free Plan Card
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Ücretsiz", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Text("₺0/ay", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Temel özelliklere erişim ve topluluk desteği.", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        BulletRow("Temel erişim")
                        BulletRow("Günde 3 yapay zeka sorgusu")
                        BulletRow("Topluluk forumu")
                    }
                }

                // Pro Plan Card
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Pro", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Text(if (isAnnualBilling) "₺249/ay" else "₺299/ay", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Büyüyen girişimler için gelişmiş araçlar.", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        BulletRow("Sınırsız yapay zeka sorgusu")
                        BulletRow("Gelişmiş raporlar")
                        BulletRow("Uzmana Soru-Cevap (5/ay)")
                    }
                }

                // Premium Plan Card (Highlighted)
                Card(
                    modifier = Modifier.fillMaxWidth().border(2.dp, NavyPrimary, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Premium", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFD7E2FF))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("AKTİF PLAN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                }
                            }
                            Text(if (isAnnualBilling) "₺499/ay" else "₺599/ay", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Liderler için özel destek ve kişiselleştirilmiş strateji.", fontSize = 13.sp, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Tüm Pro özellikleri ve ekstra:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                BulletRow("Öncelikli uzman desteği", isCheck = true)
                                BulletRow("Kişiselleştirilmiş yol haritası", isCheck = true)
                                BulletRow("Birebir danışmanlık görüşmesi", isCheck = true)
                            }
                        }
                    }
                }

                // --- Feature Matrix Table ---
                Text("Tüm Özellikleri Karşılaştır", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLow, RoundedCornerShape(8.dp))
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Özellikler", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary, modifier = Modifier.weight(1.5f))
                            Text("Free", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Pro", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Premium", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        MatrixRow("AI Asistan", "3/gün", "Sınırsız", "Sınırsız", true)
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        MatrixRow("Pazar Raporu", "Temel", "Gelişmiş", "Özel", true)
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        MatrixRow("Uzman Sor", "-", "5/ay", "Öncelikli", true)
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        MatrixRow("Yol Haritası", "-", "Standart", "Özel", true)
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        MatrixRow("Birebir", "-", "-", "✓", true)
                    }
                }
            }

            // --- Sticky Footer Actions ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(text = "Planı Değiştir", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyPrimary)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Faturalandırma Geçmişi", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BulletRow(text: String, isCheck: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (isCheck) Color(0xFFC2F0D8) else Color(0xFFE9E7EA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isCheck) EmeraldSecondary else Color.Gray,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 13.sp, color = NavyPrimary)
    }
}

@Composable
fun MatrixRow(
    feature: String,
    freeVal: String,
    proVal: String,
    premVal: String,
    highlightPrem: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(feature, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary, modifier = Modifier.weight(1.5f))
        Text(freeVal, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(proVal, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(if (highlightPrem) Color(0xFFD7E2FF).copy(alpha = 0.5f) else Color.Transparent)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = premVal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (premVal == "✓") EmeraldSecondary else NavyPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
