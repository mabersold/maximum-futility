package mabersold.services

import mabersold.dao.FranchiseSeasonDAO
import mabersold.models.MetricType
import mabersold.models.MetroData
import mabersold.models.db.Standing

class MetroDataService(private val franchiseSeasonDAO: FranchiseSeasonDAO) {
    suspend fun getMetroDataByMetric(metricType: MetricType): List<MetroData> {
        return when(metricType) {
            MetricType.ADVANCED_IN_PLAYOFFS -> getAdvancedInPostseason()
            MetricType.BEST_DIVISION -> getBestInDivision()
            else -> throw Exception("Metric type not supported")
        }
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

    private suspend fun getBestInDivision(): List<MetroData> {
        return franchiseSeasonDAO.all()
            .filter { it.totalDivisions > 0 }
            .groupBy { it.metro }
            .map { (metro, seasons) ->
                val total = seasons.count { listOf(Standing.FIRST, Standing.FIRST_TIED).contains(it.divisionPosition) }
                val opportunities = seasons.count()
                MetroData(metro.displayName, MetricType.BEST_DIVISION, total, opportunities)
            }
    }
}