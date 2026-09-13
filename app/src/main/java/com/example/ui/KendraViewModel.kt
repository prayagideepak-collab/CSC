package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.components.JobAlertItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit

data class FamilyMember(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val dob: String = "", // YYYY-MM-DD
    val gender: String = "Male"
)

data class KendraUiState(
    // Watermark
    val showWatermark: Boolean = false,
    
    // Checklist State
    val selectedService: String = "PAN",
    val checklistState: Map<String, Map<String, Boolean>> = mapOf(
        "PAN" to mapOf("Aadhaar Card" to false, "Passport Photo" to false, "Mobile Linked Aadhaar" to false),
        "Voter ID ECI" to mapOf("Aadhaar Card" to false, "Age Proof (18+)" to false, "Passport Photo" to false, "Address Proof" to false),
        "Passport" to mapOf("Aadhaar Card" to false, "PAN Card" to false, "Birth Certificate / 10th Marksheet" to false, "Non-ECR proof (if applicable)" to false),
        "Ration Card" to mapOf("Head Aadhaar Card" to false, "All Family Aadhaar Copies" to false, "Bank Passbook Copy" to false, "Family Group Photo" to false)
    ),
    
    // Ration Card Validation State
    val declaredAadhaarCount: Int = 4,
    val familyMembers: List<FamilyMember> = listOf(
        FamilyMember(name = "Rajesh Kumar", dob = "1985-05-10", gender = "Male"),
        FamilyMember(name = "Sunita Devi", dob = "1988-08-15", gender = "Female"),
        FamilyMember(name = "Aarav Kumar", dob = "2023-01-01", gender = "Male"), // < 5 years
        FamilyMember(name = "Priya Kumar", dob = "2023-01-01", gender = "Female") // Twin duplicate DOB
    ),
    val twinConfirmed: Boolean = false,
    
    // UPI Payment State
    val selectedUpiAmount: Int = 150,
    val customUpiAmount: String = "",
    val upiStatus: String = "Pending", // Pending, Paid
    
    // Tracking Hub
    val selectedTrackingTab: String = "PAN (NSDL/UTI)",
    
    // Chat State
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage("Namaste! Welcome to Prayagi Jan Seva Kendra. How can I assist you with your document or government service today?", false)
    ),
    val chatInput: String = "",
    val isAiThinking: Boolean = false,

    // Biometric Security State
    val isBiometricUnlocked: Boolean = false,

    // Job & Civic Alert State
    val jobAlertsTab: Int = 0,
    val jobSearchQuery: String = "",
    val expandedJobDocsId: String? = null,
    val jobAlertItems: List<JobAlertItem> = listOf(
        JobAlertItem(title = "UPSSSC PET / Constable Recruitment 2026", board = "UPSSSC", date = "Last Date: 25 Oct 2026", type = "job"),
        JobAlertItem(title = "SSC CGL Tier-2 Admit Card 2026", board = "SSC", date = "Exam: 02 Nov 2026", type = "admit"),
        JobAlertItem(title = "Railway RRB NTPC Final Result 2026", board = "Railway", date = "Declared: Recent", type = "result"),
        JobAlertItem(title = "UP Police Constable Re-Exam 2026", board = "Police", date = "Last Date: 10 Nov 2026", type = "job"),
        JobAlertItem(title = "UPPSC RO/ARO Prelims Admit Card", board = "UPPSC", date = "Exam: 18 Nov 2026", type = "admit"),
        JobAlertItem(title = "SSC CHSL Final Result 2026", board = "SSC", date = "Declared: Yesterday", type = "result")
    )
)

data class ChatMessage(val text: String, val isUser: Boolean)

class KendraViewModel : ViewModel() {
    private val _state = MutableStateFlow(KendraUiState())
    val state: StateFlow<KendraUiState> = _state.asStateFlow()

    fun toggleWatermark() {
        _state.update { it.copy(showWatermark = !it.showWatermark) }
    }

    fun setSelectedService(service: String) {
        _state.update { it.copy(selectedService = service) }
    }

