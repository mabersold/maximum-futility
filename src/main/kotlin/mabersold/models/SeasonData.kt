package mabersold.models

import mabersold.models.api.Warning

data class SeasonSummary(
    val name: String,
    val leagueName: String,
    val totalConferences: Int,
    val totalDivisions: Int,
    val conferences: List<String>,
    val divisions: List<String>,
    val appearedInPostseason: List<String>,
    val advancedInPostseason: List<String>,
    val appearedInChampionship: List<String>,
    val champions: String,
    val results: List<RegularSeasonResult>,
    val warnings: List<Warning> = emptyList()
) {
    val conferenceNames = conferences.joinToString()
    val divisionNames = divisions.joinToString()
}

data class RegularSeasonResult(
    val title: String,
    val teams: List<String>,
    val worstTeams: List<String>
)