package com.klodit.almizan.ui.search

data class FilterState(
    val selectedSectors: Set<String> = emptySet(),
    val selectedStatuses: Set<String> = emptySet(),
    val selectedWilayas: Set<String> = emptySet(),
    val procedureType: String? = null,
    val bankGuarantee: Boolean = false,
    val deadlineFrom: String = "",
    val deadlineTo: String = ""
)