    fun toggleChecklistItem(service: String, itemKey: String) {
        _state.update { current ->
            val currentMap = current.checklistState[service] ?: emptyMap()
            val updatedMap = currentMap.toMutableMap().apply {
                this[itemKey] = !(this[itemKey] ?: false)
            }
            current.copy(checklistState = current.checklistState + (service to updatedMap))
        }
    }

    fun setDeclaredAadhaarCount(count: Int) {
        _state.update { it.copy(declaredAadhaarCount = count) }
    }

    fun addFamilyMember(member: FamilyMember) {
        _state.update { it.copy(familyMembers = it.familyMembers + member) }
    }

    fun updateFamilyMember(index: Int, member: FamilyMember) {
        _state.update { current ->
            val list = current.familyMembers.toMutableList()
            if (index in list.indices) {
                list[index] = member
            }
            current.copy(familyMembers = list)
        }
    }

    fun removeFamilyMember(index: Int) {
        _state.update { current ->
            val list = current.familyMembers.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            current.copy(familyMembers = list)
        }
    }

    fun setTwinConfirmed(confirmed: Boolean) {
        _state.update { it.copy(twinConfirmed = confirmed) }
    }

    fun setUpiAmount(amount: Int) {
        _state.update { it.copy(selectedUpiAmount = amount) }
    }

    fun setCustomUpiAmount(amount: String) {
        _state.update { it.copy(customUpiAmount = amount) }
    }

    fun setUpiStatus(status: String) {
        _state.update { it.copy(upiStatus = status) }
    }

    fun setSelectedTrackingTab(tab: String) {
        _state.update { it.copy(selectedTrackingTab = tab) }
    }

    fun setChatInput(input: String) {
        _state.update { it.copy(chatInput = input) }
    }

    fun sendChatMessage(context: Context, userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(userText, true)
        _state.update { it.copy(chatMessages = it.chatMessages + userMsg, chatInput = "", isAiThinking = true) }

        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            kotlinx.coroutines.delay(1000)
            val replyText = generateAiResponse(userText)
            _state.update { it.copy(chatMessages = it.chatMessages + ChatMessage(replyText, false), isAiThinking = false) }
        }
    }

    private fun generateAiResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("time") || q.contains("timing") || q.contains("open") -> 
                "Prayagi Jan Seva Kendra timings: Morning 10:00 AM - 2:00 PM, Evening 4:00 PM - 7:00 PM. Sundays are Closed."
            q.contains("helpline") || q.contains("call") || q.contains("number") -> 
                "Official Helpline: 9235939809 (Call or WhatsApp wa.me/919235939809). No personal numbers used."
            q.contains("ration") || q.contains("family") -> 
                "For Ration Card, family members under 5 years old are excluded from the main ration count. Non-twin members require at least 9 months (270 days) gap between birth dates."
            q.contains("pan") || q.contains("voter") || q.contains("passport") || q.contains("caste") || q.contains("income") -> 
                "You can check document checklists and official government tracking portals directly in our app tabs. For direct assistance, WhatsApp us at 9235939809!"
            q.contains("upi") || q.contains("pay") || q.contains("qr") -> 
                "Official UPI VPA: 9235939809@okbizaxis (Prayagi Kendra). Pay via QR or select service amount and share screenshot on WhatsApp."
            else -> 
                "Thank you for contacting Prayagi Jan Seva Kendra! For immediate service, please call or WhatsApp 9235939809 or check our document checklists and payment tabs."
        }
    }

    fun makePhoneCall(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9235939809"))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openWhatsApp(context: Context, message: String) {
        try {
            val url = "https://wa.me/919235939809?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBiometricUnlocked(unlocked: Boolean) {
        _state.update { it.copy(isBiometricUnlocked = unlocked) }
    }

    fun setJobAlertsTab(tab: Int) {
        _state.update { it.copy(jobAlertsTab = tab) }
    }

    fun setJobSearchQuery(query: String) {
        _state.update { it.copy(jobSearchQuery = query) }
    }

    fun toggleJobDocsExpansion(id: String) {
        _state.update { it.copy(expandedJobDocsId = if (it.expandedJobDocsId == id) null else id) }
    }
}
