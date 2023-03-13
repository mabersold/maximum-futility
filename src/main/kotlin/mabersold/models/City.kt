package mabersold.models

data class City(
    val metroArea: Metro,
    val franchises: List<Franchise>
) {
    val totalSeasons = franchises.sumOf { it.totalSeasons }
    val totalRegularSeasons = franchises.sumOf { it.totalRegularSeasons }
    val totalSeasonsWithDivisions = franchises.sumOf { it.totalSeasonsWithDivisions }
    val totalPostSeasons = franchises.sumOf { it.totalPostSeasons }
    private val totalChampionships = franchises.sumOf { it.totalChampionships }
    private val totalChampionshipAppearances = franchises.sumOf { it.championshipAppearances }
    private val championshipAppearancesInMultiRoundPlayoffYears = franchises.sumOf { it.championshipAppearancesInMultiRoundPlayoffYears }
    private val totalAdvancedInPlayoffs = franchises.sumOf { it.advancedInPlayoffs }
    private val advancedInPlayoffsInMultiRoundPlayoffYears = franchises.sumOf { it.advancedInPlayoffsInMultiRoundPlayoffYears }
    private val totalPlayoffAppearances = franchises.sumOf { it.playoffAppearances }
    private val playoffAppearancesInMultiRoundPlayoffYears = franchises.sumOf { it.playoffAppearancesInMultiRoundPlayoffYears }
    private val totalBestInDivision = franchises.sumOf { it.bestInDivision }
    private val totalBestInConference = franchises.sumOf { it.bestInConference }
    private val totalBestOverall = franchises.sumOf { it.bestOverall }
    private val totalWorstInDivision = franchises.sumOf { it.worstInDivision }
    private val totalWorstInConference = franchises.sumOf { it.worstInConference }
    private val totalWorstOverall = franchises.sumOf { it.worstOverall }
    val championships = Metric(totalChampionships, totalPostSeasons - totalDiscount())
    val championshipAppearances = Metric(totalChampionshipAppearances, totalPostSeasons)
    val advancedInPlayoffs = Metric(totalAdvancedInPlayoffs, totalPostSeasons)
    val playoffAppearances = Metric(totalPlayoffAppearances, totalPostSeasons)
    val bestInDivision = Metric(totalBestInDivision, totalSeasonsWithDivisions)
    val bestInConference = Metric(totalBestInConference, totalRegularSeasons)
    val bestOverall = Metric(totalBestOverall, totalRegularSeasons)
    val worstInDivision = Metric(totalWorstInDivision, totalSeasonsWithDivisions)
    val worstInConference = Metric(totalWorstInConference, totalRegularSeasons)
    val worstOverall = Metric(totalWorstOverall, totalRegularSeasons)
    val successInFinals = Metric(totalChampionships, totalChampionshipAppearances)
    val reachingFinalsPerPlayoffs = Metric(championshipAppearancesInMultiRoundPlayoffYears, playoffAppearancesInMultiRoundPlayoffYears)
    val advancingInPlayoffsPerPlayoffs = Metric(advancedInPlayoffsInMultiRoundPlayoffYears, playoffAppearancesInMultiRoundPlayoffYears)

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
