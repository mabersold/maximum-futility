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
    val timeline: List<FranchiseTimeline>
) {
    val metroArea = timeline.last().metroArea
    val totalSeasons = timeline.sumOf { it.totalSeasons }
    val totalChampionships = timeline.sumOf { it.championships.size }
    val championshipAppearances = timeline.sumOf { it.championshipAppearances.size }
    val advancedInPlayoffs = timeline.sumOf { it.advancedInPlayoffs.size }
    val playoffAppearances = timeline.sumOf { it.playoffAppearances.size }
    val bestInDivision = timeline.sumOf { it.bestInDivision.size }
    val bestInConference = timeline.sumOf { it.bestInConference.size }
    val bestOverall = timeline.sumOf { it.bestOverall.size }
    val worstInDivision = timeline.sumOf { it.worstInDivision.size }
    val worstInConference = timeline.sumOf { it.worstInConference.size }
    val worstOverall = timeline.sumOf { it.worstOverall.size }
    val championshipsPerSeason = totalChampionships.toDouble() / totalSeasons
    val championshipAppearancesPerSeason = championshipAppearances.toDouble() / totalSeasons
    val advancedInPlayoffsPerSeason = advancedInPlayoffs.toDouble() / totalSeasons
    val playoffAppearancesPerSeason = playoffAppearances.toDouble() / totalSeasons
    val bestInDivisionPerSeason = bestInDivision.toDouble() / totalSeasons
    val bestInConferencePerSeason = bestInConference.toDouble() / totalSeasons
    val bestOverallPerSeason = bestOverall.toDouble() / totalSeasons
    val worstInDivisionPerSeason = worstInDivision.toDouble() / totalSeasons
    val worstInConferencePerSeason = worstInConference.toDouble() / totalSeasons
    val worstOverallPerSeason = worstOverall.toDouble() / totalSeasons

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
    val totalSeasons = endSeason - startSeason + 1
    val championshipsPerSeason = championships.size.toDouble() / totalSeasons
    val championshipAppearancesPerSeason = championshipAppearances.size.toDouble() / totalSeasons
    val advancedInPlayoffsPerSeason = advancedInPlayoffs.size.toDouble() / totalSeasons
    val playoffAppearancesPerSeason = playoffAppearances.size.toDouble() / totalSeasons
    val bestInDivisionPerSeason = bestInDivision.size.toDouble() / totalSeasons
    val bestInConferencePerSeason = bestInConference.size.toDouble() / totalSeasons
    val bestOverallPerSeason = bestOverall.size.toDouble() / totalSeasons
    val worstInDivisionPerSeason = worstInDivision.size.toDouble() / totalSeasons
    val worstInConferencePerSeason = worstInConference.size.toDouble() / totalSeasons
    val worstOverallPerSeason = worstOverall.size.toDouble() / totalSeasons

    fun isBefore(year: Int) = endSeason < year
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

    private fun List<Int>.getWithin(startYear: Int, endYear: Int) = this.filter { it in startYear..endYear }
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
