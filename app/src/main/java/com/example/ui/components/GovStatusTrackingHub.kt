package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun GovStatusTrackingHub(
    selectedTab: String,
    onSelectTab: (String) -> Unit,
    onOpenPortal: (String) -> Unit,
    onWhatsAppFallback: (String) -> Unit
) {
    val tabs = mapOf(
        "PAN (NSDL/UTI)" to "https://www.tin-nsdl.com/",
        "Voter ID (ECI)" to "https://voters.eci.gov.in/",
        "UP e-District" to "https://edistrict.up.gov.in/",
        "Driving Licence" to "https://sarathi.parivahan.gov.in/",
        "Passport" to "https://www.passportindia.gov.in/"
    )

    val portalUrl = tabs[selectedTab] ?: "https://www.google.com"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Unified Government Status Tracking Hub",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Check official status portals or request assistance via Prayagi WhatsApp fallback.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Exact Coordinates Map Navigation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Prayagi Kendra Exact GPS Pin", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text("Naini, Prayagraj, UP (25.410485, 81.8766628)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("GPS Lock", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val navUrl = "https://www.google.com/maps/search/?api=1&query=25.410485,81.8766628"
                            onOpenPortal(navUrl)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("start_gps_nav_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Start Navigation ↗", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            val mapPinUrl = "https://maps.google.com/?q=25.410485,81.8766628"
                            val msg = "Hello, here is the exact location map pin for Prayagi Store And Services (Naini, Prayagraj):\n$mapPinUrl"
                            onWhatsAppFallback(msg)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_pin_whatsapp_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) {
                        Text("Share Pin via WhatsApp")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = tabs.keys.toList().indexOf(selectedTab),
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.keys.forEach { tabName ->
                Tab(
                    selected = selectedTab == tabName,
                    onClick = { onSelectTab(tabName) },
                    text = { Text(tabName, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tracking_tab_$tabName")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Portal: $selectedTab",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = portalUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onOpenPortal(portalUrl) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_official_portal_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Open Portal", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Redirect to Official Portal", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val msg = "Hello Prayagi Kendra, I need status tracking assistance for $selectedTab. Please check my application status."
                        onWhatsAppFallback(msg)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("whatsapp_fallback_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Prayagi WhatsApp Status Fallback", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

