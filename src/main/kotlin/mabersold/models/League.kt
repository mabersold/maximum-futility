package mabersold.models

enum class League(
    val firstSeason: Int,
    val excludePostseason: List<Int>,
    val mostRecentFinishedSeason: Int,
    val doubleRegularSeasons: List<Int>,
    val firstSeasonWithDivisions: Int,
    val firstSeasonWithMultiRoundPlayoffs: Int,
    val excludeRegularSeason: List<Int>
) {
    GENERIC(1, listOf(), 2022, listOf(), 1, 1, listOf()),
    MLB(1903, listOf(1904, 1994), 2023, listOf(1981), 1969, 1969, listOf()),
    NFL(1933, listOf(), 2022, listOf(), 1966, 1966, listOf()),
    NBA(1946, listOf(), 2022, listOf(), 1970, 1946, listOf()),
    NHL(1926, listOf(2004), 2022, listOf(), 1974, 1926, listOf(2004))
}