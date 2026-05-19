package com.anlarsinsoftware.girisimkolay.analytics.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketAnalysisDetailScreen(
    onNavigateBack: () -> Unit
) {
    var expandedAccordionIndex by remember { mutableStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pazar Analizi 2024", fontWeight = FontWeight.Bold, color = NavyPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = NavyPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Daha fazla", tint = NavyPrimary)
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
            // --- Header Report Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFD7E2FF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Assessment, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(14.dp))
                                Text("Sektör Raporu", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            }
                        }
                        Text("12 Eki 2023 • 2.4 MB", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Pazar Analizi 2024", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Türkiye teknoloji ve SaaS sektörü için kapsamlı yıllık değerlendirme ve büyüme projeksiyonları.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PDF İndir", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NavyPrimary)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Paylaş", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- Executive Summary Section ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Yönetici Özeti", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "2024 yılı itibarıyla Türkiye pazarında dijital dönüşüm hız kazanmaya devam etmektedir. Özellikle B2B SaaS çözümlerine olan talep %24 oranında artış göstermiş olup, kobilerin bulut tabanlı sistemlere adaptasyonu ivmelenmiştir. Bu rapor, pazarın mevcut durumunu, anahtar oyuncuları ve önümüzdeki 12 ay için stratejik fırsatları özetlemektedir.",
                        fontSize = 14.sp,
                        color = NavyPrimary,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // --- Key Findings Grid ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Temel Bulgular", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FindingCard(
                        icon = Icons.Default.AttachMoney,
                        label = "PAZAR BÜYÜKLÜĞÜ",
                        value = "$1.2B",
                        modifier = Modifier.weight(1f)
                    )
                    FindingCard(
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        label = "YILLIK BÜYÜME",
                        value = "%12.4",
                        isTrend = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FindingCard(
                        icon = Icons.Default.Groups,
                        label = "RAKİP SAYISI",
                        value = "Orta (15+)",
                        modifier = Modifier.weight(1f)
                    )
                    FindingCard(
                        icon = Icons.Default.Bolt,
                        label = "FIRSAT SKORU",
                        value = "8.5/10",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- Expandable Insights Accordion ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Detaylı Analiz", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        AccordionRow(
                            index = 0,
                            icon = Icons.Default.Psychology,
                            title = "Tüketici Davranışları",
                            expandedIndex = expandedAccordionIndex,
                            onToggle = { expandedAccordionIndex = it },
                            content = "Müşterilerin karar alma süreçlerinde dijitalleşme ve entegrasyon hızı öncelikli hale gelmiştir. Ürünlerin kullanım kolaylığı ve kullanıcı dostu arayüzler tercih sebeplerinin başında yer almaktadır."
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        AccordionRow(
                            index = 1,
                            icon = Icons.Default.Devices,
                            title = "Teknolojik Trendler",
                            expandedIndex = expandedAccordionIndex,
                            onToggle = { expandedAccordionIndex = it },
                            content = "Bulut teknolojileri, yapay zeka tabanlı veri analitiği sistemleri ve hibrit çalışma modellerini destekleyen araçlar sektör büyümesini şekillendiren temel faktörlerdir."
                        )
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        AccordionRow(
                            index = 2,
                            icon = Icons.Default.Gavel,
                            title = "Regülasyon Değişiklikleri",
                            expandedIndex = expandedAccordionIndex,
                            onToggle = { expandedAccordionIndex = it },
                            content = "KVKK ve veri güvenliği standartlarına uyum, finansal teknolojilerdeki yeni tebliğler ve e-belge zorunlulukları pazarın hukuki çerçevesini belirlemektedir."
                        )
                    }
                }
            }

            // --- Recommended Actions Checklist ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Önerilen Adımlar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionRow(
                            boldText = "Ar-Ge yatırımlarını artır:",
                            text = " Özellikle yapay zeka entegrasyonu alanında rekabet avantajı sağlamak için bütçe ayrılmalı."
                        )
                        ActionRow(
                            boldText = "Yeni pazarlara açıl:",
                            text = " İç pazardaki doygunluk öncesinde MENA bölgesine yönelik pilot çalışmalar başlatılmalı."
                        )
                        ActionRow(
                            boldText = "Fiyatlandırma stratejisi:",
                            text = " Artan maliyetler göz önünde bulundurularak abonelik paketleri optimize edilmeli."
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FindingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isTrend: Boolean = false
) {
    Card(
        modifier = modifier.border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isTrend) Color(0xFFC2F0D8) else Color(0xFFD7E2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isTrend) EmeraldSecondary else NavyPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = if (isTrend) EmeraldSecondary else NavyPrimary
                )
            }
        }
    }
}

@Composable
fun AccordionRow(
    index: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    expandedIndex: Int,
    onToggle: (Int) -> Unit,
    content: String
) {
    val isExpanded = expandedIndex == index

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(if (isExpanded) -1 else index) }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color.Gray
            )
        }
        
        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = content,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun ActionRow(
    boldText: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = EmeraldSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = androidx.compose.ui.text.buildAnnotatedString {
                append(boldText)
                addStyle(
                    style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold),
                    start = 0,
                    end = boldText.length
                )
                append(text)
            },
            fontSize = 14.sp,
            color = NavyPrimary,
            lineHeight = 20.sp
        )
    }
}
