package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag

@Composable
fun UpssscHighlightCard(
    onWhatsAppClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("upsssc_highlight_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Active Recruitment Summary",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Ref ID: PRAYAGI-2026-JOB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "UPSSSC Constable / PET Recruitment 2026",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Minimum essential details required before form filling at Prayagi Kendra (Naini, Prayagraj).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Essential Info Grid
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Start Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(text = "01 Oct 2026", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column {
                        Text(text = "Last Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(text = "25 Oct 2026", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    }
                    Column {
                        Text(text = "Qualification", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(text = "10th / 12th Pass", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column {
                        Text(text = "Age Limit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(text = "18 - 28 Years", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        onWhatsAppClick("Hello Prayagi Kendra, I need details for UPSSSC Pet Job")
                    },
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("upsssc_whatsapp_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Inquire / Apply @ Prayagi Kendra ↗", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
