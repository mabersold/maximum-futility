package mabersold.models

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
)

data class RegularSeasonResult(
    val title: String,
    val teams: List<String>,
    val worstTeams: List<String>
)

data class Warning(
    val message: String
)