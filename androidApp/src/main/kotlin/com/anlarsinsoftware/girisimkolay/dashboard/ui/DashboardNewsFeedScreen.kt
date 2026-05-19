package com.anlarsinsoftware.girisimkolay.dashboard.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    onNavigateToNotifications: () -> Unit
) {
    val userStatus by viewModel.userStatus.collectAsState()

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
                        // Red dot badge
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Sectoral News Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        // Background image
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBpYqHBe7Yy-kBwN7zNA1KS47Z-pxxMi3LYoVnDfB6HBp77ll8kXRCFKe7VrH7ePq327UsDpPeFBnorwSzmWNAH02s1BPzzIAaPfww2NC_fxvO11R6s__V9BBuNNEjuUscypPhKtkUiXxH5LnYlB2D1wal3Ci3kE7zlbuJHdIxhvZuiwAoRF2SgbvBfUYV0bjYao_ra1w39_oRsGAZ7vMR_jsHSZnn1Lgzd-20Jjm7MWcGlospCO50a5cSm3I4MDYiS4xqSQungCm0",
                            contentDescription = "KOSGEB Destekleri",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Dark overlay gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                    )
                                )
                        )

                        // Bookmark button top-right
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

                        // Category tag top-left
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            color = EmeraldSecondary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Finansman",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Text content at the bottom
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "5 dk okuma",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "KOSGEB Yeni Dönem Destek Paketlerini Açıkladı: 2 Milyon TL'ye Varan Hibe Fırsatı",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Author details
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDYZGEek9N6k7LocAcnjuFlQUaq9hl_WdH2BP_Eapf6kHvMQcWbPsLVzSpNGM4gOtF3cEIm01EogVbkgaJFjAGhQS3i-QcDBG1ZW8TKqJD-Gncyh9dveDsZgz5Tj9sL9hALlEa6_n2kIxo0QaHl9OO9u42pCgvizxosCjGLKf9TC86q3Dko3HDwYeNw0nnfjQKsbq6VwsfTBs7AktIWRxXuhsl-Wd3wIJB_L0kK4ynBBXbrmEBc1dp0aNj3G-Dm4PW-v9ROnKbYRsk",
                                    contentDescription = "Author",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("GirişimKolay Haber", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("• 2 saat önce", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            // Read Progress Bar
                            LinearProgressIndicator(
                                progress = 0.4f,
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

            // Business Calendar Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Girişim Takvimi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Tümü",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSecondary,
                            modifier = Modifier.clickable { }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Days
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val calendarDays = listOf(
                            CalendarDay("Pzt", "12", true, listOf(ErrorColor, EmeraldSecondary)),
                            CalendarDay("Sal", "13", false, emptyList()),
                            CalendarDay("Çar", "14", false, listOf(NavyPrimary)),
                            CalendarDay("Per", "15", false, emptyList()),
                            CalendarDay("Cum", "16", false, listOf(ErrorColor))
                        )
                        
                        calendarDays.forEach { day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 54.dp, height = 70.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (day.isSelected) EmeraldSecondary else Color.White)
                                        .border(
                                            1.dp, 
                                            if (day.isSelected) EmeraldSecondary else OutlineVariant.copy(alpha = 0.5f), 
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = day.name,
                                            color = if (day.isSelected) Color.White else Color.Gray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = day.date,
                                            color = if (day.isSelected) Color.White else NavyPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                // Status dots under the day
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    day.dots.forEach { color ->
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Upcoming event details card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFDAD6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ErrorColor)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("KDV Beyannamesi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
                                Text("Bugün son gün (Kritik Görev)", fontSize = 12.sp, color = Color.Gray)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }

            // Financial Status Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Finansal Durum",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Donut chart card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Gelir/Gider", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier.size(70.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = 0.65f,
                                        modifier = Modifier.fillMaxSize(),
                                        color = EmeraldSecondary,
                                        strokeWidth = 8.dp,
                                        trackColor = Color(0xFFE2F6EC)
                                    )
                                    Text("%65", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
                                }
                            }
                        }

                        // Bar chart card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Görevler", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.height(60.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // Custom bar chart rendering
                                    Box(modifier = Modifier.width(10.dp).height(20.dp).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(Color.LightGray))
                                    Box(modifier = Modifier.width(10.dp).height(50.dp).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(EmeraldSecondary))
                                    Box(modifier = Modifier.width(10.dp).height(35.dp).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(NavyPrimary))
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
                        // Decorative pattern box
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
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Bu ay ofis giderlerinizde %15'lik bir artış gözlendi. Kiralama modeline geçerek sabit maliyetlerinizi optimize edebilirsiniz.",
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

data class CalendarDay(
    val name: String,
    val date: String,
    val isSelected: Boolean,
    val dots: List<Color>
)
