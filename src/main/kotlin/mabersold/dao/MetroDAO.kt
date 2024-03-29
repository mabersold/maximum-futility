package mabersold.dao

import mabersold.models.db.Metro

interface MetroDAO {
    suspend fun all(): List<Metro>
    suspend fun get(id: Int): Metro?
}