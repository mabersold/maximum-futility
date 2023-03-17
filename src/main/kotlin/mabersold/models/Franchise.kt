package mabersold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Franchise(
    val name: String,
    @SerialName("first_season")
    val firstSeason: Int,
    @SerialName("is_defunct")
    val isDefunct: Boolean,
    val timeline: List<FranchiseTimeline>,
    val league: League? = null
) {
    val metroArea = timeline.last().metroArea
    val totalSeasons = timeline.sumOf { it.totalSeasons }
    val totalRegularSeasons = timeline.sumOf { it.totalRegularSeasons }
    val totalSeasonsWithDivisions = timeline.sumOf { it.totalSeasonsWithDivisions }
    val totalPostSeasons = timeline.sumOf { it.totalPostSeasons }
    val totalChampionships = timeline.sumOf { it.championships.size }
    val championshipAppearances = timeline.sumOf { it.championshipAppearances.size }
    val championshipAppearancesInMultiRoundPlayoffYears = timeline.sumOf { it.totalChampionshipAppearancesInMultiRoundPlayoffSeasons }
    val advancedInPlayoffs = timeline.sumOf { it.advancedInPlayoffs.size }
    val advancedInPlayoffsInMultiRoundPlayoffYears = timeline.sumOf { it.totalAdvancedInPlayoffsInMultiRoundPlayoffSeasons }
    val playoffAppearances = timeline.sumOf { it.playoffAppearances.size }
    val playoffAppearancesInMultiRoundPlayoffYears = timeline.sumOf { it.totalPlayoffAppearancesInMultiRoundPlayoffSeasons }
    val bestInDivision = timeline.sumOf { it.totalBestInDivisionInSeasonsWithDivisionalPlay }
    val bestInConference = timeline.sumOf { it.bestInConference.size }
    val bestOverall = timeline.sumOf { it.bestOverall.size }
    val worstInDivision = timeline.sumOf { it.totalWorstInDivisionInSeasonsWithDivisionalPlay }
    val worstInConference = timeline.sumOf { it.worstInConference.size }
    val worstOverall = timeline.sumOf { it.worstOverall.size }
    val championshipsPerSeason = totalChampionships.toDouble() / totalPostSeasons
    val championshipAppearancesPerSeason = championshipAppearances.toDouble() / totalPostSeasons
    val advancedInPlayoffsPerSeason = advancedInPlayoffs.toDouble() / totalPostSeasons
    val playoffAppearancesPerSeason = playoffAppearances.toDouble() / totalPostSeasons
    val bestInDivisionPerSeason = bestInDivision.toDouble() / totalSeasonsWithDivisions
    val bestInConferencePerSeason = bestInConference.toDouble() / totalRegularSeasons
    val bestOverallPerSeason = bestOverall.toDouble() / totalRegularSeasons
    val worstInDivisionPerSeason = worstInDivision.toDouble() / totalSeasonsWithDivisions
    val worstInConferencePerSeason = worstInConference.toDouble() / totalRegularSeasons
    val worstOverallPerSeason = worstOverall.toDouble() / totalRegularSeasons
    val winningPercentageInFinals = if (championshipAppearances > 0) totalChampionships.toDouble() / championshipAppearances else null
    val reachingFinalsPerPlayoffAppearance = if (playoffAppearancesInMultiRoundPlayoffYears > 0) championshipAppearancesInMultiRoundPlayoffYears.toDouble() / playoffAppearancesInMultiRoundPlayoffYears else null
    val advancingInPlayoffsPerPlayoffAppearance = if (playoffAppearancesInMultiRoundPlayoffYears > 0) advancedInPlayoffsInMultiRoundPlayoffYears.toDouble() / playoffAppearancesInMultiRoundPlayoffYears else null

    fun withLeague(league: League) =
        this.copy(
            league = league,
            timeline = timeline.within(league.firstSeason, league.mostRecentFinishedSeason)
        )

    fun isWithin(startYear: Int, endYear: Int) =
        timeline.within(startYear, endYear).isNotEmpty()

    fun within(startYear: Int, endYear: Int) =
        this.copy(
            timeline = timeline.within(startYear, endYear)
        )

    /**
     * Given a season, returns true if this franchise was active during that season
     */
    fun playedInSeason(season: Int): Boolean {
        return timeline.any { (it.startSeason..it.endSeason).contains(season) }
    }

    private fun List<FranchiseTimeline>.within(startYear: Int, endYear: Int) =
        this.filter { it.isWithin(startYear, endYear) }
            .map { it.trim(startYear, endYear) }
}
