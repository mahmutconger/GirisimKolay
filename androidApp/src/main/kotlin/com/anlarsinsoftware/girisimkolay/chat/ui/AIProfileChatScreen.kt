package com.anlarsinsoftware.girisimkolay.chat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMode
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.Message
import com.anlarsinsoftware.girisimkolay.chat.viewmodel.ChatViewModel
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OnEmeraldSecondaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLowest
import com.anlarsinsoftware.girisimkolay.ui.theme.OnSurfaceVariant
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIProfileChatScreen(
    viewModel: ChatViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToRoadmap: () -> Unit,
    onAskExpert: (String) -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val showWelcome = messages.size <= 1 && messages.none { it.isFromUser }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBYAeElu_rlgVvFeMIqEgJrugYN2kGXI86NvtpjudXEthTFK9ieBP7cEQfygoE7sYp14DmT_u9EacHu0DF8fhLS4gJNwmuhWyfp85utZmbYjiI529WqjJm_hdw1rDfzHYoYcKmzWmcfUoYf5SGmT5IbfD5Ta5wzifuOriwEVED9b0GDKYfpTzczTNofT2aFzJoNhTQ2Q0YU9k5gkfJa--s5QGd2aInz1OurUhZMqsPKSosb_cpjhFnlWLWzcsM0UIifml2_YS5NXuY",
                            contentDescription = "Profil",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(1.dp, OutlineVariant, CircleShape)
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Girişim Asistanı",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kurumsal danışman", fontSize = 12.sp, color = OnSurfaceVariant)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Bildirimler",
                            tint = OnSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = inputText,
                onTextChange = { inputText = it },
                onAddAttachment = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Belge yükleme özelliği Android dosya seçiciye bağlanmak üzere hazır.")
                    }
                },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBF9FB))
        ) {
            ChatModeSelector(
                selectedMode = selectedMode,
                onModeSelected = viewModel::selectMode
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (showWelcome) {
                    item {
                        WelcomeStateView()
                    }
                }

                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        onActionClick = { action ->
                            when {
                                action.contains("Rapor", ignoreCase = true) -> onNavigateToRoadmap()
                                action.contains("Uzman", ignoreCase = true) -> onAskExpert("financial")
                                action.contains("Detay", ignoreCase = true) -> inputText = "Bu konuyu detaylandırır mısın?"
                                else -> scope.launch { snackbarHostState.showSnackbar(action) }
                            }
                        },
                        onCitationClick = { source ->
                            scope.launch { snackbarHostState.showSnackbar("Kaynak: $source") }
                        }
                    )
                }

                if (isTyping) {
                    item {
                        TypingIndicatorBubble()
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatModeSelector(
    selectedMode: ChatMode,
    onModeSelected: (ChatMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChatMode.entries.forEach { mode ->
            FilterChip(
                modifier = Modifier.weight(1f),
                selected = selectedMode == mode,
                onClick = { onModeSelected(mode) },
                label = {
                    Text(
                        text = mode.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NavyPrimaryContainer,
                    selectedLabelColor = NavyPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedMode == mode,
                    borderColor = OutlineVariant,
                    selectedBorderColor = NavyPrimary
                )
            )
        }
    }
}

@Composable
fun WelcomeStateView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(NavyPrimaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Nasıl yardımcı olabilirim?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "KOSGEB destekleri, iş planı hazırlama veya şirket kurulum süreçleri hakkında sorular sorabilirsiniz.",
            fontSize = 14.sp,
            color = OnSurfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp),
            lineHeight = 20.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun MessageBubble(
    message: Message,
    onActionClick: (String) -> Unit,
    onCitationClick: (String) -> Unit
) {
    if (message.isFromUser) {
        // User bubble
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                    .background(NavyPrimary)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .widthIn(max = 290.dp)
            ) {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
    } else {
        // AI bubble with Avatar on left, custom formatted Card on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(EmeraldSecondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = OnEmeraldSecondaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Render based on templates
                        when {
                            message.text.startsWith("[KOSGEB_TEMPLATE]") -> {
                                val cleanText = message.text.removePrefix("[KOSGEB_TEMPLATE]")
                                KosgebTemplateContent(cleanText)
                            }
                            message.text.startsWith("[BLOCKCHAIN_TEMPLATE]") -> {
                                val cleanText = message.text.removePrefix("[BLOCKCHAIN_TEMPLATE]")
                                BlockchainTemplateContent(cleanText)
                            }
                            message.text.startsWith("[SGK_TEMPLATE]") -> {
                                val cleanText = message.text.removePrefix("[SGK_TEMPLATE]")
                                SgkTemplateContent(cleanText)
                            }
                            message.text.startsWith("[VERGI_TEMPLATE]") -> {
                                val cleanText = message.text.removePrefix("[VERGI_TEMPLATE]")
                                VergiTemplateContent(cleanText)
                            }
                            else -> {
                                // Default response text
                                Text(
                                    text = message.text,
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )

                                if (message.sources.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    message.sources.forEach { sourceName ->
                                        CitationBadge(sourceName, onClick = { onCitationClick(sourceName) })
                                    }
                                }
                            }
                        }
                    }
                }

                // Action buttons row below message card
                if (message.nextActions.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        message.nextActions.forEachIndexed { index, actionText ->
                            val isPrimary = index == 0
                            val buttonIcon = when {
                                actionText.contains("Rapor", ignoreCase = true) -> Icons.Default.Description
                                actionText.contains("Uzman", ignoreCase = true) -> Icons.Default.SupportAgent
                                actionText.contains("Detay", ignoreCase = true) -> Icons.Default.Search
                                else -> null
                            }

                            if (isPrimary) {
                                Button(
                                    onClick = { onActionClick(actionText) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (buttonIcon != null) {
                                            Icon(buttonIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(actionText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onActionClick(actionText) },
                                    border = BorderStroke(1.dp, NavyPrimary),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (buttonIcon != null) {
                                            Icon(buttonIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(actionText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CitationBadge(sourceName: String, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = EmeraldSecondaryContainer.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, EmeraldSecondaryContainer),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                tint = OnEmeraldSecondaryContainer,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "[Kaynak: $sourceName]",
                color = OnEmeraldSecondaryContainer,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 1. KOSGEB checklist template Content
@Composable
fun KosgebTemplateContent(introText: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = introText,
            color = Color.Black,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Text(
            text = "1. Uygun Destek Programı:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )

        Text(
            text = "Şu anki durumunuz için en uygun program \"Geleneksel Girişimci Destek Programı\" olarak görünmektedir. Bu program, imalat sektörü dışında kalan veya belirli ölçekteki imalatları kapsayan yeni girişimcilere yöneliktir.",
            fontSize = 13.sp,
            color = OnSurfaceVariant,
            lineHeight = 18.sp
        )

        Text(
            text = "2. İzlemeniz Gereken Adımlar:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )

        val steps = listOf(
            "Girişimcilik Eğitimini Tamamlayın" to "KOSGEB'in e-Akademi sistemi üzerinden ücretsiz ve online olarak \"Geleneksel Girişimci Eğitimi\"ni almalısınız. Şirketinizi kurmadan önce bu belgeyi almanız şarttır.",
            "Şirketinizi Kurun" to "Eğitimi tamamladıktan sonra şahıs şirketinizi resmi olarak kurabilirsiniz.",
            "KOSGEB'e Kayıt ve Başvuru" to "Şirket kuruluşundan sonra KOSGEB veritabanına kayıt olup, iş planınızı hazırlayarak destek başvurunuzu sistem üzerinden yapabilirsiniz."
        )

        steps.forEach { (title, desc) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldSecondary,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        text = desc,
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        CitationBadge("KOSGEB 2026 Destek Rehberi")
    }
}

// 2. Blockchain numbered template Content
@Composable
fun BlockchainTemplateContent(introText: String) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = introText,
            color = Color.Black,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        // Item 1
        BlockchainItem(
            number = "1",
            title = "Katman 1 (Layer 1) Çözümleri",
            intro = "Doğrudan temel blockchain protokolünde yapılan iyileştirmelerdir.",
            points = listOf(
                "Sharding" to "Ağı daha küçük, yönetilebilir parçalara (shard'lara) bölerek işlemleri paralel işleme.",
                "Konsensüs Güncellemeleri" to "Proof of Work (PoW) yerine daha hızlı olan Proof of Stake (PoS) gibi mekanizmalara geçiş."
            )
        )

        // Item 2
        BlockchainItem(
            number = "2",
            title = "Katman 2 (Layer 2) Çözümleri",
            intro = "Ana ağın (Layer 1) üzerine inşa edilen, işlemleri zincir dışında gerçekleştirip sonuçlarını ana ağa kaydeden protokollerdir.",
            points = listOf(
                "Rollups" to "Çok sayıda işlemi bir araya toplayıp (roll-up) ana ağa tek bir işlem olarak sunar. Optimistic ve ZK (Zero-Knowledge) Rollups en yaygın olanlarıdır.",
                "State Channels & Sidechains" to "Kullanıcılar arasında özel kanallar kurarak veya ana ağa paralel çalışan yan zincirler oluşturarak işlem yükünü hafifletir."
            )
        )

        // Item 3
        BlockchainItem(
            number = "3",
            title = "Off-chain Çözümler",
            intro = "Verilerin ve işlemlerin blockchain dışında tutulduğu, yalnızca kritik kanıtların zincire yazıldığı yaklaşımlardır. Büyük veri setleri gerektiren merkeziyetsiz uygulamalar (dApps) için idealdir.",
            points = emptyList()
        )

        // Trilemma box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
        ) {
            Text(
                text = "Sonuç olarak, tek bir \"gümüş kurşun\" strateji yoktur. Modern blockchain ekosistemleri genellikle, güvenlik, merkezsizleşme ve ölçeklenebilirlik arasındaki 'Blockchain Trilemma'sını dengelemek için bu çözümlerin bir kombinasyonunu kullanır.",
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = OnSurfaceVariant,
                lineHeight = 18.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun BlockchainItem(
    number: String,
    title: String,
    intro: String,
    points: List<Pair<String, String>>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(SurfaceContainerLow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(intro, fontSize = 13.sp, color = OnSurfaceVariant, lineHeight = 18.sp)

            if (points.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Left border indicator line
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(IntrinsicSize.Max)
                            .background(OutlineVariant.copy(alpha = 0.5f))
                            .align(Alignment.CenterStart)
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        points.forEach { (subTitle, subDesc) ->
                            Column {
                                Row {
                                    Text(
                                        "$subTitle: ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        subDesc,
                                        fontSize = 12.sp,
                                        color = OnSurfaceVariant,
                                        lineHeight = 16.sp
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

// 3. SGK template Content
@Composable
fun SgkTemplateContent(introText: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = introText,
            color = Color.Black,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        val cardsData = listOf(
            Triple("5 Puanlık İndirim", "%5 İNDİRİM", "Primlerini düzenli ödeyen işverenler için uygulanır. SGK işveren payında 5 puanlık bir indirim sağlanarak maliyetleriniz düşürülür."),
            Triple("Genç Girişimci Teşviki", "12 AY TEŞVİK", "29 yaş altı genç girişimciler için geçerlidir. Bağ-Kur primleriniz 1 yıl boyunca Hazine tarafından karşılanır."),
            Triple("Kadın ve Genç İstihdamı", "İSTİHDAM DESTEĞİ", "Kadınları ve gençleri işe alan işletmelere yönelik sigorta primi maliyetlerini azaltan önemli bir istihdam desteğidir.")
        )

        cardsData.forEach { (title, badge, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Surface(
                            shape = CircleShape,
                            color = EmeraldSecondaryContainer.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, EmeraldSecondaryContainer.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = EmeraldSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(desc, fontSize = 13.sp, color = OnSurfaceVariant, lineHeight = 18.sp)
                }
            }
        }
    }
}

// 4. Vergi template Content
@Composable
fun VergiTemplateContent(introText: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = introText,
            color = Color.Black,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        // Card 1
        VergiCard(
            icon = Icons.Default.School,
            title = "Genç Girişimci İstisnası",
            desc = "18-29 yaş arası ilk defa iş kuranlar için geçerlidir. 3 yıl boyunca yıllık 75.000 TL'ye kadar kazanç vergiden istisnadır. Ayrıca 1 yıl boyunca Bağ-Kur primleri Hazine tarafından karşılanır.",
            badges = listOf("75.000 TL Kazanç", "1 Yıl Bağ-Kur")
        )

        // Card 2
        VergiCard(
            icon = Icons.Default.Science,
            title = "Teknopark & Ar-Ge İndirimleri",
            desc = "Teknoloji Geliştirme Bölgelerinde (Teknopark) faaliyet gösteren firmaların yazılım ve Ar-Ge faaliyetlerinden elde ettikleri kazançlar Kurumlar Vergisi'nden istisnadır. Ar-Ge personeli için gelir vergisi stopajı teşviki uygulanır.",
            badges = emptyList()
        )

        // Card 3
        VergiCard(
            icon = Icons.Default.FlightTakeoff,
            title = "İhracat İstisnası",
            desc = "Yurtdışına verilen yazılım, mühendislik, mimarlık, tasarım ve veri işleme gibi hizmetlerden elde edilen kazançların %50'si beyanname üzerinden indirilebilir (Şartların sağlanması durumunda).",
            badges = emptyList()
        )
    }
}

@Composable
fun VergiCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    badges: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(NavyPrimaryContainer.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                }
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(desc, fontSize = 13.sp, color = OnSurfaceVariant, lineHeight = 18.sp)

            if (badges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    badges.forEach { badge ->
                        Surface(
                            shape = CircleShape,
                            color = EmeraldSecondary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = EmeraldSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicatorBubble() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(EmeraldSecondaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                tint = OnEmeraldSecondaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                .background(Color.White)
                .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = androidx.compose.ui.Modifier.alpha(alpha)
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(OnSurfaceVariant))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(OnSurfaceVariant))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(OnSurfaceVariant))
            }
        }
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    onTextChange: (String) -> Unit,
    onAddAttachment: () -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest, RoundedCornerShape(12.dp))
                    .border(2.dp, OutlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onAddAttachment) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Ekle",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                BasicTextField(
                    value = inputText,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.Black,
                        fontSize = 15.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (inputText.isEmpty()) {
                            Text(
                                "Bir soru sorun... (örn. KOSGEB)",
                                color = OnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                )

                IconButton(
                    onClick = onSend,
                    enabled = inputText.isNotBlank(),
                    modifier = Modifier
                        .size(36.dp)
                        .background(NavyPrimary, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Gönder",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Yapay zeka asistanı hata yapabilir. Önemli kararlar öncesi uzmanlara danışın.",
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
