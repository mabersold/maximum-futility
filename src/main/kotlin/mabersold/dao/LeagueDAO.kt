package mabersold.dao

import mabersold.models.db.League

interface LeagueDAO {
    suspend fun all(): List<League>
    suspend fun get(id: Int): League?
    suspend fun create(name: String, sport: String): League?
    suspend fun update(id: Int, name: String?, sport: String?): Int
}