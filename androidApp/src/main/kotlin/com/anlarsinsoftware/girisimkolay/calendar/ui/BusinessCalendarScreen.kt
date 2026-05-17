package com.anlarsinsoftware.girisimkolay.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anlarsinsoftware.girisimkolay.calendar.domain.entity.CalendarEvent
import com.anlarsinsoftware.girisimkolay.calendar.domain.entity.EventType
import com.anlarsinsoftware.girisimkolay.calendar.viewmodel.CalendarViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessCalendarScreen(viewModel: CalendarViewModel = koinViewModel()) {
    val events by viewModel.events.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("İşletme Takvimi", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Simulated Monthly Calendar Widget
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                MockCalendarWidget(events)
            }

            Text(
                text = "Kritik Görevler",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    EventListItem(event)
                }
            }
        }
    }
}

@Composable
fun MockCalendarWidget(events: List<CalendarEvent>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Mayıs 2026",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        val daysOfWeek = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            daysOfWeek.forEach { day ->
                Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        // Mocking a few days
        val days = (1..31).toList()
        val weeks = days.chunked(7)
        
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                week.forEach { day ->
                    val dayEvents = events.filter { 
                        // Mock mapping for demo: event 1 on 15th, event 2 on 20th
                        (it.id == "1" && day == 15) || (it.id == "2" && day == 20) || (it.id == "3" && day == 25)
                    }
                    CalendarDayItem(day.toString(), dayEvents)
                }
                // Pad empty days
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(day: String, events: List<CalendarEvent>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.size(36.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (day == "15") MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
        ) {
            Text(text = day, fontSize = 14.sp, fontWeight = if (day == "15") FontWeight.Bold else FontWeight.Normal)
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(top = 2.dp)) {
            events.forEach { event ->
                val color = when(event.type) {
                    EventType.TAX_DEADLINE -> Color(0xFFE53935) // Red
                    EventType.GRANT_WINDOW -> Color(0xFF4CAF50) // Green
                    EventType.DAILY_TASK -> Color(0xFFFFC107) // Yellow
                }
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(color))
            }
        }
    }
}

@Composable
fun EventListItem(event: CalendarEvent) {
    val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale("tr", "TR"))
    val dateString = formatter.format(Date(event.dateMillis))
    
    val indicatorColor = when(event.type) {
        EventType.TAX_DEADLINE -> Color(0xFFE53935)
        EventType.GRANT_WINDOW -> Color(0xFF4CAF50)
        EventType.DAILY_TASK -> Color(0xFFFFC107)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (event.isCritical) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (event.isCritical) 4.dp else 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(12.dp).clip(CircleShape).background(indicatorColor)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateString,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
