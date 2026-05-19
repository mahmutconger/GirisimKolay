package com.anlarsinsoftware.girisimkolay.community.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anlarsinsoftware.girisimkolay.community.domain.entity.CommunityPost
import com.anlarsinsoftware.girisimkolay.community.viewmodel.CommunityViewModel
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.ErrorColor
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityHubScreen(
    viewModel: CommunityViewModel = koinViewModel(),
    onNavigateToNotifications: () -> Unit,
    onNavigateToAdviceDetail: (String) -> Unit
) {
    val networkPosts by viewModel.networkPosts.collectAsState()
    val expertPosts by viewModel.expertPosts.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Girişimci Ağı", "Uzmanlara Sor")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Community Hub", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 20.sp)
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Yaz")
            }
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
            // Rounded Tab Selector
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = SurfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(99.dp)),
                    indicator = { Box(modifier = Modifier.size(0.dp)) }, // Hide standard indicator line
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (isSelected) Color.White else Color.Transparent),
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) NavyPrimary else Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }

            // Featured Card (Only shows on Girişimci Ağı)
            if (selectedTabIndex == 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            AsyncImage(
                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBpYqHBe7Yy-kBwN7zNA1KS47Z-pxxMi3LYoVnDfB6HBp77ll8kXRCFKe7VrH7ePq327UsDpPeFBnorwSzmWNAH02s1BPzzIAaPfww2NC_fxvO11R6s__V9BBuNNEjuUscypPhKtkUiXxH5LnYlB2D1wal3Ci3kE7zlbuJHdIxhvZuiwAoRF2SgbvBfUYV0bjYao_ra1w39_oRsGAZ7vMR_jsHSZnn1Lgzd-20Jjm7MWcGlospCO50a5cSm3I4MDYiS4xqSQungCm0",
                                contentDescription = "Featured Post",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                            )
                            
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp),
                                color = ErrorColor,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Öne Çıkan",
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
                                Text("2 saat önce", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "2024 E-Ticaret Trendleri Raporu",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Expert Q&A Card (Only shows on Uzmanlara Sor tab)
            if (selectedTabIndex == 1) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(NavyPrimary)
                                    .align(Alignment.CenterStart)
                            )
                            Column(modifier = Modifier.padding(16.dp).padding(start = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Help, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("GÜNÜN SORUSU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Genç Girişimci İstisnası Şartları Nelerdir?", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Expert profile & short answer block
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDYZGEek9N6k7LocAcnjuFlQUaq9hl_WdH2BP_Eapf6kHvMQcWbPsLVzSpNGM4gOtF3cEIm01EogVbkgaJFjAGhQS3i-QcDBG1ZW8TKqJD-Gncyh9dveDsZgz5Tj9sL9hALlEa6_n2kIxo0QaHl9OO9u42pCgvizxosCjGLKf9TC86q3Dko3HDwYeNw0nnfjQKsbq6VwsfTBs7AktIWRxXuhsl-Wd3wIJB_L0kK4ynBBXbrmEBc1dp0aNj3G-Dm4PW-v9ROnKbYRsk",
                                        contentDescription = "Kemal D.",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Kemal D.", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Verified, contentDescription = "Onaylı", tint = Color(0xFF6CF8BB), modifier = Modifier.size(14.dp))
                                        }
                                        Text("Onaylı Mali Müşavir", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Kemal D: 29 yaş altı ilk defa şirket kuracaklar için 3 yıl boyunca gelir vergisi istisnası ve 1 yıl Bağ-Kur prim desteği sunulmaktadır...",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onNavigateToAdviceDetail("1") },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text("Cevabı Görüntüle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Post Cards List
            val posts = if (selectedTabIndex == 0) networkPosts else expertPosts
            items(posts) { post ->
                CommunityPostCard(
                    post = post,
                    avatarUrl = if (post.authorName.contains("Ahmet")) {
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuAeQuQRxFnVjvOW2QwbHcnREp2FspenxIS95ILrotKrryj9ARJMRXfp-ZHYJYIRZy4m2wnVuOCioV1lPurex0_xW9_89BX1nJ_XBVXxsrQMHQeI0mstlEP2Tnwy9CRytH1u4F6sbvvwJ35VXVtSH9eXJ4UbPO4gqxqXIX-W-qBtVHFGCtpC4aNuVcfYeY9JMgjrEvcxtnxcH5_Es3ubPQSHEabnCIkWt2dTNGbKsgLJUm5vqRr2kUSUVIfaiirsHH3gkiJxuynezJk"
                    } else if (post.authorName.contains("Zeynep")) {
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuC4eqP3coFOwZn_jkwbntho5uiM37I2_qpIwytnC8Um2wQ4tYEc19-hDUKR3E66oFS8tLlYzb-k3WPthzAj8AzNG5BDOtpPshESlJpY9xnv6JOcsRB5z3fhQX6rjjcR2wkw5WkrvGF2P4lwkjzDx6rJ_x6pQw86YgoygbHcUePFaivZIlAfwlpfIRLT1KIVqcF8UpN75BEl83xBOV3pFhBaCBhS0_-AHwkgAIzjOUJ-twGVkNzvCVc-6kmu-8uDg3uI-E7X7Jx4_iE"
                    } else {
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuAFoKXs-uFg2YP3yvJZ2hobDmpj7SC31KJQkVlMWr8HdJ22Zt25Yyn6kBuMngzxalAoZCxDDwo07oksDc874G8SY-wRBPo4uRoDDJq2yUamo5s0x_xuizpjGFrUaCV7wDJMTy0DLI9wsp0c_kqzDSun0ouF-3tPNSFegxavSJHajL51o2LJMVRxT1xh60FZMCmXdDHjSgdQn-EHsT288DSPUoR2LyR2O7D5EXzekiIv-E1QZh36vxu_ShLwSUu57ohend6-FAoinYA"
                    }
                )
            }
        }
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost, avatarUrl: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Profile row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = post.authorName,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
                            if (post.isVerifiedExpert) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF6CF8BB), modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(
                            text = if (post.isVerifiedExpert) "Sistem Onaylı Uzman" else "Mikro Girişimci",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content
            Text(
                text = post.content,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = NavyPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = OutlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))
            
            // Interactions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                    Icon(Icons.Default.ThumbUp, contentDescription = "Like", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${post.likes}", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                    Icon(Icons.Default.Comment, contentDescription = "Comment", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${post.commentsCount}", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Paylaş", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
