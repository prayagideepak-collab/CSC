package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class JobAlertItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val board: String, // SSC, UPSSSC, Railway, Police, UPPSC
    val date: String,
    val type: String, // "job", "admit", "result"
    val docsChecklist: List<String> = listOf("Aadhaar Card", "Passport Photo", "Candidate Signature", "Educational Marksheet", "Category Certificate (if applicable)")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivicJobAlertDashboard(
    selectedTab: Int, // 0: Jobs, 1: Admit Card, 2: Results
    onSelectTab: (Int) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    items: List<JobAlertItem>,
    expandedDocsId: String?,
    onToggleDocs: (String) -> Unit,
    onApplyWhatsApp: (String, String) -> Unit
) {
    val tabTitles = listOf("Latest Jobs", "Latest Admit Card", "Latest Results")
    val filteredItems = items.filter { item ->
        val matchesTab = when (selectedTab) {
            0 -> item.type == "job"
            1 -> item.type == "admit"
            2 -> item.type == "result"
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.board.contains(searchQuery, ignoreCase = true)
        matchesTab && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Prayagi Civic & Job Alert Dashboard",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Official notifications, admit cards & results. Apply or print directly via Prayagi Kendra (Naini, Prayagraj).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Search & Filter Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("job_search_input"),
            placeholder = { Text("Search by board (SSC, UPSSSC, Railway, Police)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onSelectTab(index) },
                    text = { Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) },
                    modifier = Modifier.testTag("job_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Item List with Highlight Card on Jobs Tab
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Featured Highlight Card for UPSSSC on Jobs tab (when no heavy search query conflicts)
            if (selectedTab == 0 && (searchQuery.isBlank() || "UPSSSC".contains(searchQuery, ignoreCase = true) || "Constable".contains(searchQuery, ignoreCase = true))) {
                item {
                    UpssscHighlightCard(
                        onWhatsAppClick = { msg -> onApplyWhatsApp("UPSSSC Constable / PET Recruitment 2026", msg) }
                    )
                }
            }

            if (filteredItems.isEmpty() && !(selectedTab == 0 && searchQuery.isBlank())) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notifications found matching your search.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                items(filteredItems) { item ->
                    val isExpanded = expandedDocsId == item.id
                    val categoryLabel = when (item.type) {
                        "job" -> "Job Application"
                        "admit" -> "Admit Card Download"
                        else -> "Result Checking"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                        text = item.board,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = item.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Custom CSC Action Footer
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { onApplyWhatsApp(item.title, categoryLabel) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("apply_whatsapp_${item.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                                ) {
                                    Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Apply / Print @ Prayagi Kendra", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedButton(
                                    onClick = { onToggleDocs(item.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .testTag("toggle_docs_${item.id}"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text(if (isExpanded) "Hide Required Docs Checklist" else "Required Docs Checklist ▾", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }

                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("Mandatory Documents Required:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            item.docsChecklist.forEach { doc ->
                                                Row(
                                                    modifier = Modifier.padding(vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Check", tint = Color(0xFF00C853), modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(doc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
