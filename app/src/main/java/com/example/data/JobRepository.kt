package com.example.data

import com.example.ui.components.JobAlertItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JobRepository(private val dao: JobAlertDao) {
    val cachedJobs: Flow<List<JobAlertItem>> = dao.getAllJobs().map { entities ->
        if (entities.isEmpty()) {
            getDefaultJobs()
        } else {
            entities.map { e ->
                JobAlertItem(
                    id = e.id,
                    title = e.title,
                    board = e.board,
                    category = e.category,
                    startDate = e.startDate,
                    lastDate = e.lastDate,
                    statusLabel = e.statusLabel,
                    isActive = e.isActive,
                    importantInfo = e.importantInfo,
                    notifiedUpcoming = e.notifiedUpcoming,
                    notifiedExpiring = e.notifiedExpiring
                )
            }
        }
    }

    suspend fun refreshCache(items: List<JobAlertItem>) {
        val entities = items.map { item ->
            JobAlertEntity(
                id = item.id,
                title = item.title,
                board = item.board,
                category = item.category,
                startDate = item.startDate,
                lastDate = item.lastDate,
                statusLabel = item.statusLabel,
                isActive = item.isActive,
                importantInfo = item.importantInfo,
                notifiedUpcoming = item.notifiedUpcoming,
                notifiedExpiring = item.notifiedExpiring
            )
        }
        dao.insertJobs(entities)
    }

    private fun getDefaultJobs(): List<JobAlertItem> = listOf(
        JobAlertItem(title = "UPSSSC PET / Constable Recruitment 2026", board = "UPSSSC", category = "job", statusLabel = "Active", importantInfo = "Minimum 10th Pass, Age 18-28. Apply via Prayagi Kendra."),
        JobAlertItem(title = "SSC CGL Tier-2 Admit Card 2026", board = "SSC", category = "admit", statusLabel = "Exam: 02 Nov", importantInfo = "Download admit card and print at Prayagi Kendra."),
        JobAlertItem(title = "Railway RRB NTPC Final Result 2026", board = "Railway", category = "result", statusLabel = "Declared", importantInfo = "Check scorecards and merit list."),
        JobAlertItem(title = "UP Police Constable Re-Exam 2026", board = "Police", category = "job", statusLabel = "Expiring in 5 Days", importantInfo = "Last date approaching fast. Hurry!"),
        JobAlertItem(title = "UPPSC RO/ARO Prelims Admit Card", board = "UPPSC", category = "admit", statusLabel = "Available", importantInfo = "Exam center location verification available."),
        JobAlertItem(title = "SSC CHSL Final Result 2026", board = "SSC", category = "result", statusLabel = "Declared", importantInfo = "Check final cut-off and selection status."),
        JobAlertItem(title = "PM Kisan Samman Nidhi 19th Installment Verification", board = "Govt of India", category = "scheme", statusLabel = "Announcement", importantInfo = "Ensure Aadhaar-bank seeding and e-KYC are complete at Prayagi Kendra.")
    )
}
