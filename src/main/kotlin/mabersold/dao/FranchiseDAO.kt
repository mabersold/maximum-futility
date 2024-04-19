package mabersold.dao

import mabersold.models.db.Franchise

interface FranchiseDAO {
    suspend fun all(): List<Franchise>
    suspend fun allByLeagueId(leagueId: Int): List<Franchise>
    suspend fun get(id: Int): Franchise?
    suspend fun create(name: String, isDefunct: Boolean, leagueId: Int): Franchise?
}