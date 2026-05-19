package com.anlarsinsoftware.girisimkolay.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OnEmeraldSecondaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainer

import androidx.compose.material.icons.filled.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GirisimciProfileScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToMarketAnalysis: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                        ) {
                            AsyncImage(
                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuANWfKL8Jno7qX0HivVfh5PJoXljkh7h1w-KRBd3BVNikowMyO8oRYrUvozlnWB29c5QxVAiu01t3fwFv7yOp9IgenaBqMufq1hPdDelq9CunjXAQFBHYqDrsGdLMx7vf2Pl_YvA9NHPlLlGoiAqKfTZjym80L62g8kDi5Uv7XzY-3UtlPqUl-KzPz3LiBGoaKsTpfXMnmTOUOonM5droovKoN9b2TKGAfm8-asT666MJgOSygFr6VxfJAdgoAzebH0ELNMbLqxHYE",
                                contentDescription = "Profil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "GirişimKolay",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Bildirimler",
                            tint = NavyPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Ayarlar",
                            tint = NavyPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBF9FB))
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // Profile Card Row
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuB8hEbqm-eHsh7gAjpPjj8rJXynGc0JHNR74IiP039qcvZdEzit8GZJO8ZuxD0f64a8quIj3-vo69KFrwfSphUBmDUqPDEQEQudTYmYN4OyAyyXdnsCXRbSx5K9f5Ws-uyI-ZMWDcJAdfo2bjru54Ij5DmAebW9yJNZ_BOEYFebOK9jS2K7mE5l-ebAIPg6u1uhUdcbVYa0Y8phBwHr44bmZQYMARSzuPA__6aOU1mZG8eBg4SXv66P067yhPDKocq8y8um9o1IkkQ",
                            contentDescription = "Ahmet Yılmaz",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Ahmet Yılmaz",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kurucu, EcoTech Solutions",
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = EmeraldSecondaryContainer.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(99.dp),
                            border = BorderStroke(1.dp, EmeraldSecondaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = EmeraldSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Onaylı Hesap",
                                    color = EmeraldSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val stats = listOf(
                        Triple(Icons.Default.RocketLaunch, "2", "Aktif Projeler"),
                        Triple(Icons.Default.Description, "14", "Raporlar"),
                        Triple(Icons.Default.Forum, "5", "Görüşmeler")
                    )

                    stats.forEach { (icon, value, label) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = value,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // My Startup Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Girişimim",
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 18.sp
                            )
                            IconButton(onClick = {}) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Düzenle",
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFBF9FB), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, OutlineVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "EcoTech Solutions",
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    fontSize = 16.sp
                                )
                                Surface(
                                    color = NavyPrimaryContainer,
                                    shape = RoundedCornerShape(99.dp)
                                ) {
                                    Text(
                                        "Tohum Aşaması",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                "Sürdürülebilir enerji yönetimi için yapay zeka destekli IoT cihazları üretiyoruz. Hedefimiz, KOBİ'lerin enerji tüketimlerini optimize ederek karbon ayak izlerini %30 azaltmalarını sağlamak.",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                lineHeight = 18.sp
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("SaaS", "IoT", "Yeşil Teknoloji").forEach { tag ->
                                    Surface(
                                        color = EmeraldSecondaryContainer.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(99.dp)
                                    ) {
                                        Text(
                                            tag,
                                            color = EmeraldSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Saved Reports Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Kayıtlı Raporlar",
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 18.sp
                            )
                            TextButton(
                                onClick = onNavigateToReports,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Tümü", color = NavyPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = NavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val reports = listOf(
                            "Pazar Analizi 2024" to "12 Eki 2023 • 2.4 MB",
                            "Vergi Muafiyet Rehberi" to "05 Eki 2023 • 1.1 MB"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            reports.forEachIndexed { index, (title, meta) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (title == "Pazar Analizi 2024") {
                                                onNavigateToMarketAnalysis()
                                            }
                                        }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    NavyPrimary.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.PictureAsPdf,
                                                contentDescription = null,
                                                tint = NavyPrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                title,
                                                fontWeight = FontWeight.Bold,
                                                color = NavyPrimary,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                meta,
                                                color = Color.Gray,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    IconButton(onClick = {}) {
                                        Icon(
                                            Icons.Default.Download,
                                            contentDescription = "İndir",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                if (index < reports.size - 1) {
                                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }

            // Achievement Badges Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Başarı Rozetleri",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val badges = listOf(
                                Triple(Icons.Default.AccountBalance, "Vergi Hazır", true),
                                Triple(Icons.Default.MilitaryTech, "İlk Hibe", true),
                                Triple(Icons.Default.GroupAdd, "İlk Çalışan", false),
                                Triple(Icons.AutoMirrored.Filled.TrendingUp, "Seri A", false)
                            )

                            badges.forEach { (icon, title, unlocked) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (unlocked) EmeraldSecondaryContainer else Color(
                                                    0xFFE4E2E5
                                                )
                                            )
                                            .border(
                                                if (unlocked) BorderStroke(0.dp, Color.Transparent)
                                                else BorderStroke(1.dp, OutlineVariant),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = if (unlocked) OnEmeraldSecondaryContainer else Color.Gray,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        title,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (unlocked) NavyPrimary else Color.Gray,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
