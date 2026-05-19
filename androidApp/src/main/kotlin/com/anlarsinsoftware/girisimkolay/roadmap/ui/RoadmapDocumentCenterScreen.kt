package com.anlarsinsoftware.girisimkolay.roadmap.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Send
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

    // Hata kodlarını kullanıcı dostu mesajlara çeviriyoruz
    val errorMessage = when (uiState.error?.code) {
        "unauthenticated" -> stringResource(R.string.roadmap_error_unauthenticated)
        "missing_session" -> stringResource(R.string.roadmap_error_missing_session)
        "missing_token" -> stringResource(R.string.roadmap_error_missing_token)
        "report_generate_failed" -> stringResource(R.string.roadmap_error_report_failed)
        null -> null
        else -> uiState.error?.message ?: stringResource(R.string.roadmap_error_generic)
    }

    // Hata mesajı geldiğinde Snackbar göster
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
                title = { Text(stringResource(R.string.roadmap_title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.roadmap_journey_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
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
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.steps.isEmpty()) {
                item {
                    EmptyStepsView()
                }
            }

            itemsIndexed(
                items = uiState.steps,
                key = { _, step -> step.id }
            ) { index, step ->
                StepperItem(
                    step = step,
                    isLast = index == uiState.steps.size - 1
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                
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
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isGeneratingReport && (uiState.steps.isNotEmpty() || uiState.latestReport != null)
                ) {
                    if (uiState.isGeneratingReport) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary, 
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.roadmap_generating_report), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.roadmap_download_report), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { viewModel.sendToExpert() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isSendingToExpert && uiState.latestReport != null
                ) {
                    if (uiState.isSendingToExpert) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary, 
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.roadmap_sending_to_expert), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.roadmap_send_to_expert), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
fun StepperItem(step: RoadmapStep, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            step.isCompleted -> Color(0xFF4CAF50)
                            step.isActive -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (step.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(16.dp))
                } else if (step.isActive) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onPrimary))
                }
            }
            
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(if (step.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.padding(top = 2.dp, bottom = if (isLast) 0.dp else 24.dp)) {
            Text(
                text = step.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (step.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
