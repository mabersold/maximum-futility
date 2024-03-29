package mabersold.dao

import mabersold.models.FranchiseSeasonInfo
import mabersold.models.db.FranchiseSeason

interface FranchiseSeasonDAO {
    suspend fun all(): List<FranchiseSeasonInfo>
    suspend fun activeMetros(): List<String>
    suspend fun getBySeason(seasonId: Int): List<FranchiseSeason>
}