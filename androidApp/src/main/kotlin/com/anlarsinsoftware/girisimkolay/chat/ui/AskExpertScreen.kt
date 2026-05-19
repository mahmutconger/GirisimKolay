package com.anlarsinsoftware.girisimkolay.chat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.UploadFile
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
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OnNavyPrimaryContainer
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskExpertScreen(
    initialSpecialty: String? = null,
    onNavigateBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialSpecialty ?: "") }
    var questionTitle by remember { mutableStateOf("") }
    var detailedDescription by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("standard") } // "standard" or "urgent"
    var expandedDropdown by remember { mutableStateOf(false) }

    val categories = listOf(
        "Mali Müşavir" to "financial",
        "Hukuk Danışmanı" to "legal",
        "Hibe Uzmanı" to "grant",
        "Pazarlama Danışmanı" to "marketing"
    )

    val selectedCategoryLabel = categories.find { it.second == selectedCategory }?.first ?: "Seçiniz..."

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Uzmana Sor",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 20.sp,
                            modifier = Modifier.offset(x = (-24).dp) // offset to balance back button
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = NavyPrimary)
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
                        onClick = {
                            if (selectedCategory.isNotEmpty() && questionTitle.isNotEmpty() && detailedDescription.isNotEmpty()) {
                                onSubmitSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Soruyu Gönder",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBF9FB))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Context Header
            Text(
                text = "Doğru uzmana ulaşın ve işletmeniz için profesyonel destek alın.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Category Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "UZMANLIK ALANI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { expandedDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCategoryLabel,
                                color = if (selectedCategory.isEmpty()) Color.LightGray else NavyPrimary,
                                fontSize = 15.sp
                            )
                            Icon(
                                Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        categories.forEach { (label, value) ->
                            DropdownMenuItem(
                                text = { Text(label, color = NavyPrimary) },
                                onClick = {
                                    selectedCategory = value
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Question Title
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SORU BAŞLIĞI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = questionTitle,
                    onValueChange = { questionTitle = it },
                    placeholder = { Text("Kısaca ne hakkında?", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = NavyPrimary,
                        unfocusedBorderColor = OutlineVariant,
                        focusedTextColor = NavyPrimary,
                        unfocusedTextColor = NavyPrimary
                    ),
                    singleLine = true
                )
            }

            // Detailed Description
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DETAYLI AÇIKLAMA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = detailedDescription,
                    onValueChange = { detailedDescription = it },
                    placeholder = { Text("Lütfen durumunuzu detaylı bir şekilde açıklayın...", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = NavyPrimary,
                        unfocusedBorderColor = OutlineVariant,
                        focusedTextColor = NavyPrimary,
                        unfocusedTextColor = NavyPrimary
                    ),
                    maxLines = 6
                )
            }

            // Document Upload Area
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "İLGİLİ BELGELER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(2.dp, OutlineVariant),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { /* Simulate File Chooser */ }
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NavyPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = OnNavyPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belge Yükle",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PDF, JPG veya PNG (Maks 10MB)",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Priority Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ÖNCELİK DURUMU",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Standard Card Option
                    val standardSelected = selectedPriority == "standard"
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPriority = "standard" },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (standardSelected) NavyPrimaryContainer else Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (standardSelected) NavyPrimary else OutlineVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Standart",
                                fontWeight = FontWeight.Bold,
                                color = if (standardSelected) OnNavyPrimaryContainer else Color.Gray,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "24-48 Saat",
                                fontSize = 11.sp,
                                color = if (standardSelected) OnNavyPrimaryContainer.copy(alpha = 0.8f) else Color.LightGray
                            )
                        }
                    }

                    // Urgent Card Option
                    val urgentSelected = selectedPriority == "urgent"
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPriority = "urgent" },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (urgentSelected) Color(0xFFFFDAD6) else Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (urgentSelected) Color(0xFFBA1A1A) else OutlineVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Acil",
                                fontWeight = FontWeight.Bold,
                                color = if (urgentSelected) Color(0xFFBA1A1A) else Color.Gray,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "1-4 Saat",
                                fontSize = 11.sp,
                                color = if (urgentSelected) Color(0xFFBA1A1A).copy(alpha = 0.8f) else Color.LightGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
