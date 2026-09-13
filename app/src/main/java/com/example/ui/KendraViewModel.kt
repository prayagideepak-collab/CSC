package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PrayagiDatabase
import com.example.data.JobRepository
import com.example.ui.components.JobAlertItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collectLatest

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
    val jobAlertItems: List<JobAlertItem> = emptyList()
)

data class ChatMessage(val text: String, val isUser: Boolean)

class KendraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: JobRepository
    private val _state = MutableStateFlow(KendraUiState())
    val state: StateFlow<KendraUiState> = _state.asStateFlow()

    init {
        val db = PrayagiDatabase.getDatabase(application)
        repository = JobRepository(db.jobAlertDao())

        viewModelScope.launch {
            // Seed initial data if empty
            val initialJobs = listOf(
                JobAlertItem(title = "UPSSSC PET / Constable Recruitment 2026", board = "UPSSSC", category = "job", statusLabel = "Active", importantInfo = "Minimum 10th Pass, Age 18-28. Apply via Prayagi Kendra."),
                JobAlertItem(title = "SSC CGL Tier-2 Admit Card 2026", board = "SSC", category = "admit", statusLabel = "Exam: 02 Nov", importantInfo = "Download admit card and print at Prayagi Kendra."),
                JobAlertItem(title = "Railway RRB NTPC Final Result 2026", board = "Railway", category = "result", statusLabel = "Declared", importantInfo = "Check scorecards and merit list."),
                JobAlertItem(title = "UP Police Constable Re-Exam 2026", board = "Police", category = "job", statusLabel = "Expiring in 5 Days", importantInfo = "Last date approaching fast. Hurry!"),
                JobAlertItem(title = "UPPSC RO/ARO Prelims Admit Card", board = "UPPSC", category = "admit", statusLabel = "Available", importantInfo = "Exam center location verification available."),
                JobAlertItem(title = "SSC CHSL Final Result 2026", board = "SSC", category = "result", statusLabel = "Declared", importantInfo = "Check final cut-off and selection status.")
            )
            repository.refreshCache(initialJobs)

            repository.cachedJobs.collectLatest { cached ->
                _state.update { it.copy(jobAlertItems = cached) }
            }
        }
    }

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
                "Prayagi Jan Seva Kendra operates via digital document checklists and direct government portal links."
            q.contains("ration") || q.contains("family") -> 
                "For Ration Card, family members under 5 years old are excluded from the main ration count. Non-twin members require at least 9 months (270 days) gap between birth dates."
            q.contains("pan") || q.contains("voter") || q.contains("passport") || q.contains("caste") || q.contains("income") -> 
                "You can check document checklists and official government tracking portals directly in our app tabs."
            q.contains("upi") || q.contains("pay") || q.contains("qr") -> 
                "Official UPI VPA: 9235939809@okbizaxis (Prayagi Kendra). Pay via QR or select service amount."
            else -> 
                "Thank you for visiting Prayagi Jan Seva Kendra! Please check our document checklists and payment tabs for digital services."
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
