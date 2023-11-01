package mabersold.services

import mabersold.dao.FranchiseSeasonDAO
import mabersold.models.MetricType
import mabersold.models.MetroData
import mabersold.models.db.Standing

class MetroDataService(private val franchiseSeasonDAO: FranchiseSeasonDAO) {
    suspend fun getMetroDataByMetric(metricType: MetricType): List<MetroData> {
        return when(metricType) {
            MetricType.CHAMPIONSHIP_APPEARANCES -> getAppearedInChampionship()
            MetricType.ADVANCED_IN_PLAYOFFS -> getAdvancedInPostseason()
            MetricType.QUALIFIED_FOR_PLAYOFFS -> getQualifiedForPostseason()
            MetricType.BEST_OVERALL -> getFirstOverall()
            MetricType.WORST_OVERALL -> getLastOverall()
            MetricType.BEST_CONFERENCE -> getFirstInConference()
            MetricType.WORST_CONFERENCE -> getLastInConference()
            MetricType.BEST_DIVISION -> getFirstInDivision()
            MetricType.WORST_DIVISION -> getLastInDivision()
            else -> throw Exception("Metric type not supported")
        }
    }

    private suspend fun getAppearedInChampionship() =
        franchiseSeasonDAO.all()
            .filter { season -> season.postSeasonRounds != null }
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                MetroData(
                    metro.displayName,
                    MetricType.CHAMPIONSHIP_APPEARANCES,
                    seasons.count { it.appearedInChampionship ?: false },
                    seasons.count()
                )
            }

    private suspend fun getAdvancedInPostseason(): List<MetroData> {
        return franchiseSeasonDAO.all()
            .filter { season -> season.postSeasonRounds?.let { it > 1 } ?: false }
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                MetroData(
                    metro.displayName,
                    MetricType.ADVANCED_IN_PLAYOFFS,
                    seasons.count { (it.roundsWon ?: 0) > 0 },
                    seasons.count()
                )
            }
    }

    private suspend fun getQualifiedForPostseason() =
        franchiseSeasonDAO.all()
            .filter { season -> season.postSeasonRounds != null }
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                MetroData(
                    metro.displayName,
                    MetricType.QUALIFIED_FOR_PLAYOFFS,
                    seasons.count { it.qualifiedForPostseason ?: false },
                    seasons.count()
                )
            }

    private suspend fun getFirstOverall() =
        franchiseSeasonDAO.all()
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                val total = seasons.count { listOf(Standing.FIRST, Standing.FIRST_TIED).contains(it.leaguePosition) }
                val opportunities = seasons.count()
                MetroData(metro.displayName, MetricType.BEST_OVERALL, total, opportunities)
            }

    private suspend fun getLastOverall() =
        franchiseSeasonDAO.all()
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                val total = seasons.count { listOf(Standing.LAST, Standing.LAST_TIED).contains(it.leaguePosition) }
                val opportunities = seasons.count()
                MetroData(metro.displayName, MetricType.WORST_OVERALL, total, opportunities)
            }

    private suspend fun getFirstInConference() =
        franchiseSeasonDAO.all()
            .filter { it.totalConferences > 0 }
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                val total = seasons.count { listOf(Standing.FIRST, Standing.FIRST_TIED).contains(it.conferencePosition) }
                val opportunities = seasons.count()
                MetroData(metro.displayName, MetricType.BEST_CONFERENCE, total, opportunities)
            }

    private suspend fun getLastInConference() =
        franchiseSeasonDAO.all()
            .filter { it.totalConferences > 0 }
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                val total = seasons.count { listOf(Standing.LAST, Standing.LAST_TIED).contains(it.conferencePosition) }
                val opportunities = seasons.count()
                MetroData(metro.displayName, MetricType.WORST_CONFERENCE, total, opportunities)
            }

    private suspend fun getFirstInDivision(): List<MetroData> {
        return franchiseSeasonDAO.all()
            .filter { it.totalDivisions > 0 }
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                val total = seasons.count { listOf(Standing.FIRST, Standing.FIRST_TIED).contains(it.divisionPosition) }
                val opportunities = seasons.count()
                MetroData(metro.displayName, MetricType.BEST_DIVISION, total, opportunities)
            }
    }

    private suspend fun getLastInDivision() =
        franchiseSeasonDAO.all()
            .filter { it.totalDivisions > 0 }
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                val total = seasons.count { listOf(Standing.LAST, Standing.LAST_TIED).contains(it.divisionPosition) }
                val opportunities = seasons.count()
                MetroData(metro.displayName, MetricType.WORST_DIVISION, total, opportunities)
            }
}