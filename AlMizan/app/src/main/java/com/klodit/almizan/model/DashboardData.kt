package com.klodit.almizan.model

import androidx.compose.ui.graphics.Color
import com.klodit.almizan.ui.theme.*

data class DashboardData(
    val user         : UserData,
    val stats        : List<StatData>,
    val quickActions : List<QuickActionData>,
    val cases        : List<CaseData>
)

data class UserData(
    val name     : String,
    val initials : String,
    val tier     : String
)

data class StatData(
    val id     : String,
    val value  : String,
    val label  : String,
    val accent : String
) {
    fun accentColor(): Color = when (accent) {
        "green"  -> Green500
        "blue"   -> Blue700
        "orange" -> Orange400
        else     -> Navy500
    }
}

data class QuickActionData(
    val id    : String,
    val icon  : String,
    val label : String
)

data class CaseData(
    val id         : String,
    val title      : String,
    val caseNumber : String,
    val status     : String,
    val date       : String,
    val category   : String,
    val wilaya     : String = ""
)

enum class CaseStatus(val label: String, val color: Color) {
    OPEN("Open",               Color(0xFF4CAF50)),
    IN_PROGRESS("In Progress", Color(0xFFFFA726)),
    CLOSED("Closed",           Color(0xFF6B8090)),
    URGENT("Urgent",           Color(0xFFE53935));

    companion object {
        fun from(value: String): CaseStatus =
            entries.firstOrNull { it.name == value } ?: OPEN
    }
}

fun mockDashboardData() = DashboardData(
    user = UserData(
        name     = "CodedTech",
        initials = "CT",
        tier     = "OPEN"
    ),
    stats = listOf(
        StatData(id = "active_cases",      value = "12", label = "Active\nCases",      accent = "green"),
        StatData(id = "upcoming_hearings", value = "3",  label = "Upcoming\nHearings", accent = "blue"),
        StatData(id = "pending_documents", value = "5",  label = "Pending\nDocuments", accent = "orange")
    ),
    quickActions = listOf(
        QuickActionData(id = "new_case",  icon = "add_circle",     label = "New Case"),
        QuickActionData(id = "documents", icon = "description",    label = "Documents"),
        QuickActionData(id = "hearings",  icon = "calendar_today", label = "Hearings"),
        QuickActionData(id = "clients",   icon = "people",         label = "Clients"),
        QuickActionData(id = "reports",   icon = "bar_chart",      label = "Reports"),
        QuickActionData(id = "settings",  icon = "settings",       label = "Settings")
    ),
    cases = listOf(
        CaseData(
            id         = "1",
            title      = "Contract Dispute – Al Noor Ltd.",
            caseNumber = "#2024-CR-0041",
            status     = "URGENT",
            date       = "Mar 14, 2026",
            category   = "Civil",
            wilaya     = "Alger"
        ),
        CaseData(
            id         = "2",
            title      = "Employment Termination Review",
            caseNumber = "#2024-EM-0089",
            status     = "IN_PROGRESS",
            date       = "Mar 10, 2026",
            category   = "Labor",
            wilaya     = "Oran"
        ),
        CaseData(
            id         = "3",
            title      = "Property Ownership Claim",
            caseNumber = "#2024-PR-0012",
            status     = "OPEN",
            date       = "Mar 05, 2026",
            category   = "Real Estate",
            wilaya     = "Constantine"
        ),
        CaseData(
            id         = "4",
            title      = "Intellectual Property Filing",
            caseNumber = "#2023-IP-0067",
            status     = "CLOSED",
            date       = "Dec 20, 2025",
            category   = "IP Law",
            wilaya     = "Annaba"
        ),
        CaseData(
            id         = "5",
            title      = "Corporate Merger Agreement",
            caseNumber = "#2024-CO-0033",
            status     = "IN_PROGRESS",
            date       = "Feb 28, 2026",
            category   = "Corporate",
            wilaya     = "Blida"
        ),
        CaseData(
            id         = "6",
            title      = "Family Custody Dispute",
            caseNumber = "#2024-FM-0055",
            status     = "OPEN",
            date       = "Mar 01, 2027",
            category   = "Family",
            wilaya     = "Tizi Ouzou"
        )
    )
)