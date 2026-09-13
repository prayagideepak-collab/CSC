package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.KendraViewModel
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : androidx.fragment.app.FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: KendraViewModel = viewModel()
                val state by viewModel.state.collectAsState()
                val context = LocalContext.current

                var currentTab by remember { mutableStateOf(0) }

                if (!state.isBiometricUnlocked) {
                    BiometricLockScreen(
                        onUnlockSuccess = { viewModel.setBiometricUnlocked(true) }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                Column {
                                    ShopStatusBar(
                                        showWatermark = state.showWatermark,
                                        onToggleWatermark = { viewModel.toggleWatermark() }
                                    )
                                    HelplineBar(
                                        onCallClick = { viewModel.makePhoneCall(context) },
                                        onWhatsAppClick = { viewModel.openWhatsApp(context, "Hello Prayagi Kendra, I am contacting you from the official app regarding government services.") }
                                    )
                                }
                            },
                            bottomBar = {
                                NavigationBar {
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Checklist, contentDescription = "Checklists") },
                                        label = { Text("Checklist") },
                                        selected = currentTab == 0,
                                        onClick = { currentTab = 0 },
                                        modifier = Modifier.testTag("nav_checklist")
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.FamilyRestroom, contentDescription = "Ration") },
                                        label = { Text("Ration") },
                                        selected = currentTab == 1,
                                        onClick = { currentTab = 1 },
                                        modifier = Modifier.testTag("nav_ration")
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Payment, contentDescription = "UPI") },
                                        label = { Text("UPI Pay") },
                                        selected = currentTab == 2,
                                        onClick = { currentTab = 2 },
                                        modifier = Modifier.testTag("nav_upi")
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.TrackChanges, contentDescription = "Tracking") },
                                        label = { Text("Gov Hub") },
                                        selected = currentTab == 3,
                                        onClick = { currentTab = 3 },
                                        modifier = Modifier.testTag("nav_hub")
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI Assistant") },
                                        label = { Text("AI Help") },
                                        selected = currentTab == 4,
                                        onClick = { currentTab = 4 },
                                        modifier = Modifier.testTag("nav_ai")
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Work, contentDescription = "Jobs") },
                                        label = { Text("Jobs") },
                                        selected = currentTab == 5,
                                        onClick = { currentTab = 5 },
                                        modifier = Modifier.testTag("nav_jobs")
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (currentTab) {
                                    0 -> DocumentChecklistScreen(
                                        selectedService = state.selectedService,
                                        onSelectService = { viewModel.setSelectedService(it) },
                                        checklistState = state.checklistState,
                                        onToggleItem = { service, item -> viewModel.toggleChecklistItem(service, item) },
                                        onSendWhatsApp = { msg -> viewModel.openWhatsApp(context, msg) }
                                    )
                                    1 -> RationCardValidationScreen(
                                        declaredAadhaarCount = state.declaredAadhaarCount,
                                        onUpdateDeclaredCount = { viewModel.setDeclaredAadhaarCount(it) },
                                        familyMembers = state.familyMembers,
                                        onAddMember = { viewModel.addFamilyMember(it) },
                                        onUpdateMember = { idx, mem -> viewModel.updateFamilyMember(idx, mem) },
                                        onRemoveMember = { idx -> viewModel.removeFamilyMember(idx) },
                                        twinConfirmed = state.twinConfirmed,
                                        onToggleTwinConfirmed = { viewModel.setTwinConfirmed(it) }
                                    )
                                    2 -> UpiPaymentScreen(
                                        selectedAmount = state.selectedUpiAmount,
                                        customAmount = state.customUpiAmount,
                                        upiStatus = state.upiStatus,
                                        onSelectAmount = { viewModel.setUpiAmount(it) },
                                        onCustomAmountChange = { viewModel.setCustomUpiAmount(it) },
                                        onSetUpiStatus = { viewModel.setUpiStatus(it) },
                                        onShareWhatsAppScreenshot = { msg -> viewModel.openWhatsApp(context, msg) }
                                    )
                                    3 -> GovStatusTrackingHub(
                                        selectedTab = state.selectedTrackingTab,
                                        onSelectTab = { viewModel.setSelectedTrackingTab(it) },
                                        onOpenPortal = { url -> viewModel.openUrl(context, url) },
                                        onWhatsAppFallback = { msg -> viewModel.openWhatsApp(context, msg) }
                                    )
                                    4 -> PrayagiAiChatScreen(
                                        messages = state.chatMessages,
                                        input = state.chatInput,
                                        onInputChanged = { viewModel.setChatInput(it) },
                                        onSendMessage = { txt -> viewModel.sendChatMessage(context, txt) },
                                        isThinking = state.isAiThinking
                                    )
                                    5 -> CivicJobAlertDashboard(
                                        selectedTab = state.jobAlertsTab,
                                        onSelectTab = { viewModel.setJobAlertsTab(it) },
                                        searchQuery = state.jobSearchQuery,
                                        onSearchQueryChange = { viewModel.setJobSearchQuery(it) },
                                        items = state.jobAlertItems,
                                        expandedDocsId = state.expandedJobDocsId,
                                        onToggleDocs = { id -> viewModel.toggleJobDocsExpansion(id) },
                                        onApplyWhatsApp = { title, category ->
                                            val msg = "Hello Prayagi Kendra, I need help applying for $category: $title"
                                            viewModel.openWhatsApp(context, msg)
                                        }
                                    )
                                }
                            }
                        }

                        // Watermark Background Toggle Overlay
                        if (state.showWatermark) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "PRAYAGI STORE & SERVICES • JAN SEVA KENDRA • 9235939809",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    modifier = Modifier.rotate(45f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
