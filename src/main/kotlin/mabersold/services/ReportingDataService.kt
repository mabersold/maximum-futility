package mabersold.services

import mabersold.dao.FranchiseSeasonDAO
import mabersold.dao.MetroLeagueYearDAO
import mabersold.models.FranchiseSeasonInfo
import mabersold.models.api.MetricType
import mabersold.models.api.MetroData
import mabersold.models.db.Standing

class ReportingDataService(private val franchiseSeasonDAO: FranchiseSeasonDAO, private val metroLeagueYearDAO: MetroLeagueYearDAO) {
    suspend fun getMetroReportByMetric(metricType: MetricType, from: Int? = null, until: Int? = null, leagueIds: Set<Int> = setOf()) =
        franchiseSeasonDAO.all()
            .fromSeason(from)
            .untilSeason(until)
            .withLeagues(leagueIds)
            .withMetricFilter(metricType)
            .groupBy { it.metro }
            .results(metricType)

    suspend fun getMetroReportFromMetroLeagueYears(metricType: MetricType, startYear: Int, endYear: Int, leagueIds: Set<Int>): List<MetroData> {
        val metroLeagueYears = metroLeagueYearDAO.all(startYear, endYear, leagueIds)

        return metroLeagueYears.groupBy { it.metroId }
            .map { (_, recordsForMetro) ->
                MetroData(
                    name = recordsForMetro.first().metroName,
                    metricType = metricType,
                    total = recordsForMetro.sumOf(metricType.total),
                    opportunities = recordsForMetro.sumOf(metricType.opportunities),
                    lastActiveYear = recordsForMetro.maxOf { it.year }
                )
            }
    }

    private fun List<FranchiseSeasonInfo>.fromSeason(from: Int?) =
        this.filter { from == null || it.startYear >= from }

    private fun List<FranchiseSeasonInfo>.untilSeason(until: Int?) =
        this.filter { until == null || it.startYear <= until }

    private fun List<FranchiseSeasonInfo>.withLeagues(leagueIds: Set<Int>) =
        this.filter { leagueIds.isEmpty() || leagueIds.contains(it.leagueId) }

