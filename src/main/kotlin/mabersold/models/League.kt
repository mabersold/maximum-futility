package mabersold.models

enum class League(
    val firstSeason: Int,
    val excludePostseason: List<Int>,
    val mostRecentFinishedSeason: Int,
    val doubleRegularSeasons: List<Int>,
    val firstSeasonWithDivisions: Int,
    val firstSeasonWithMultiRoundPlayoffs: Int
) {
    MLB(1903, listOf(1904, 1994), 2022, listOf(1981), 1969, 1969)
}