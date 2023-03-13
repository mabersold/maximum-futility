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
    val championshipsPerSeason = totalChampionships.toDouble() / (totalPostSeasons - totalDiscount())
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

    /**
     * Returns the total championship discount for a league.
     *
     * Why calculate this? Only one team can win a championship per season. For a city with multiple franchises in the same
     * league, if they win a championship, the fact that the other franchises did not will count against the city. To
     * account for this, we discount the total championship opportunities for a city by one for every other franchise in
     * that city in that league for each season with a championship. So, for Example, if the Yankees and Mets play in
     * New York for twenty seasons each, and win five titles between them, it would be calculated at 5/35 rather than 5/40.
     */
    private fun totalDiscount() = allLeagues().sumOf { league -> multipleFranchiseDiscount(league) }

    /**
     * Given a season and a league, returns the total number of franchises that this city has in that league for that
     * particular season
     */
    private fun totalFranchisesInCityDuringSeason(season: Int, league: League) =
        franchises.filter { it.league == league }.filter { it.playedInSeason(season) }.size


    /**
     * Given a league, returns a list of all championships that this city has for that league
     */
    private fun allChampionshipsByLeague(league: League) =
        franchises.filter { it.league == league }.flatMap { it.timeline.flatMap { tl -> tl.championships } }

    /**
     * Given a league, returns the total discount for that league to be used when calculating championship data
     */
    private fun multipleFranchiseDiscount(league: League): Int {
        return allChampionshipsByLeague(league).sumOf { titleSeason ->
            totalFranchisesInCityDuringSeason(
                titleSeason,
                league
            ) - 1
        }
    }

    /**
     * A set of all leagues that this city has had or currently has franchises in
     */
    private fun allLeagues() = franchises.flatMap { it.timeline.map { tl -> tl.league } }.toSet()
}
