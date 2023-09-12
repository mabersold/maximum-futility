package mabersold.dao

import mabersold.models.MetricType
import mabersold.models.MetroData
import mabersold.models.db.FranchiseSeason

interface FranchiseSeasonDAO {
    suspend fun all(): List<FranchiseSeason>
    suspend fun get(id: Int): FranchiseSeason?
    suspend fun resultsByMetro(metricType: MetricType): List<MetroData>
    suspend fun activeMetros(): List<String>
    suspend fun getBySeason(seasonId: Int): List<FranchiseSeason>
}