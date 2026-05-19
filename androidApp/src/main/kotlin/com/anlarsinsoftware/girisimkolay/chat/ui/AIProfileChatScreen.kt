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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMode
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatSessionSummary
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.Citation
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.Message
import com.anlarsinsoftware.girisimkolay.chat.viewmodel.ChatViewModel
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OnEmeraldSecondaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OnSurfaceVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.PurpleAccent
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLowest
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
    val recentSessions by viewModel.recentSessions.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val showWelcome = messages.size <= 1 && messages.none { it.isFromUser }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val currentUserName = viewModel.currentUserName

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatDrawerContent(
                sessions = recentSessions,
                currentUserName = currentUserName,
                onSessionClick = { session ->
                    viewModel.switchSession(session.id)
                    scope.launch { drawerState.close() }
                },
                onNewChat = {
                    viewModel.startNewSession()
                    scope.launch { drawerState.close() }
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(EmeraldSecondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = OnEmeraldSecondaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    "Girişim Asistanı",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Text("Kurumsal danışman", fontSize = 11.sp, color = OnSurfaceVariant)
                            }
                        }
                    },
                    navigationIcon = {
                        Row {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menü")
                            }
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                            }
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
                    selectedMode = selectedMode,
                    onModeSelected = viewModel::selectMode,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFFBF9FB))
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (showWelcome) {
                    item {
                        WelcomeStateView()
                    }
                }

                items(
                    items = messages.sortedBy { it.timestamp },
                    key = { it.id }
                ) { message ->
                    MessageBubble(
                        message = message,
                        displayName = currentUserName ?: "Sen",
                        onActionClick = { action ->
                            when {
                                action.contains("Rapor", ignoreCase = true) -> onNavigateToRoadmap()
                                action.contains("Uzman", ignoreCase = true) -> onAskExpert("financial")
                                action.contains("Detay", ignoreCase = true) -> inputText = "Bu konuyu detaylandırır mısın?"
                                else -> scope.launch { snackbarHostState.showSnackbar(action) }
                            }
                        },
                        onCitationMissingUrl = {
                            scope.launch { snackbarHostState.showSnackbar("Bu kaynak için bağlantı bulunamadı.") }
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
private fun ChatModePicker(
    selectedMode: ChatMode,
    onModeSelected: (ChatMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            color = NavyPrimaryContainer.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, NavyPrimary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = selectedMode.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyPrimary
                )
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = "Mod seç",
                    tint = NavyPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ChatMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = mode.displayName,
                            fontWeight = if (mode == selectedMode) FontWeight.Bold else FontWeight.Normal,
                            color = if (mode == selectedMode) NavyPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    }
                )
            }
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
fun ChatDrawerContent(
    sessions: List<ChatSessionSummary>,
    currentUserName: String?,
    onSessionClick: (ChatSessionSummary) -> Unit,
    onNewChat: () -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "GirişimKolay",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 18.sp
            )
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kapat")
            }
        }

        // Yeni sohbet butonu
        Button(
            onClick = onNewChat,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Yeni Sohbet")
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Menü öğesi
        NavigationDrawerItem(
            label = { Text("Girişim Asistanı", fontWeight = FontWeight.SemiBold) },
            selected = true,
            onClick = onClose,
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Son sohbetler
        if (sessions.isNotEmpty()) {
            Text(
                "SON SOHBETLER",
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
                letterSpacing = 0.8.sp
            )
            sessions.forEach { session ->
                NavigationDrawerItem(
                    label = {
                        Text(
                            session.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp
                        )
                    },
                    selected = false,
                    onClick = { onSessionClick(session) },
                    icon = {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        } else {
            Text(
                "Henüz sohbet geçmişi yok.",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                fontSize = 13.sp,
                color = OnSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Alt kullanıcı alanı
        HorizontalDivider()
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(NavyPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    currentUserName?.firstOrNull()?.uppercase() ?: "G",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Text(
                currentUserName ?: "Girişimci",
                fontWeight = FontWeight.SemiBold,
                color = NavyPrimary,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    displayName: String = "Sen",
    onActionClick: (String) -> Unit,
    onCitationMissingUrl: () -> Unit = {}
) {
    // Both user and AI messages: left-aligned with avatar + name header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (message.isFromUser) NavyPrimary else EmeraldSecondaryContainer,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (message.isFromUser) {
                Text(
                    displayName.firstOrNull()?.uppercase() ?: "S",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            } else {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = OnEmeraldSecondaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Name header
            Text(
                if (message.isFromUser) displayName else "Girişim Asistanı",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (message.isFromUser) NavyPrimary else OnEmeraldSecondaryContainer
            )

            // Message card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isFromUser) NavyPrimary.copy(alpha = 0.06f) else Color.White
                ),
                border = BorderStroke(
                    1.dp,
                    if (message.isFromUser) NavyPrimary.copy(alpha = 0.12f) else OutlineVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                            Text(
                                text = message.text,
                                color = if (message.isFromUser) NavyPrimary else Color.Black,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )

                            if (message.citations.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                message.citations.forEach { citation ->
                                    CitationBadge(citation, onMissingUrl = onCitationMissingUrl)
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (buttonIcon != null) {
                                    Icon(buttonIcon, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(actionText, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { onActionClick(actionText) },
                                border = BorderStroke(1.dp, NavyPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (buttonIcon != null) {
                                    Icon(buttonIcon, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(actionText, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CitationBadge(citation: Citation, onMissingUrl: () -> Unit = {}) {
    val uriHandler = LocalUriHandler.current
    val hasUrl = !citation.sourceUrl.isNullOrBlank()
    Surface(
        onClick = {
            if (hasUrl) uriHandler.openUri(citation.sourceUrl!!) else onMissingUrl()
        },
        shape = RoundedCornerShape(8.dp),
        color = EmeraldSecondary.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, EmeraldSecondary.copy(alpha = 0.4f)),
        modifier = Modifier.padding(top = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                tint = EmeraldSecondary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = citation.sourceName,
                color = EmeraldSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (hasUrl) {
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Kaynağa git",
                    tint = EmeraldSecondary,
                    modifier = Modifier.size(12.dp)
                )
            }
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

        CitationBadge(Citation(sourceName = "KOSGEB 2026 Destek Rehberi", sourceUrl = "https://www.kosgeb.gov.tr/"))
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
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = OnEmeraldSecondaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Girişim Asistanı",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnEmeraldSecondaryContainer
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                    .background(Color.White)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                    .padding(14.dp)
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
}

@Composable
fun ChatInputBar(
    inputText: String,
    selectedMode: ChatMode,
    onModeSelected: (ChatMode) -> Unit,
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
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Mod seçici (üst satır)
            ChatModePicker(
                selectedMode = selectedMode,
                onModeSelected = onModeSelected
            )

            // Alt satır: metin alanı + gönder/mikrofon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SurfaceContainerLow, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = onTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black,
                            fontSize = 15.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    "Sorunuzu yazın...",
                                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                if (inputText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(PurpleAccent, CircleShape)
                            .clickable(onClick = onSend),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Gönder",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { /* mikrofon placeholder */ },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Sesli giriş",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
