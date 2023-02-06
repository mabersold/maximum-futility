package mabersold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mabersold.MOST_RECENT_COMPLETED_MLB_SEASON

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
    val totalChampionships = timeline.sumOf { it.championships.size }
    val championshipAppearances = timeline.sumOf { it.championshipAppearances.size }
    val advancedInPlayoffs = timeline.sumOf { it.advancedInPlayoffs.size }
    val playoffAppearances = timeline.sumOf { it.playoffAppearances.size }
    val bestInDivision = timeline.sumOf { it.bestInDivision(league).size }
    val bestInConference = timeline.sumOf { it.bestInConference.size }
    val bestOverall = timeline.sumOf { it.bestOverall.size }
    val worstInDivision = timeline.sumOf { it.worstInDivision(league).size }
    val worstInConference = timeline.sumOf { it.worstInConference.size }
    val worstOverall = timeline.sumOf { it.worstOverall.size }
    val championshipsPerSeason = totalChampionships.toDouble() / timeline.sumOf { it.totalPostSeasons(league) }
    val championshipAppearancesPerSeason = championshipAppearances.toDouble() / timeline.sumOf { it.totalPostSeasons(league) }
    val advancedInPlayoffsPerSeason = advancedInPlayoffs.toDouble() / timeline.sumOf { it.totalPostSeasons(league) }
    val playoffAppearancesPerSeason = playoffAppearances.toDouble() / timeline.sumOf { it.totalPostSeasons(league) }
    val bestInDivisionPerSeason = bestInDivision.toDouble() / timeline.sumOf { it.totalSeasonsWithDivisions(league) }
    val bestInConferencePerSeason = bestInConference.toDouble() / timeline.sumOf { it.totalRegularSeasons(league) }
    val bestOverallPerSeason = bestOverall.toDouble() / timeline.sumOf { it.totalRegularSeasons(league) }
    val worstInDivisionPerSeason = worstInDivision.toDouble() / timeline.sumOf { it.totalSeasonsWithDivisions(league) }
    val worstInConferencePerSeason = worstInConference.toDouble() / timeline.sumOf { it.totalRegularSeasons(league) }
    val worstOverallPerSeason = worstOverall.toDouble() / timeline.sumOf { it.totalRegularSeasons(league) }

    fun withLeague(league: League) =
        this.copy(
            league = league,
            timeline = timeline.within(league.firstSeason, league.mostRecentFinishedSeason)
        )

    fun within(startYear: Int, endYear: Int) =
        this.copy(
            timeline = timeline.within(startYear, endYear)
        )

    private fun List<FranchiseTimeline>.within(startYear: Int, endYear: Int) =
        this.filter { it.isWithin(startYear, endYear) }
            .map { it.trim(startYear, endYear) }
}

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

    private fun League?.extraSeasons() =
        this?.let {
            (startSeason..endSeason).intersect(it.doubleRegularSeasons.toSet()).size
        } ?: 0

    private fun List<Int>.getWithin(startYear: Int, endYear: Int) = this.filter { it in startYear..endYear }

    private fun List<Int>.withDivisions(league: League?) =
        this.filter { season -> league?.let { l -> season >= l.firstSeasonWithDivisions } ?: true }
}

@Serializable
enum class Metro(val displayName: String) {
    @SerialName("Atlanta")
    ATLANTA("Atlanta"),
    @SerialName("Baltimore")
    BALTIMORE("Baltimore"),
    @SerialName("Boston")
    BOSTON("Boston"),
    @SerialName("Chicago")
    CHICAGO("Chicago"),
    @SerialName("Cincinnati")
    CINCINNATI("Cincinnati"),
    @SerialName("Cleveland")
    CLEVELAND("Cleveland"),
    @SerialName("Dallas")
    DALLAS("Dallas"),
    @SerialName("Denver")
    DENVER("Denver"),
    @SerialName("Detroit")
    DETROIT("Detroit"),
    @SerialName("Grand Rapids")
    GRAND_RAPIDS("Grand Rapids"),
    @SerialName("Houston")
    HOUSTON("Houston"),
    @SerialName("Kansas City")
    KANSAS_CITY("Kansas City"),
    @SerialName("Los Angeles")
    LOS_ANGELES("Los Angeles"),
    @SerialName("Miami")
    MIAMI("Miami"),
    @SerialName("Milwaukee")
    MILWAUKEE("Milwaukee"),
    @SerialName("Minneapolis")
    MINNEAPOLIS("Minneapolis"),
    @SerialName("Montreal")
    MONTREAL("Montreal"),
    @SerialName("New York")
    NEW_YORK("New York"),
    @SerialName("Philadelphia")
    PHILADELPHIA("Philadelphia"),
    @SerialName("Phoenix")
    PHOENIX("Phoenix"),
    @SerialName("Pittsburgh")
    PITTSBURGH("Pittsburgh"),
    @SerialName("San Diego")
    SAN_DIEGO("San Diego"),
    @SerialName("San Francisco Bay")
    SAN_FRANCISCO_BAY("San Francisco Bay"),
    @SerialName("Seattle")
    SEATTLE("Seattle"),
    @SerialName("St. Louis")
    ST_LOUIS("St. Louis"),
    @SerialName("Tampa Bay")
    TAMPA_BAY("Tampa Bay"),
    @SerialName("Toronto")
    TORONTO("Toronto"),
    @SerialName("Washington")
    WASHINGTON("Washington")
}
