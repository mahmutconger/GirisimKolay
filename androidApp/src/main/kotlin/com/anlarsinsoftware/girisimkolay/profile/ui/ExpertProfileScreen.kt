package com.anlarsinsoftware.girisimkolay.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OnEmeraldSecondaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainer

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpertProfileScreen(
    expertId: String,
    onNavigateBack: () -> Unit,
    onAskQuestion: (String) -> Unit
) {
    var isBookmarked by remember { mutableStateOf(false) }

    // Let's resolve expert details based on ID
    val name = if (expertId == "kemal") "Müş. Kemal Doğru" else "Av. Selin Demir"
    val title = if (expertId == "kemal") "Mali Müşavir & Teşvik Uzmanı" else "Tax & Regulation Specialist"
    val avatarUrl = if (expertId == "kemal") {
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCuUgr74BSNsojo2_uOEo3C5-3jsJBYMOUxptcJ6MjgNoY_sU317z9XsFjgKlbWU73bQs_fcA4sDI1UV2z0hAEvPrj8AcwMiCRZFeQoWsipzxEWDv6Rb1cAujuurgrk13N8M-Wn1PRhKr0d9LQyqRHUv9uo516NRBtIge1cxNWu1zmJURisvGMBlPz8cZx4NH4Z1TjdPpIxldQDFK3NzxHTYAlQ8VGqMCN5FntNyhw6LBXEMPE1C-Hw5jxFal4jGrzjO0OM-u9OWgc"
    } else {
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCAPTmYvg5rqzRnlB2F3mv--4YtbxPPbQKNBObqgMD27HaxYenXoPF5CWG06MYxdpbaFY9rAQVdqve0Y3l67YvgvUQt6PFc9X2Z9dnvfYfxRLa5hgxGQchgeWtiUdK-UFtffWAF86Zc6fATPrfF8l_IoEf4K1tPEXQTvKeU_ZoiPDrbW69DaeOLlM4mFk2cUq5krneNynv0M90hgOCR5XRQ7cFQYlJjntUAJ-jgs45lUIG1uI1oVBKe_FyfiTyPh04Eo590XHX3R2M"
    }

    val experience = if (expertId == "kemal") "15 Yıl" else "12 Yıl"
    val answersCount = if (expertId == "kemal") "580+" else "450+"
    val rating = if (expertId == "kemal") "5.0" else "4.9"
    val category = if (expertId == "kemal") "financial" else "legal"

    val specialties = if (expertId == "kemal") {
        listOf("Hibe & Teşvikler", "KOSGEB Mevzuatı", "SGK Teşvikleri", "Genç Girişimci İstisnası", "Dönem Sonu İşlemleri")
    } else {
        listOf("Vergi Hukuku", "Ar-Ge Teşvikleri", "Genç Girişimci İstisnası", "Teknokent Mevzuatı", "Yatırım Sözleşmeleri")
    }

    val bio = if (expertId == "kemal") {
        "Kemal Doğru, KOBİ'ler ve yenilikçi girişimler için mali danışmanlık, devlet teşvikleri ve hibe süreçlerinde 15 yılı aşkın deneyime sahiptir. TÜBİTAK, KOSGEB ve kalkınma ajansları projelerinde 100'den fazla firmaya finansal planlama ve raporlama desteği sağlamıştır."
    } else {
        "Av. Selin Demir, teknoloji girişimleri ve KOBİ'ler için vergi hukuku, teşvik mevzuatı ve şirket kuruluş süreçlerinde 12 yılı aşkın deneyime sahiptir. Özellikle Teknokent ve Ar-Ge merkezi süreçlerinde uzmanlaşmış olup, 50'den fazla girişimin kuruluş ve yatırım turlarında hukuki danışmanlık yapmıştır. Karmaşık vergi mevzuatını girişimciler için anlaşılır hale getirmesiyle tanınır."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = NavyPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { isBookmarked = !isBookmarked }) {
                        Icon(
                             imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                             contentDescription = "Kaydet",
                             tint = NavyPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp, bottom = 24.dp)
                ) {
                    Button(
                        onClick = { onAskQuestion(category) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Soru Sor",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBF9FB))
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // Profile Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(80.dp)) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(SurfaceContainer),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSecondaryContainer)
                                    .border(2.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Onaylı",
                                    tint = OnEmeraldSecondaryContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = title,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))

                    // Metrics row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        // Metric 1
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = experience,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Deneyim",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(32.dp).background(OutlineVariant.copy(alpha = 0.5f)))

                        // Metric 2
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = answersCount,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Cevap",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(32.dp).background(OutlineVariant.copy(alpha = 0.5f)))

                        // Metric 3
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = rating,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSecondary,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Değerlendirme",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // About Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Hakkında",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 18.sp
                    )
                    Text(
                        text = bio,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // Specialties Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Uzmanlık Alanları",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 18.sp
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        specialties.forEach { spec ->
                            Surface(
                                color = NavyPrimary.copy(alpha = 0.05f),
                                border = BorderStroke(1.dp, NavyPrimary.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(99.dp)
                            ) {
                                Text(
                                    spec,
                                    color = NavyPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Recent Answers Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Son Yanıtları",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 18.sp
                        )
                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Tümünü Gör", color = NavyPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    val answers = if (expertId == "kemal") {
                        listOf(
                            Triple(
                                "Genç Girişimci İstisnası Şartları",
                                "29 yaş altı ilk defa şahıs şirketi kuracaklar için 3 yıl gelir vergisi istisnası ve 1 yıl Bağ-Kur desteği sunulmaktadır...",
                                "24 Faydalı • 2 gün önce"
                            ),
                            Triple(
                                "KOSGEB geleneksel girişimci desteği",
                                "KOSGEB hibe başvuruları öncesinde e-Devlet üzerinden girişimcilik eğitimini tamamlamış olmanız gerekmektedir...",
                                "15 Faydalı • 1 hafta önce"
                            )
                        )
                    } else {
                        listOf(
                            Triple(
                                "Şahıs şirketi mi, Limited şirket mi kurmalıyım?",
                                "Bu karar tamamen iş modelinize, beklentilerinize ve ilk yılki gelir hedeflerinize bağlıdır. Şahıs şirketi kurması kolay ve maliyetsizdir, ancak gelir arttıkça vergi dilimleri dezavantajlı hale gelebilir...",
                                "42 Faydalı • 2 gün önce"
                            ),
                            Triple(
                                "Genç girişimci istisnası şartları nelerdir?",
                                "29 yaşını doldurmamış olmak, ilk defa mükellefiyet tesis ettirmek ve işletmeyi fiilen kendi sevk ve idare etmek temel şartlardır. 3 yıl boyunca yıllık belirli bir tutara kadar gelir vergisinden muaf olursunuz...",
                                "18 Faydalı • 1 hafta önce"
                            )
                        )
                    }

                    answers.forEach { (q, a, meta) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Help,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp).offset(y = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        q,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    a,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(start = 24.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ThumbUp,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        meta,
                                        color = Color.Gray,
                                        fontSize = 11.sp
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
