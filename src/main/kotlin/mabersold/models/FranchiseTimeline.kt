package mabersold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FranchiseTimeline(
    val name: String,
    val league: League,
    @SerialName("start_season")
    val startSeason: Int,
    @SerialName("end_season")
    val endSeason: Int = league.mostRecentFinishedSeason,
    @SerialName("metro_area")
    val metroArea: Metro,
    val championships: List<Int>,
    @SerialName("championship_appearances")
    val championshipAppearances: List<Int>,
    @SerialName("playoff_appearances")
    val playoffAppearances: List<Int>,
    @SerialName("advanced_in_playoffs")
    val advancedInPlayoffs: List<Int>,
    @SerialName("best_in_division")
    private val bestInDivision: List<Int>,
    @SerialName("best_in_conference")
    val bestInConference: List<Int>,
    @SerialName("best_overall")
    val bestOverall: List<Int>,
    @SerialName("worst_in_division")
    private val worstInDivision: List<Int>,
    @SerialName("worst_in_conference")
    val worstInConference: List<Int>,
    @SerialName("worst_overall")
    val worstOverall: List<Int>,
) {
    val totalSeasons = (startSeason..endSeason).toList().size

    fun isWithin(startYear: Int, endYear: Int) = (startYear..endYear).intersect(startSeason..endSeason).isNotEmpty()

    fun trim(startYear: Int, endYear: Int) = this.copy(
        startSeason = startSeason.coerceAtLeast(startYear),
        endSeason = endSeason.coerceAtMost(endYear),
        championships = championships.getWithin(startYear, endYear),
        championshipAppearances = championshipAppearances.getWithin(startYear, endYear),
        advancedInPlayoffs = advancedInPlayoffs.getWithin(startYear, endYear),
        playoffAppearances = playoffAppearances.getWithin(startYear, endYear),
        bestInDivision = bestInDivision.getWithin(startYear, endYear),
        bestInConference = bestInConference.getWithin(startYear, endYear),
        bestOverall = bestOverall.getWithin(startYear, endYear),
        worstInDivision = worstInDivision.getWithin(startYear, endYear),
        worstInConference = worstInConference.getWithin(startYear, endYear),
        worstOverall = worstOverall.getWithin(startYear, endYear)
    )

    val totalPostSeasons = (startSeason..endSeason).toList().filterNot { league.excludePostseason.contains(it) }.size
    val totalRegularSeasons = totalSeasons + league.extraSeasons()
    val totalSeasonsWithDivisions = (startSeason..endSeason).toList().withDivisions(league).size + league.extraSeasons()
    val bestInDivisionInSeasonsWithDivisionalPlay = bestInDivision.filter { divisionTitle -> divisionTitle >= league.firstSeasonWithDivisions }
    val totalBestInDivisionInSeasonsWithDivisionalPlay = bestInDivisionInSeasonsWithDivisionalPlay.size
    val worstInDivisionInSeasonsWithDivisionalPlay = worstInDivision.filter { divisionLoser -> divisionLoser >= league.firstSeasonWithDivisions }
    val totalWorstInDivisionInSeasonsWithDivisionalPlay = worstInDivisionInSeasonsWithDivisionalPlay.size
    val playoffAppearancesInMultiRoundPlayoffSeasons = playoffAppearances.filter { playoffYear -> playoffYear >= league.firstSeasonWithMultiRoundPlayoffs }
    val totalPlayoffAppearancesInMultiRoundPlayoffSeasons = playoffAppearancesInMultiRoundPlayoffSeasons.size
    val advancedInPlayoffsInMultiRoundPlayoffSeasons = advancedInPlayoffs.filter { advancedYear -> advancedYear >= league.firstSeasonWithMultiRoundPlayoffs }
    val totalAdvancedInPlayoffsInMultiRoundPlayoffSeasons = advancedInPlayoffsInMultiRoundPlayoffSeasons.size
    val championshipAppearancesInMultiRoundPlayoffSeasons = championshipAppearances.filter { appearanceYear -> appearanceYear >= league.firstSeasonWithMultiRoundPlayoffs }
    val totalChampionshipAppearancesInMultiRoundPlayoffSeasons = championshipAppearancesInMultiRoundPlayoffSeasons.size
    val totalSeasonsWithMultiRoundPlayoffs = (startSeason..endSeason).toList().withMultiRoundPlayoffs(league).size

    private fun League?.extraSeasons() =
        this?.let {
            (startSeason..endSeason).intersect(it.doubleRegularSeasons.toSet()).size
        } ?: 0

    private fun List<Int>.getWithin(startYear: Int, endYear: Int) = this.filter { it in startYear..endYear }

    private fun List<Int>.withDivisions(league: League?) =
        this.filter { season -> league?.let { l -> season >= l.firstSeasonWithDivisions } ?: true }

    private fun List<Int>.withMultiRoundPlayoffs(league: League?) =
        this.filter { season -> league?.let { l -> season >= l.firstSeasonWithMultiRoundPlayoffs } ?: true }
}