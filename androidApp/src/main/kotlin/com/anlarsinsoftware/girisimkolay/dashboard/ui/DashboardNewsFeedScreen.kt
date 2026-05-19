package com.anlarsinsoftware.girisimkolay.dashboard.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anlarsinsoftware.girisimkolay.dashboard.viewmodel.DashboardViewModel
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.ErrorColor
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardNewsFeedScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val userStatus by viewModel.userStatus.collectAsState()

    val newsArticles = listOf(
        NewsCardItem(
            category = "Finansman",
            title = "KOSGEB Kadın Girişimci Kredisinde Limit Artışı",
            readTime = "5 dk okuma",
            author = "GirişimKolay Haber",
            timeAgo = "2 saat önce",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBpYqHBe7Yy-kBwN7zNA1KS47Z-pxxMi3LYoVnDfB6HBp77ll8kXRCFKe7VrH7ePq327UsDpPeFBnorwSzmWNAH02s1BPzzIAaPfww2NC_fxvO11R6s__V9BBuNNEjuUscypPhKtkUiXxH5LnYlB2D1wal3Ci3kE7zlbuJHdIxhvZuiwAoRF2SgbvBfUYV0bjYao_ra1w39_oRsGAZ7vMR_jsHSZnn1Lgzd-20Jjm7MWcGlospCO50a5cSm3I4MDYiS4xqSQungCm0",
            progress = 0.6f
        ),
        NewsCardItem(
            category = "Mevzuat",
            title = "2026 Yılı E-Ticaret Vergi Muafiyeti Sınırı Yükseltildi",
            readTime = "4 dk okuma",
            author = "Vergi Dünyası",
            timeAgo = "4 saat önce",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFoKXs-uFg2YP3yvJZ2hobDmpj7SC31KJQkVlMWr8HdJ22Zt25Yyn6kBuMngzxalAoZCxDDwo07oksDc874G8SY-wRBPo4uRoDDJq2yUamo5s0x_xuizpjGFrUaCV7wDJMTy0DLI9wsp0c_kqzDSun0ouF-3tPNSFegxavSJHajL51o2LJMVRxT1xh60FZMCmXdDHjSgdQn-EHsT288DSPUoR2LyR2O7D5EXzekiIv-E1QZh36vxu_ShLwSUu57ohend6-FAoinYA",
            progress = 0.2f
        ),
        NewsCardItem(
            category = "Pazar",
            title = "Türkiye'de E-İhracat Hacmi Rekor Seviyeye Ulaştı",
            readTime = "6 dk okuma",
            author = "Ticaret Analiz",
            timeAgo = "1 gün önce",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDYZGEek9N6k7LocAcnjuFlQUaq9hl_WdH2BP_Eapf6kHvMQcWbPsLVzSpNGM4gOtF3cEIm01EogVbkgaJFjAGhQS3i-QcDBG1ZW8TKqJD-Gncyh9dveDsZgz5Tj9sL9hALlEa6_n2kIxo0QaHl9OO9u42pCgvizxosCjGLKf9TC86q3Dko3HDwYeNw0nnfjQKsbq6VwsfTBs7AktIWRxXuhsl-Wd3wIJB_L0kK4ynBBXbrmEBc1dp0aNj3G-Dm4PW-v9ROnKbYRsk",
            progress = 0.8f
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = ErrorColor, fontWeight = FontWeight.Bold)) {
                                    append("Girişim")
                                }
                                withStyle(style = SpanStyle(color = NavyPrimary, fontWeight = FontWeight.Bold)) {
                                    append("Kolay")
                                }
                            },
                            fontSize = 20.sp
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToProfile() }
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC4eqP3coFOwZn_jkwbntho5uiM37I2_qpIwytnC8Um2wQ4tYEc19-hDUKR3E66oFS8tLlYzb-k3WPthzAj8AzNG5BDOtpPshESlJpY9xnv6JOcsRB5z3fhQX6rjjcR2wkw5WkrvGF2P4lwkjzDx6rJ_x6pQw86YgoygbHcUePFaivZIlAfwlpfIRLT1KIVqcF8UpN75BEl83xBOV3pFhBaCBhS0_-AHwkgAIzjOUJ-twGVkNzvCVc-6kmu-8uDg3uI-E7X7Jx4_iE",
                            contentDescription = "Profil",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", tint = NavyPrimary, modifier = Modifier.size(26.dp))
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ErrorColor)
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            // Welcome Header Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tekrar hoş geldin, ${userStatus?.name ?: "Girişimci"}!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "İşletmenizin bugünkü durumuna göz atın.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Sectoral News Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Sektörel Haberler",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        newsArticles.forEach { article ->
                            DashboardNewsCard(article = article)
                        }
                    }
                }
            }

            // Bento Grid for Calendar and Analytics
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "İşletme Takvimi & Analitik",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    // Bento Item 1: Calendar Widget
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Mayıs 2024", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = EmeraldSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            // Calendar days grid
                            val weekdays = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                weekdays.forEach { day ->
                                    Text(
                                        text = day,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        modifier = Modifier.width(36.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Simplified calendar grid rows
                            val daysRow1 = listOf("20", "21", "22", "23", "24", "25", "26")
                            val daysRow2 = listOf("27", "28", "29", "30", "31", "1", "2")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                daysRow1.forEach { day ->
                                    val isSpecial = day == "26"
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSpecial) Color(0xFFFFDAD6) else Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSpecial) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSpecial) ErrorColor else NavyPrimary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                daysRow2.forEach { day ->
                                    val isSpecial = day == "29"
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSpecial) Color(0xFFE2F6EC) else Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSpecial) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSpecial) EmeraldSecondary else NavyPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = OutlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Upcoming Event List
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ErrorColor))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("May 26 - KDV Beyannamesi (Son Gün)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldSecondary))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("May 29 - KOSGEB Başvuru Bitişi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                }
                            }
                        }
                    }

                    // Bento Item 2: Financial Charts Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Pie Chart Box
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Gelir / Gider", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier.size(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = 0.7f,
                                        modifier = Modifier.fillMaxSize(),
                                        color = EmeraldSecondary,
                                        strokeWidth = 8.dp,
                                        trackColor = Color(0xFFE2F6EC)
                                    )
                                    Text("Net %70", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                }
                            }
                        }

                        // Bar Chart Box
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Görevler", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.height(60.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Box(modifier = Modifier.width(8.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).background(Color.LightGray))
                                    Box(modifier = Modifier.width(8.dp).height(45.dp).clip(RoundedCornerShape(4.dp)).background(Color.LightGray))
                                    Box(modifier = Modifier.width(8.dp).height(30.dp).clip(RoundedCornerShape(4.dp)).background(NavyPrimary))
                                    Box(modifier = Modifier.width(8.dp).height(55.dp).clip(RoundedCornerShape(4.dp)).background(EmeraldSecondary))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("12 Tamamlanan", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // AI Insight Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .offset(x = 20.dp, y = (-20).dp)
                                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                .align(Alignment.TopEnd)
                        )

                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Yapay Zeka İçgörüsü", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Bu ay kaynaklarınızı %15 daha verimli kullandınız. Harika gidiyorsunuz! Sabit maliyetleri kiralama modelleri ile optimize etmeye devam edebilirsiniz.",
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardNewsCard(article: NewsCardItem) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(280.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = "Kaydet", tint = Color.White)
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                color = EmeraldSecondary,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = article.category,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = article.readTime,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = article.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(article.author, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("• ${article.timeAgo}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = article.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = EmeraldSecondary,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

data class NewsCardItem(
    val category: String,
    val title: String,
    val readTime: String,
    val author: String,
    val timeAgo: String,
    val imageUrl: String,
    val progress: Float
)
