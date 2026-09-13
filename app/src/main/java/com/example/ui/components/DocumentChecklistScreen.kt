package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun DocumentChecklistScreen(
    selectedService: String,
    onSelectService: (String) -> Unit,
    checklistState: Map<String, Map<String, Boolean>>,
    onToggleItem: (String, String) -> Unit,
    onSendWhatsApp: (String) -> Unit
) {
    val services = listOf("PAN", "Voter ID ECI", "Passport", "Ration Card")
    val currentChecklist = checklistState[selectedService] ?: emptyMap()
    val allChecked = currentChecklist.isNotEmpty() && currentChecklist.values.all { it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Mandatory Pre-Application Document Checklist",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Gatekeeper Logic: Check all required boxes below to enable direct submission to Prayagi Kendra.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Service selector chips
        ScrollableTabRow(
            selectedTabIndex = services.indexOf(selectedService),
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            services.forEach { service ->
                Tab(
                    selected = selectedService == service,
                    onClick = { onSelectService(service) },
                    text = { Text(service, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_$service")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Checklist Items
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Required Documents for $selectedService:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                currentChecklist.forEach { (docName, isChecked) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onToggleItem(selectedService, docName) },
                                modifier = Modifier.testTag("checkbox_$docName")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = docName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (isChecked) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Gatekeeper Submission Button
                Button(
                    onClick = {
                        if (allChecked) {
                            val msg = "Hello Prayagi Kendra, I have completed all mandatory document checklists for $selectedService and ready to proceed with application!"
                            onSendWhatsApp(msg)
                        }
                    },
                    enabled = allChecked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C853),
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_whatsapp_checklist_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (allChecked) "Send to Prayagi Kendra via WhatsApp" else "Complete All Checklists to Unlock",
                        fontWeight = FontWeight.Bold,
                        color = if (allChecked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
