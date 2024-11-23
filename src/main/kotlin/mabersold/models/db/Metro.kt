package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class Metro(val id: Int, val name: String, val label: String)

object Metros : IntIdTable() {
    val name = varchar("name", 128)
    val label = varchar("label", 32).uniqueIndex()
}
