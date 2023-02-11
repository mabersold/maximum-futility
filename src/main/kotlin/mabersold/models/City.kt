package mabersold.models

data class City(
    val metroArea: Metro,
    val franchises: List<Franchise>
) {
    val totalSeasons = franchises.sumOf { it.totalSeasons }
    val totalRegularSeasons = franchises.sumOf { it.totalRegularSeasons }
    val totalSeasonsWithDivisions = franchises.sumOf { it.totalSeasonsWithDivisions }
    val totalPostSeasons = franchises.sumOf { it.totalPostSeasons }
    val totalChampionships = franchises.sumOf { it.totalChampionships }
    val championshipAppearances = franchises.sumOf { it.championshipAppearances }
    private val championshipAppearancesInMultiRoundPlayoffYears = franchises.sumOf { it.championshipAppearancesInMultiRoundPlayoffYears }
    val advancedInPlayoffs = franchises.sumOf { it.advancedInPlayoffs }
    private val advancedInPlayoffsInMultiRoundPlayoffYears = franchises.sumOf { it.advancedInPlayoffsInMultiRoundPlayoffYears }
    val playoffAppearances = franchises.sumOf { it.playoffAppearances }
    private val playoffAppearancesInMultiRoundPlayoffYears = franchises.sumOf { it.playoffAppearancesInMultiRoundPlayoffYears }
    val bestInDivision = franchises.sumOf { it.bestInDivision }
    val bestInConference = franchises.sumOf { it.bestInConference }
    val bestOverall = franchises.sumOf { it.bestOverall }
    val worstInDivision = franchises.sumOf { it.worstInDivision }
    val worstInConference = franchises.sumOf { it.worstInConference }
    val worstOverall = franchises.sumOf { it.worstOverall }
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
}
