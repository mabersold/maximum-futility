package mabersold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mabersold.MOST_RECENT_COMPLETED_MLB_SEASON

@Serializable
data class FranchiseTimeline(
    @SerialName("start_season")
    val startSeason: Int,
    @SerialName("end_season")
    val endSeason: Int = MOST_RECENT_COMPLETED_MLB_SEASON,
    val name: String,
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
    val bestInDivision: List<Int>,
    @SerialName("best_in_conference")
    val bestInConference: List<Int>,
    @SerialName("best_overall")
    val bestOverall: List<Int>,
    @SerialName("worst_in_division")
    val worstInDivision: List<Int>,
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

    fun totalPostSeasons(league: League? = null) =
        (startSeason..endSeason).toList().filterNot { league?.excludePostseason?.contains(it) ?: false }.size

    fun totalRegularSeasons(league: League? = null) =
        totalSeasons + league.extraSeasons()

    fun totalSeasonsWithDivisions(league: League? = null) =
        (startSeason..endSeason).toList().withDivisions(league).size + league.extraSeasons()

    fun bestInDivision(league: League? = null) =
        league?.let { l -> bestInDivision.filter { divisionTitle -> divisionTitle >= l.firstSeasonWithDivisions } } ?: bestInDivision

    fun worstInDivision(league: League? = null) =
        league?.let { l -> worstInDivision.filter { divisionTitle -> divisionTitle >= l.firstSeasonWithDivisions } } ?: worstInDivision

    fun playoffAppearances(league: League? = null) =
        league?.let { l -> playoffAppearances.filter { playoffYear -> playoffYear >= l.firstSeasonWithMultiRoundPlayoffs } } ?: playoffAppearances

    fun advancedInPlayoffs(league: League? = null) =
        league?.let { l -> advancedInPlayoffs.filter { advancedYear -> advancedYear >= l.firstSeasonWithMultiRoundPlayoffs } } ?: advancedInPlayoffs

    fun championshipAppearances(league: League? = null) =
        league?.let { l -> championshipAppearances.filter { appearanceYear -> appearanceYear >= l.firstSeasonWithMultiRoundPlayoffs } } ?: championshipAppearances

    fun seasonsWithMultiRoundPlayoffs(league: League? = null) =
        (startSeason..endSeason).toList().withMultiRoundPlayoffs(league).size

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