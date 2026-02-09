package mabersold.models.api

import kotlinx.serialization.Serializable
import mabersold.models.db.MetroLeagueYear

@Serializable
data class MetroData(
    val name: String,
    val metricType: MetricType,
    val total: Int,
    val opportunities: Int,
    val lastActiveYear: Int
) {
    val rate = if (opportunities > 0) total.toDouble() / opportunities else null
}

enum class MetricType(
    val displayName: String,
    val total: (MetroLeagueYear) -> Int,
    val opportunities: (MetroLeagueYear) -> Int)
{
    TOTAL_CHAMPIONSHIPS("Total Championships", { it.championships }, { it.championshipOpportunities }),
    CHAMPIONSHIP_APPEARANCES("Championship Appearances", { it.championshipAppearances }, { it.championshipAppearanceOpportunities }),
    ADVANCED_IN_PLAYOFFS("Advanced in Playoffs", { it.advancedInPostseason }, { it.advancedInPostseasonOpportunities }),
    QUALIFIED_FOR_PLAYOFFS("Qualified for Playoffs", { it.qualifiedForPostseason }, { it.qualifiedForPostseasonOpportunities }),
    BEST_OVERALL("Best Overall", { it.totalFirstOverall }, { it.overallOpportunities }),
    WORST_OVERALL("Worst Overall", { it.totalLastOverall }, { it.overallOpportunities }),
    BEST_CONFERENCE("Best in Conference", { it.totalFirstConference }, { it.conferenceOpportunities }),
    WORST_CONFERENCE("Worst in Conference", { it.totalLastConference }, { it.conferenceOpportunities }),
    BEST_DIVISION("Best in Division", { it.totalFirstDivision }, { it.divisionOpportunities }),
    WORST_DIVISION("Worst in Division", { it.totalLastDivision }, { it.divisionOpportunities }),
    CHAMPIONSHIPS_WINNING_RATE("Winning Rate in Championships", { it.championships }, { it.championshipAppearances }),
    CHAMPIONSHIP_APPEARANCES_PER_POSTSEASON("Championship Appearances per Postseason", { it.championshipAppearances }, { it.qualifiedForPostseason }),
    ADVANCED_IN_PLAYOFFS_PER_POSTSEASON("Advanced in Playoffs per Postseason", { it.advancedInPostseason }, { it.qualifiedForPostseason }),
}