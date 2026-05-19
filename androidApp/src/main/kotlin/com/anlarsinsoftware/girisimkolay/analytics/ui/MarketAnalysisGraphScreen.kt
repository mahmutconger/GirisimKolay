package com.anlarsinsoftware.girisimkolay.analytics.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.ui.theme.NavyPrimary
import com.anlarsinsoftware.girisimkolay.ui.theme.EmeraldSecondary
import com.anlarsinsoftware.girisimkolay.ui.theme.OutlineVariant
import com.anlarsinsoftware.girisimkolay.ui.theme.SurfaceContainerLow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketAnalysisGraphScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: () -> Unit
) {
    var selectedTimeRange by remember { mutableStateOf("5Y") }
    var selectedBarIndex by remember { mutableStateOf(-1) }

    val barData = listOf(
        BarInfo("2019", 0.40f, "45M₺ / +8%"),
        BarInfo("2020", 0.50f, "52M₺ / +15%"),
        BarInfo("2021", 0.65f, "68M₺ / +30%"),
        BarInfo("2022", 0.80f, "85M₺ / +25%"),
        BarInfo("2023(T)", 0.90f, "95M₺ / +11%", isEst = true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pazar Analizi 2024", fontWeight = FontWeight.Bold, color = NavyPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = NavyPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToDetail) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Detay", tint = NavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBF9FB))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- Header Section ---
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pazar Analizi Grafiği",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Son 5 yıllık sektör büyüme trendleri ve projeksiyonları.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // --- Bento Chart Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Legends
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            LegendItem("Pazar Hacmi (M₺)", NavyPrimary)
                            LegendItem("Büyüme Hızı (%)", EmeraldSecondary)
                        }

                        // Time range segmented selectors
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLow)
                                .padding(2.dp)
                        ) {
                            listOf("1Y", "3Y", "5Y", "Max").forEach { range ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selectedTimeRange == range) Color.White else Color.Transparent)
                                        .clickable { selectedTimeRange = range }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = range,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTimeRange == range) NavyPrimary else Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Chart Canvas Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        // Horizontal Gridlines & Y-Axis Labels
                        Column(
                            modifier = Modifier.fillMaxSize().padding(start = 40.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            val labels = listOf("100M", "75M", "50M", "25M", "0")
                            labels.forEach { label ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.width(35.dp)
                                    )
                                    HorizontalDivider(
                                        color = OutlineVariant.copy(alpha = 0.3f),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Chart Columns / Bars
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 75.dp, bottom = 24.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            barData.forEachIndexed { index, bar ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable {
                                            selectedBarIndex = if (selectedBarIndex == index) -1 else index
                                        },
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        // Tooltip popup
                                        if (selectedBarIndex == index) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(NavyPrimary)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(bar.tooltip, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                        }

                                        // Bar column
                                        Box(
                                            modifier = Modifier
                                                .width(28.dp)
                                                .fillMaxHeight(bar.height)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(
                                                    if (bar.isEst) Color(0xFF1A2B48) else NavyPrimary
                                                )
                                                .then(
                                                    if (bar.isEst) Modifier.border(2.dp, NavyPrimary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)) else Modifier
                                                )
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(bar.year, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    }
                                }
                            }
                        }

                        // Trend Curve (drawn with custom Canvas Path overlay)
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 75.dp, bottom = 24.dp, end = 16.dp)
                        ) {
                            val w = size.width
                            val h = size.height

                            val point1 = Offset(w * 0.1f, h * 0.65f)
                            val point2 = Offset(w * 0.3f, h * 0.55f)
                            val point3 = Offset(w * 0.5f, h * 0.40f)
                            val point4 = Offset(w * 0.7f, h * 0.25f)
                            val point5 = Offset(w * 0.9f, h * 0.15f)

                            val path = Path().apply {
                                moveTo(point1.x, point1.y)
                                quadraticTo((point1.x + point2.x) / 2, (point1.y + point2.y) / 2, point2.x, point2.y)
                                quadraticTo((point2.x + point3.x) / 2, (point2.y + point3.y) / 2, point3.x, point3.y)
                                quadraticTo((point3.x + point4.x) / 2, (point3.y + point4.y) / 2, point4.x, point4.y)
                                quadraticTo((point4.x + point5.x) / 2, (point4.y + point5.y) / 2, point5.x, point5.y)
                            }

                            drawPath(
                                path = path,
                                color = EmeraldSecondary,
                                style = Stroke(width = 6f)
                            )

                            // Trend nodes
                            val points = listOf(point1, point2, point3, point4, point5)
                            points.forEachIndexed { idx, point ->
                                drawCircle(
                                    color = EmeraldSecondary,
                                    radius = 10f,
                                    center = point
                                )
                                if (idx == points.size - 1) {
                                    drawCircle(
                                        color = Color.White,
                                        radius = 6f,
                                        center = point
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- Key Data Points (Bento Cards) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CAGR Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Yıllık Büyüme (CAGR)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = EmeraldSecondary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("%22.4", fontSize = 22.sp, fontWeight = FontWeight.Black, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = EmeraldSecondary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sektörün üzerinde", fontSize = 11.sp, color = EmeraldSecondary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Volume Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tahmini Hacim", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Icon(Icons.Default.Wallet, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("95M₺", fontSize = 22.sp, fontWeight = FontWeight.Black, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Son çeyrek verisi", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            // --- Detailed Data Table ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow)
                            .padding(16.dp)
                    ) {
                        Text("Detaylı Veri Tablosu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    }
                    
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Headers
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Yıl", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1f))
                            Text("Hacim (M₺)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1.2f))
                            Text("Büyüme (%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1.2f))
                            Text("Pazar Payı", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1.2f))
                        }
                        
                        HorizontalDivider(color = Color(0xFFF0EFF1))

                        TableRow("2022", "85.0", "+25.0%", "%12.4")
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        TableRow("2021", "68.0", "+30.7%", "%10.1")
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        TableRow("2020", "52.0", "+15.5%", "%8.5")
                        HorizontalDivider(color = Color(0xFFF0EFF1))
                        TableRow("2019", "45.0", "-", "%7.2")
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Composable
fun TableRow(
    year: String,
    volume: String,
    growth: String,
    share: String
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(year, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary, modifier = Modifier.weight(1f))
        Text(volume, fontSize = 13.sp, color = NavyPrimary, modifier = Modifier.weight(1.2f))
        Text(
            text = growth, 
            fontSize = 13.sp, 
            color = if (growth.startsWith("+")) EmeraldSecondary else NavyPrimary, 
            modifier = Modifier.weight(1.2f)
        )
        Text(share, fontSize = 13.sp, color = NavyPrimary, modifier = Modifier.weight(1.2f))
    }
}

data class BarInfo(
    val year: String,
    val height: Float,
    val tooltip: String,
    val isEst: Boolean = false
)
