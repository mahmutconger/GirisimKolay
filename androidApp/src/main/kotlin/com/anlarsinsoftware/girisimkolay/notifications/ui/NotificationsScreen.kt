package com.anlarsinsoftware.girisimkolay.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.ErrorColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdvice: (String) -> Unit,
    onNavigateToExpertProfile: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Hepsi") }
    val filters = listOf("Hepsi", "Takip", "Mesajlar", "Sistem")

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
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", tint = NavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBF9FB))
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bildirimler",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Delete, contentDescription = "Temizle", tint = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("Bugün ")
                            withStyle(style = SpanStyle(color = ErrorColor, fontWeight = FontWeight.SemiBold)) {
                                append("4 yeni bildiriminiz")
                            }
                            append(" var")
                        },
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val isSelected = filter == selectedFilter
                        Surface(
                            modifier = Modifier.clickable { selectedFilter = filter },
                            shape = RoundedCornerShape(99.dp),
                            color = if (isSelected) NavyPrimary else Color(0xFFE4E2E5),
                            contentColor = if (isSelected) Color.White else Color(0xFF44474D)
                        ) {
                            Text(
                                text = filter,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Today Group
            item {
                Text(
                    text = "Bugün",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Expert Response Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Blue vertical indicator on the left
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(NavyPrimary)
                                .align(Alignment.CenterStart)
                        )
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(start = 8.dp)
                                .clickable { onNavigateToExpertProfile("selin") },
                            verticalAlignment = Alignment.Top
                        ) {
                            AsyncImage(
                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAeQuQRxFnVjvOW2QwbHcnREp2FspenxIS95ILrotKrryj9ARJMRXfp-ZHYJYIRZy4m2wnVuOCioV1lPurex0_xW9_89BX1nJ_XBVXxsrQMHQeI0mstlEP2Tnwy9CRytH1u4F6sbvvwJ35VXVtSH9eXJ4UbPO4gqxqXIX-W-qBtVHFGCtpC4aNuVcfYeY9JMgjrEvcxtnxcH5_Es3ubPQSHEabnCIkWt2dTNGbKsgLJUm5vqRr2kUSUVIfaiirsHH3gkiJxuynezJk",
                                contentDescription = "Av. Burak Yılmaz",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = NavyPrimary)) {
                                            append("Av. Burak Yılmaz")
                                        }
                                        append(" hukuki sorunuzu yanıtladı: ")
                                        withStyle(style = SpanStyle(color = Color.Gray)) {
                                            append("\"Şirket kuruluşu için gerekli...\"")
                                        }
                                    },
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "10 dk önce", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onNavigateToAdvice("1") },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Cevabı Görüntüle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Swipe to Delete Simulated Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Simulated red swipe action on the right
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(80.dp)
                                .background(ErrorColor)
                                .align(Alignment.CenterEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color.White)
                        }

                        // Shifted content row (simulating translationX)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = (-30).dp) // Simulate partial swipe
                                .background(Color.White)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC4eqP3coFOwZn_jkwbntho5uiM37I2_qpIwytnC8Um2wQ4tYEc19-hDUKR3E66oFS8tLlYzb-k3WPthzAj8AzNG5BDOtpPshESlJpY9xnv6JOcsRB5z3fhQX6rjjcR2wkw5WkrvGF2P4lwkjzDx6rJ_x6pQw86YgoygbHcUePFaivZIlAfwlpfIRLT1KIVqcF8UpN75BEl83xBOV3pFhBaCBhS0_-AHwkgAIzjOUJ-twGVkNzvCVc-6kmu-8uDg3uI-E7X7Jx4_iE",
                                contentDescription = "Ayşe Demir",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = NavyPrimary)) {
                                            append("Ayşe Demir")
                                        }
                                        append(" seni takip etmeye başladı.")
                                    },
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "2 saat önce", fontSize = 12.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("+ Takip Et", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Article Notification Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NavyPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null, tint = NavyPrimary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = buildAnnotatedString {
                                    append("Yeni makale yayınlandı: ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = NavyPrimary)) {
                                        append("\"2024 Vergi Muafiyetleri Nelerdir?\"")
                                    }
                                },
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "5 saat önce", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // This Week Group
            item {
                Text(
                    text = "Bu Hafta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Comment Notification Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFoKXs-uFg2YP3yvJZ2hobDmpj7SC31KJQkVlMWr8HdJ22Zt25Yyn6kBuMngzxalAoZCxDDwo07oksDc874G8SY-wRBPo4uRoDDJq2yUamo5s0x_xuizpjGFrUaCV7wDJMTy0DLI9wsp0c_kqzDSun0ouF-3tPNSFegxavSJHajL51o2LJMVRxT1xh60FZMCmXdDHjSgdQn-EHsT288DSPUoR2LyR2O7D5EXzekiIv-E1QZh36vxu_ShLwSUu57ohend6-FAoinYA",
                            contentDescription = "Can Özkan",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = NavyPrimary)) {
                                        append("Can Özkan")
                                    }
                                    append(" gönderine yorum yaptı: ")
                                    withStyle(style = SpanStyle(color = Color.Gray)) {
                                        append("\"Harika bir fikir, kesinlikle katılıyorum!\"")
                                    }
                                },
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Dün", fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBpYqHBe7Yy-kBwN7zNA1KS47Z-pxxMi3LYoVnDfB6HBp77ll8kXRCFKe7VrH7ePq327UsDpPeFBnorwSzmWNAH02s1BPzzIAaPfww2NC_fxvO11R6s__V9BBuNNEjuUscypPhKtkUiXxH5LnYlB2D1wal3Ci3kE7zlbuJHdIxhvZuiwAoRF2SgbvBfUYV0bjYao_ra1w39_oRsGAZ7vMR_jsHSZnn1Lgzd-20Jjm7MWcGlospCO50a5cSm3I4MDYiS4xqSQungCm0",
                            contentDescription = "Post Thumbnail",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            // Profile Verified Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2F6EC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldSecondary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Profil onayınız başarıyla tamamlandı. Artık tüm özelliklere erişebilirsiniz.",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "3 gün önce", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
