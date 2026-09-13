package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun ShopStatusBar(
    showWatermark: Boolean,
    onToggleWatermark: () -> Unit
) {
    // Injecting India Standard Time (IST UTC+5:30) offset calculation for accurate shop hours evaluation
    val istZone = ZoneId.of("Asia/Kolkata")
    val istTime = ZonedDateTime.now(istZone)
    val dayOfWeek = istTime.dayOfWeek
    val time = istTime.toLocalTime()

    val isSunday = dayOfWeek == DayOfWeek.SUNDAY
    val isMorning = !time.isBefore(LocalTime.of(10, 0)) && time.isBefore(LocalTime.of(14, 0))
    val isEvening = !time.isBefore(LocalTime.of(16, 0)) && time.isBefore(LocalTime.of(19, 0))

    val isOpen = !isSunday && (isMorning || isEvening)
    val statusText = when {
        isSunday -> "CLOSED (Sunday Holiday)"
        isMorning -> "OPEN (Morning Shift: 10AM - 2PM)"
        isEvening -> "OPEN (Evening Shift: 4PM - 7PM)"
        else -> "CLOSED (Outside Shift Hours)"
    }

    val badgeColor = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828)
    val badgeBg = if (isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

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
                        Text(
                            text = "Prayagi Jan Seva Kendra (IST)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
