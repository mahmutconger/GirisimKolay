package com.anlarsinsoftware.girisimkolay.roadmap.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anlarsinsoftware.girisimkolay.R
import com.anlarsinsoftware.girisimkolay.core.domain.AppError
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapStep
import com.anlarsinsoftware.girisimkolay.roadmap.viewmodel.RoadmapEffect
import com.anlarsinsoftware.girisimkolay.roadmap.viewmodel.RoadmapViewModel
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.ErrorColor
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapDocumentCenterScreen(viewModel: RoadmapViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current

    // Effect handling
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RoadmapEffect.OpenUrl -> {
                    uriHandler.openUri(effect.url)
                }
                is RoadmapEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    val errorMessage = when (uiState.error?.code) {
        "unauthenticated" -> stringResource(R.string.roadmap_error_unauthenticated)
        "missing_session" -> stringResource(R.string.roadmap_error_missing_session)
        "missing_token" -> stringResource(R.string.roadmap_error_missing_token)
        "report_generate_failed" -> stringResource(R.string.roadmap_error_report_failed)
        null -> null
        else -> uiState.error?.message ?: stringResource(R.string.roadmap_error_generic)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.roadmap_title), fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 20.sp)
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
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.roadmap_journey_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            if (uiState.isLoadingSteps && uiState.steps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NavyPrimary)
                    }
                }
            } else if (uiState.steps.isEmpty()) {
                item {
                    EmptyStepsView()
                }
            }

            // Timeline steps
            itemsIndexed(
                items = uiState.steps,
                key = { _, step -> step.id }
            ) { index, step ->
                StepperItem(
                    step = step,
                    isLast = index == uiState.steps.size - 1
                )
            }

            // CPA Signature/Verification Card (if latestReport is ready)
            if (uiState.latestReport != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2F6EC)),
                        border = BorderStroke(1.dp, EmeraldSecondary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Onaylandı",
                                tint = EmeraldSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Mali Müşavir Şahan Can SARKI tarafından onaylandı ve mühürlendi.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "SMMM Sicil No: 34-87462 • Doğrulama Kodu: GK-76A3-B9",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Buttons (Download PDF or Request Report)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        if (uiState.latestReport != null) {
                            viewModel.downloadReport()
                        } else {
                            viewModel.generateReport()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    enabled = !uiState.isGeneratingReport && (uiState.steps.isNotEmpty() || uiState.latestReport != null)
                ) {
                    if (uiState.isGeneratingReport) {
                        CircularProgressIndicator(
                            color = Color.White, 
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.roadmap_generating_report), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Spacer(modifier = Modifier.width(8.dp))
                        val buttonText = if (uiState.latestReport != null) "Hazırlık Raporunu İndir (PDF)" else stringResource(R.string.roadmap_download_report)
                        Text(buttonText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Send to CPA / Expert Approval
            if (uiState.latestReport != null && uiState.latestReport?.approvalStatus != com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.ApprovalStatus.SENT) {
                item {
                    OutlinedButton(
                        onClick = { viewModel.sendToExpert() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NavyPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                        enabled = !uiState.isSendingToExpert
                    ) {
                        if (uiState.isSendingToExpert) {
                            CircularProgressIndicator(
                                color = NavyPrimary, 
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(R.string.roadmap_sending_to_expert), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = "Send")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.roadmap_send_to_expert), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepperItem(step: RoadmapStep, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            step.isCompleted -> EmeraldSecondary
                            step.isActive -> NavyPrimary
                            else -> Color(0xFFE4E2E5)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (step.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
                } else if (step.isActive) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                }
            }
            
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(if (step.isCompleted) EmeraldSecondary else Color(0xFFE4E2E5))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.padding(top = 2.dp, bottom = if (isLast) 0.dp else 24.dp)) {
            Text(
                text = step.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (step.isActive) NavyPrimary else Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.description,
                fontSize = 13.sp,
                color = if (step.isActive) Color.DarkGray else Color.LightGray,
                lineHeight = 18.sp
            )

            // Dynamic warning box for active Mali Müşavir approval step
            if (step.isActive && step.title.contains("Müşavir")) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDAD6)),
                    border = BorderStroke(1.dp, ErrorColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Bekliyor",
                            tint = ErrorColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mali Müşavir onayına gönderildi. Yanıt bekleniyor...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStepsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.roadmap_empty_steps),
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}
