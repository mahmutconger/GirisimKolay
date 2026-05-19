package com.anlarsinsoftware.girisimkolay.chat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertAdviceDetailScreen(
    adviceId: String,
    onNavigateBack: () -> Unit,
    onNavigateToRelatedQuestions: () -> Unit,
    onNavigateToExpertProfile: (String) -> Unit,
    onAskExpert: (String) -> Unit
) {
    val expertId = if (adviceId == "2") "selin" else "kemal"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Uzman Görüşü", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = NavyPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Kaydet", tint = NavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFBF9FB))
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp) // extra padding for bottom action buttons
            ) {
                // Expert Profile Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToExpertProfile(expertId) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                AsyncImage(
                                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDYZGEek9N6k7LocAcnjuFlQUaq9hl_WdH2BP_Eapf6kHvMQcWbPsLVzSpNGM4gOtF3cEIm01EogVbkgaJFjAGhQS3i-QcDBG1ZW8TKqJD-Gncyh9dveDsZgz5Tj9sL9hALlEa6_n2kIxo0QaHl9OO9u42pCgvizxosCjGLKf9TC86q3Dko3HDwYeNw0nnfjQKsbq6VwsfTBs7AktIWRxXuhsl-Wd3wIJB_L0kK4ynBBXbrmEBc1dp0aNj3G-Dm4PW-v9ROnKbYRsk",
                                    contentDescription = "Kemal D.",
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Kemal D.", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Verified, 
                                            contentDescription = "Onaylı", 
                                            tint = EmeraldSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text("Onaylı Mali Müşavir", fontSize = 13.sp, color = Color.Gray)
                                }
                            }
                            Button(
                                onClick = { onNavigateToExpertProfile(expertId) },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Profili Gör", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Original Question Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(110.dp)
                                    .background(NavyPrimary)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("SENİN SORUN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Genç Girişimci İstisnası Şartları", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Merhaba, 28 yaşındayım ve ilk defa şahıs şirketi kurmak istiyorum. Genç girişimci istisnasından faydalanmak için hangi şartları sağlamam gerekiyor ve bu destek tam olarak neleri kapsıyor?",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // The Answer Content
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Merhaba, yeni girişiminiz şimdiden hayırlı olsun! Genç Girişimci İstisnası, devletin yeni iş kuran gençlere sunduğu oldukça avantajlı bir teşviktir. Şartları ve avantajları detaylıca aşağıda bulabilirsiniz.",
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = NavyPrimary
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, tint = EmeraldSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Yararlanma Şartları", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            val conditions = listOf(
                                "İşe başlama tarihiniz itibariyle 29 yaşını doldurmamış olmak.",
                                "İlk defa gelir vergisi mükellefi olmak.",
                                "İşe başlamanın kanuni süresi içinde (10 gün) bildirilmiş olması.",
                                "Kendi işinde bilfiil çalışılması veya işin kendisi tarafından sevk ve idare edilmesi.",
                                "Ortaklık halinde tüm ortakların şartları taşıması."
                            )
                            
                            conditions.forEach { condition ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldSecondary,
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(condition, fontSize = 13.sp, lineHeight = 18.sp, color = Color.DarkGray)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = EmeraldSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sağlanan Avantajlar", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Advantage 1
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceContainer, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text("1. Gelir Vergisi İstisnası (3 Yıl)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Üç vergilendirme dönemi boyunca elde edilen kazancın 2024 yılı için 230.000 TL'ye kadarı gelir vergisinden istisnadır.",
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Advantage 2
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceContainer, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text("2. Bağ-Kur Desteği (1 Yıl)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "1 yıl boyunca 4/b (Bağ-Kur) primleri Hazine tarafından karşılanmaktadır.",
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Not: Şirket kurulum işlemlerinizde mali müşaviriniz ile bu süreci en başından planlamanız hak kaybı yaşamamanız için kritiktir.",
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Related Questions
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "İLGİLİ SORULAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val relatedQuestionsList = listOf(
                            "Şahıs şirketi mi, Limited şirket mi?",
                            "KOSGEB desteği nasıl alınır?"
                        )
                        relatedQuestionsList.forEach { question ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onNavigateToRelatedQuestions() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(question, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // Attached Documents
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "EKLİ DOSYALAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFFFDAD6), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color(0xFFBA1A1A))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("GİB Rehberi.pdf", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                                    Text("2.4 MB", fontSize = 11.sp, color = Color.Gray)
                                }
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.Download, contentDescription = "Download", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Sticky Action Bar (Pills style)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                tonalElevation = 8.dp,
                color = Color.White.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rate Button
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceContainerLow,
                            contentColor = NavyPrimary
                        ),
                        shape = RoundedCornerShape(99.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Değerlendir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Ask expert Button (Filled primary)
                    Button(
                        onClick = { onAskExpert("financial") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(99.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ek Soru Sor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Share Button
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceContainerLow,
                            contentColor = NavyPrimary
                        ),
                        shape = RoundedCornerShape(99.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Paylaş", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
