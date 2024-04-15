package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class SeasonReport(
    val id: Int,
    val name: String,
    val startYear: Int,
    val endYear: Int,
    val totalMajorDivisions: Int,
    val totalMinorDivisions: Int,
    val postSeasonRounds: Int?,
    val structure: Group,
    val teamsInPostseason: List<String>,
    val teamsAdvancedInPostseason: List<String>,
    val teamsInChampionship: List<String>,
    val champion: String?,
    val league: League,
    val warnings: List<Warning>
)