    private fun List<FranchiseSeasonInfo>.withMetricFilter(metricType: MetricType) =
        when(metricType) {
            MetricType.TOTAL_CHAMPIONSHIPS,
            MetricType.CHAMPIONSHIP_APPEARANCES,
            MetricType.CHAMPIONSHIPS_WINNING_RATE,
            MetricType.CHAMPIONSHIP_APPEARANCES_PER_POSTSEASON,
            MetricType.QUALIFIED_FOR_PLAYOFFS -> this.filter { season -> season.postSeasonRounds != null }
            MetricType.ADVANCED_IN_PLAYOFFS_PER_POSTSEASON,
            MetricType.ADVANCED_IN_PLAYOFFS -> this.filter { season -> season.postSeasonRounds?.let { it > 1 } ?: false }
            MetricType.BEST_OVERALL, MetricType.WORST_OVERALL -> this
            MetricType.BEST_CONFERENCE, MetricType.WORST_CONFERENCE -> this.filter { it.totalConferences > 0 }
            MetricType.BEST_DIVISION, MetricType.WORST_DIVISION -> this.filter { it.totalDivisions > 0 }
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.results(metricType: MetricType) =
        when(metricType) {
            MetricType.TOTAL_CHAMPIONSHIPS -> this.totalChampionshipsResults()
            MetricType.CHAMPIONSHIP_APPEARANCES -> this.appearedInChampionshipResults()
            MetricType.ADVANCED_IN_PLAYOFFS -> this.advancedInPlayoffsResults()
            MetricType.QUALIFIED_FOR_PLAYOFFS -> this.qualifiedForPlayoffsResults()
            MetricType.BEST_OVERALL,
            MetricType.WORST_OVERALL,
            MetricType.BEST_CONFERENCE,
            MetricType.WORST_CONFERENCE,
            MetricType.BEST_DIVISION,
            MetricType.WORST_DIVISION -> this.standingResults(metricType)
            MetricType.CHAMPIONSHIPS_WINNING_RATE -> this.championshipWinningRateResults()
            MetricType.CHAMPIONSHIP_APPEARANCES_PER_POSTSEASON -> this.championshipAppearancesPerPostseasonResults()
            MetricType.ADVANCED_IN_PLAYOFFS_PER_POSTSEASON -> this.advancedInPlayoffsPerPostseasonResults()
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.totalChampionshipsResults() =
        this.map { (metro, seasons) ->
            val seasonIdsWithChampionship = seasons
                .filter { it.wonChampionship ?: false }
                .map { it.seasonId }
                .distinct()
                .toSet()

            val totalDiscount = seasons.filter { season ->
                seasonIdsWithChampionship.contains(season.seasonId) && season.wonChampionship != true
            }.size

            MetroData(
                metro,
                MetricType.TOTAL_CHAMPIONSHIPS,
                seasons.count { it.wonChampionship ?: false },
                seasons.count() - totalDiscount,
                seasons.maxOf { it.endYear }
            )
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.appearedInChampionshipResults() =
        this.map { (metro, seasons) ->
            MetroData(
                metro,
                MetricType.CHAMPIONSHIP_APPEARANCES,
                seasons.count { it.appearedInChampionship ?: false },
                seasons.count(),
                seasons.maxOf { it.endYear }
            )
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.advancedInPlayoffsResults() =
        this.map { (metro, seasons) ->
            MetroData(
                metro,
                MetricType.ADVANCED_IN_PLAYOFFS,
                seasons.count { (it.roundsWon ?: 0) > 0 },
                seasons.count(),
                seasons.maxOf { it.endYear }
            )
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.qualifiedForPlayoffsResults() =
        this.map { (metro, seasons) ->
            MetroData(
                metro,
                MetricType.QUALIFIED_FOR_PLAYOFFS,
                seasons.count { it.qualifiedForPostseason ?: false },
                seasons.count(),
                seasons.maxOf { it.endYear }
            )
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.standingResults(metricType: MetricType) =
        this.map { (metro, seasons) ->
            MetroData(
                metro,
                metricType,
                seasons.count { it.shouldBeCountedForMetric(metricType) },
                seasons.count(),
                seasons.maxOf { it.endYear }
            )
        }

    private fun FranchiseSeasonInfo.shouldBeCountedForMetric(metricType: MetricType) =
        when (metricType) {
            MetricType.BEST_OVERALL -> listOf(Standing.FIRST, Standing.FIRST_TIED).contains(this.leaguePosition)
            MetricType.WORST_OVERALL -> listOf(Standing.LAST, Standing.LAST_TIED).contains(this.leaguePosition)
            MetricType.BEST_CONFERENCE -> listOf(Standing.FIRST, Standing.FIRST_TIED).contains(this.conferencePosition)
            MetricType.WORST_CONFERENCE -> listOf(Standing.LAST, Standing.LAST_TIED).contains(this.conferencePosition)
            MetricType.BEST_DIVISION -> listOf(Standing.FIRST, Standing.FIRST_TIED).contains(this.divisionPosition)
            MetricType.WORST_DIVISION -> listOf(Standing.LAST, Standing.LAST_TIED).contains(this.divisionPosition)
            else -> throw IllegalArgumentException("Invalid metric type for standing: $metricType")
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.championshipWinningRateResults() =
        this.mapNotNull { (metro, seasons) ->
            if (seasons.all { it.appearedInChampionship != true }) return@mapNotNull null

            val qualifyingSeasons = seasons.filter { it.appearedInChampionship == true }

            val totalDiscount = qualifyingSeasons.groupBy( { it.seasonId }, { it.appearedInChampionship } )
                .mapValues { it.value.count { appeared -> appeared == true } }
                .filter { it.value > 1 }
                .keys
                .count()

            MetroData(
                metro,
                MetricType.CHAMPIONSHIPS_WINNING_RATE,
                qualifyingSeasons.count { it.wonChampionship == true },
                qualifyingSeasons.count() - totalDiscount,
                seasons.maxOf { it.endYear }
            )
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.championshipAppearancesPerPostseasonResults() =
        this.map { (metro, seasons) ->
            MetroData(
                metro,
                MetricType.CHAMPIONSHIP_APPEARANCES_PER_POSTSEASON,
                seasons.count { it.appearedInChampionship == true },
                seasons.count { it.postSeasonRounds != null && it.qualifiedForPostseason == true },
                seasons.maxOf { it.endYear }
            )
        }

    private fun Map<String, List<FranchiseSeasonInfo>>.advancedInPlayoffsPerPostseasonResults() =
        this.map { (metro, seasons) ->
            MetroData(
                metro,
                MetricType.ADVANCED_IN_PLAYOFFS_PER_POSTSEASON,
                seasons.count { (it.roundsWon ?: 0) > 0 },
                seasons.count { it.postSeasonRounds != null && it.qualifiedForPostseason == true },
                seasons.maxOf { it.endYear }
            )
        }
}