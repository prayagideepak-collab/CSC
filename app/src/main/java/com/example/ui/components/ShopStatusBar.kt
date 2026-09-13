package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ShopStatusBar(
    showWatermark: Boolean,
    onToggleWatermark: () -> Unit
) {
    val istZone = ZoneId.of("Asia/Kolkata")
    var currentTime by remember { mutableStateOf(ZonedDateTime.now(istZone)) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = ZonedDateTime.now(istZone)
            delay(1000L)
        }
    }

    val dayOfWeek = currentTime.dayOfWeek
    val time = currentTime.toLocalTime()

    val isSunday = dayOfWeek == DayOfWeek.SUNDAY
    val openTime = LocalTime.of(10, 0)
    val closeTime = LocalTime.of(19, 0) // 7:00 PM

    val isOpen = !isSunday && !time.isBefore(openTime) && time.isBefore(closeTime)
    
    val statusText = when {
        isSunday -> "CLOSED - Sunday"
        time.isBefore(openTime) -> "Opens at 10:00 AM"
        time.isAfter(closeTime) || time == closeTime -> "Closed for today (Opens 10 AM)"
        else -> "OPEN - Closes at 7:00 PM"
    }

    val badgeColor = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828)
    val badgeBg = if (isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Shop Hours",
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Prayagi Jan Seva Kendra",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "IST: ${currentTime.format(timeFormatter)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            color = badgeBg,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = statusText,
                                color = badgeColor,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Watermark Toggle
                IconButton(
                    onClick = onToggleWatermark,
                    modifier = Modifier.testTag("watermark_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Toggle Watermark",
                        tint = if (showWatermark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
