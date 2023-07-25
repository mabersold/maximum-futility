package mabersold.dao

import mabersold.models.db.Season

interface SeasonDAO {
    suspend fun all(): List<Season>
    suspend fun get(id: Int): Season?
}