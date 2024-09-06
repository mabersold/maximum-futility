package mabersold.dao

import mabersold.models.db.Franchise

interface FranchiseDAO {
    suspend fun allByLeagueId(leagueId: Int): List<Franchise>
    suspend fun get(id: Int): Franchise?
    suspend fun create(name: String, isDefunct: Boolean, leagueId: Int): Franchise?
    suspend fun update(id: Int, name: String?, isDefunct: Boolean?, leagueId: Int?): Franchise?
    suspend fun delete(id: Int): Boolean
}