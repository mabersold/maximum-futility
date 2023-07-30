package mabersold.dao

import mabersold.models.MetricType
import mabersold.models.MetroData
import mabersold.models.db.FranchisePostseason

interface FranchisePostSeasonDAO {
    suspend fun all(): List<FranchisePostseason>
    suspend fun postSeasonResultsByMetro(metricType: MetricType): List<MetroData>
}