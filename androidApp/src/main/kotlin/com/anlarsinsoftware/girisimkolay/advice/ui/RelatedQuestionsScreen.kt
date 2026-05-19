package com.anlarsinsoftware.girisimkolay.advice.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Visibility
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
import coil.compose.AsyncImage
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatedQuestionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdviceDetail: (String) -> Unit,
    onNavigateToAIChat: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tümü") }
    val filters = listOf("Tümü", "Vergi & Hukuk", "Hibe & Destek", "Şirket Kurulumu")

    val questions = listOf(
        QuestionCardItem(
            id = "1",
            category = "Şirket Kurulumu",
            title = "Şahıs şirketi mi, Limited şirket mi kurmalıyım?",
            summary = "Tek başınıza, düşük maliyetle ve hızlıca işe başlamak istiyorsanız şahıs şirketi idealdir. Ancak uzun vadede yatırımcı almayı veya prestijli bir yapı kurmayı hedefliyorsanız Limited (LTD) şirket daha uygun olacaktır. Ayrıca vergi dilimleri açısından...",
            authorName = "Kemal D.",
            authorRole = "Mali Müşavir",
            authorAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuDYZGEek9N6k7LocAcnjuFlQUaq9hl_WdH2BP_Eapf6kHvMQcWbPsLVzSpNGM4gOtF3cEIm01EogVbkgaJFjAGhQS3i-QcDBG1ZW8TKqJD-Gncyh9dveDsZgz5Tj9sL9hALlEa6_n2kIxo0QaHl9OO9u42pCgvizxosCjGLKf9TC86q3Dko3HDwYeNw0nnfjQKsbq6VwsfTBs7AktIWRxXuhsl-Wd3wIJB_L0kK4ynBBXbrmEBc1dp0aNj3G-Dm4PW-v9ROnKbYRsk",
            views = "1.2k Okunma"
        ),
        QuestionCardItem(
            id = "2",
            category = "Hibe & Destek",
            title = "KOSGEB Geleneksel Girişimci desteği nasıl alınır?",
            summary = "KOSGEB desteği almak için öncelikle e-Devlet üzerinden girişimcilik eğitimini tamamlamanız gerekmektedir. Sonrasında şirket kurulumunu yapıp veri tabanına kayıt olmalısınız. Destek miktarı...",
            authorName = "Ayşe Y.",
            authorRole = "Hibe Danışmanı",
            authorAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFoKXs-uFg2YP3yvJZ2hobDmpj7SC31KJQkVlMWr8HdJ22Zt25Yyn6kBuMngzxalAoZCxDDwo07oksDc874G8SY-wRBPo4uRoDDJq2yUamo5s0x_xuizpjGFrUaCV7wDJMTy0DLI9wsp0c_kqzDSun0ouF-3tPNSFegxavSJHajL51o2LJMVRxT1xh60FZMCmXdDHjSgdQn-EHsT288DSPUoR2LyR2O7D5EXzekiIv-E1QZh36vxu_ShLwSUu57ohend6-FAoinYA",
            views = "850 Okunma"
        ),
        QuestionCardItem(
            id = "3",
            category = "Vergi & Hukuk",
            title = "Genç Girişimci İstisnası şartları nelerdir?",
            summary = "İşe başlama tarihi itibariyle 29 yaşını doldurmamış, ilk defa gelir vergisi mükellefi olan kişiler faydalanabilir. 3 yıl boyunca kazancın belirli bir kısmı gelir vergisinden muaftır ve 1 yıl Bağ-Kur desteği sunulur.",
            authorName = "Burak T.",
            authorRole = "Vergi Hukukçusu",
            authorAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuC4eqP3coFOwZn_jkwbntho5uiM37I2_qpIwytnC8Um2wQ4tYEc19-hDUKR3E66oFS8tLlYzb-k3WPthzAj8AzNG5BDOtpPshESlJpY9xnv6JOcsRB5z3fhQX6rjjcR2wkw5WkrvGF2P4lwkjzDx6rJ_x6pQw86YgoygbHcUePFaivZIlAfwlpfIRLT1KIVqcF8UpN75BEl83xBOV3pFhBaCBhS0_-AHwkgAIzjOUJ-twGVkNzvCVc-6kmu-8uDg3uI-E7X7Jx4_iE",
            views = "2.1k Okunma"
        )
    )

    // Filtering logic
    val filteredQuestions = questions.filter { question ->
        val matchesCategory = selectedFilter == "Tümü" || question.category == selectedFilter
        val matchesSearch = question.title.contains(searchQuery, ignoreCase = true) ||
                question.summary.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("İlgili Sorular", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 20.sp)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBF9FB))
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    placeholder = { Text("Soru veya konu ara...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimary,
                        unfocusedBorderColor = OutlineVariant.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
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

            // Question Feed or Empty State
            if (filteredQuestions.isEmpty()) {
                item {
                    SearchEmptyState(
                        onKeywordClick = { keyword ->
                            searchQuery = keyword
                        },
                        onNavigateToAIChat = onNavigateToAIChat
                    )
                }
            } else {
                items(filteredQuestions) { question ->
                    QuestionFeedCard(
                        item = question,
                        onClick = { onNavigateToAdviceDetail(question.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionFeedCard(
    item: QuestionCardItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                Surface(
                    color = EmeraldSecondary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.views,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.summary,
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = item.authorAvatar,
                    contentDescription = item.authorName,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NavyPrimary
                    )
                    Text(
                        text = item.authorRole,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun SearchEmptyState(
    onKeywordClick: (String) -> Unit,
    onNavigateToAIChat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sonuç Bulunamadı",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Farklı anahtar kelimeler deneyebilir veya AI Danışmanımıza danışabilirsiniz.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Suggested Keywords
        Text(
            text = "Önerilen Kelimeler:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val keywords = listOf("Genç Girişimci", "Limited Şirket", "KOSGEB Hibe", "KDV Muafiyeti")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            keywords.forEach { keyword ->
                Surface(
                    modifier = Modifier.clickable { onKeywordClick(keyword) },
                    shape = RoundedCornerShape(99.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = keyword,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        color = NavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToAIChat,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("AI Danışmana Sor", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

data class QuestionCardItem(
    val id: String,
    val category: String,
    val title: String,
    val summary: String,
    val authorName: String,
    val authorRole: String,
    val authorAvatar: String,
    val views: String
)
