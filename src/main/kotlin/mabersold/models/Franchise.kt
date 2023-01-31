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
    val timeline: List<FranchiseTimeline>
) {
    val metroArea = timeline.last().metroArea
    val totalChampionships = timeline.sumOf { it.championships.size }
    val championshipAppearances = timeline.sumOf { it.championshipAppearances.size }
    val bestInDivision = timeline.sumOf { it.bestInDivision.size }
    val bestInConference = timeline.sumOf { it.bestInConference.size }
    val bestOverall = timeline.sumOf { it.bestOverall.size }
    val worstInDivision = timeline.sumOf { it.worstInDivision.size }
    val worstInConference = timeline.sumOf { it.worstInConference.size }
    val worstOverall = timeline.sumOf { it.worstOverall.size }
}

@Serializable
data class FranchiseTimeline(
    @SerialName("start_season")
    val startSeason: Int,
    @SerialName("end_season")
    val endSeason: Int? = null,
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
)

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
