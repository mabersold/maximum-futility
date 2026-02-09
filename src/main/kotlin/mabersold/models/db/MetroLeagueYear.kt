package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class MetroLeagueYear(
    val id: Int,
    val year: Int,
    val leagueId: Int,
    val metroId: Int,
    val metroName: String,
    val championships: Int,
    val championshipOpportunities: Int,
    val championshipAppearances: Int,
    val championshipAppearanceOpportunities: Int,
    val advancedInPostseason: Int,
    val advancedInPostseasonOpportunities: Int,
    val qualifiedForPostseason: Int,
    val qualifiedForPostseasonOpportunities: Int,
    val overallOpportunities: Int,
    val totalFirstOverall: Int,
    val totalLastOverall: Int,
    val conferenceOpportunities: Int,
    val totalFirstConference: Int,
    val totalLastConference: Int,
    val divisionOpportunities: Int,
    val totalFirstDivision: Int,
    val totalLastDivision: Int,
)

object MetroLeagueYears : IntIdTable() {
    val year = integer("year")
    val leagueId = reference("league_id", Leagues)
    val metroId = reference("metro_id", Metros)
    val championships = integer("championships")
    val championshipOpportunities = integer("championship_opportunities")
    val championshipAppearances = integer("championship_appearances")
    val championshipAppearanceOpportunities = integer("championship_appearance_opportunities")
    val advancedInPostseason = integer("advanced_postseason")
    val advancedInPostseasonOpportunities = integer("advanced_postseason_opportunities")
    val qualifiedForPostseason = integer("qualified_for_postseason")
    val qualifiedForPostseasonOpportunities = integer("qualified_for_postseason_opportunities")
    val overallOpportunities = integer("overall_opportunities")
    val totalFirstOverall = integer("total_first_overall")
    val totalLastOverall = integer("total_last_overall")
    val conferenceOpportunities = integer("conference_opportunities")
    val totalFirstConference = integer("total_first_conference")
    val totalLastConference = integer("total_last_conference")
    val divisionOpportunities = integer("division_opportunities")
    val totalFirstDivision = integer("total_first_division")
    val totalLastDivision = integer("total_last_division")
}