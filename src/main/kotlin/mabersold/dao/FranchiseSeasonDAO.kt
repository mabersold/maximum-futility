package mabersold.dao

import mabersold.models.FranchiseSeasonInfo
import mabersold.models.api.MetricType
import mabersold.models.api.MetroData
import mabersold.models.db.FranchiseSeason

interface FranchiseSeasonDAO {
    suspend fun all(): List<FranchiseSeasonInfo>
    suspend fun get(id: Int): FranchiseSeason?
    suspend fun resultsByMetro(metricType: MetricType): List<MetroData>
    suspend fun activeMetros(): List<String>
    suspend fun getBySeason(seasonId: Int): List<FranchiseSeason>
}