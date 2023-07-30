package mabersold.services

import mabersold.dao.FranchisePostSeasonDAOImpl
import mabersold.dao.FranchiseSeasonDAOImpl
import mabersold.models.MetricType
import mabersold.models.MetricType.ADVANCED_IN_PLAYOFFS
import mabersold.models.MetricType.BEST_CONFERENCE
import mabersold.models.MetricType.BEST_DIVISION
import mabersold.models.MetricType.BEST_OVERALL
import mabersold.models.MetricType.CHAMPIONSHIP_APPEARANCES
import mabersold.models.MetricType.QUALIFIED_FOR_PLAYOFFS
import mabersold.models.MetricType.TOTAL_CHAMPIONSHIPS
import mabersold.models.MetricType.WORST_CONFERENCE
import mabersold.models.MetricType.WORST_DIVISION
import mabersold.models.MetricType.WORST_OVERALL
import mabersold.models.MetroData

class MetroDataService {
    private val franchiseSeasonDAO = FranchiseSeasonDAOImpl()
    private val franchisePostSeasonDAO = FranchisePostSeasonDAOImpl()

    suspend fun getMetroData(metricType: MetricType): List<MetroData> {
        return when(metricType) {
            TOTAL_CHAMPIONSHIPS, CHAMPIONSHIP_APPEARANCES, ADVANCED_IN_PLAYOFFS, QUALIFIED_FOR_PLAYOFFS ->
                franchisePostSeasonDAO.postSeasonResultsByMetro(metricType)
            BEST_OVERALL, BEST_CONFERENCE, BEST_DIVISION, WORST_OVERALL, WORST_CONFERENCE, WORST_DIVISION ->
                franchiseSeasonDAO.regularSeasonResultsByMetro(metricType)
            else -> throw Exception("Invalid metric type")
        }
    }
}