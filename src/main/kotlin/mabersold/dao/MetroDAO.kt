package mabersold.dao

import mabersold.models.db.Metro

interface MetroDAO {
    suspend fun all(): List<Metro>
    suspend fun allByLabel(labels: List<String>): List<Metro>
    suspend fun get(id: Int): Metro?
    suspend fun create(name: String, label: String): Metro?
}