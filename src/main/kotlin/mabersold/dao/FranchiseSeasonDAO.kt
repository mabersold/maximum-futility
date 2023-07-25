package mabersold.dao

import mabersold.models.db.FranchiseSeason

interface FranchiseSeasonDAO {
    suspend fun all(): List<FranchiseSeason>
    suspend fun get(id: Int): FranchiseSeason?
}