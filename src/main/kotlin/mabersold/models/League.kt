package mabersold.models

enum class League(val firstSeason: Int, val excludePostseason: List<Int>, val mostRecentFinishedSeason: Int) {
    MLB(1903, listOf(1904, 1994), 2022)
}