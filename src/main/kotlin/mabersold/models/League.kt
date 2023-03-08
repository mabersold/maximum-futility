package mabersold.models

enum class League(
    val firstSeason: Int,
    val excludePostseason: List<Int>,
    val mostRecentFinishedSeason: Int,
    val doubleRegularSeasons: List<Int>,
    val firstSeasonWithDivisions: Int,
    val firstSeasonWithMultiRoundPlayoffs: Int
) {
    GENERIC(1, listOf(), 2022, listOf(), 1, 1),
    MLB(1903, listOf(1904, 1994), 2022, listOf(1981), 1969, 1969),
    NFL(1933, listOf(), 2022, listOf(), 1966, 1966),
    NBA(1946, listOf(), 2021, listOf(), 1970, 1946),
    NHL(1926, listOf(), 2021, listOf(), 1926, 1926)
}