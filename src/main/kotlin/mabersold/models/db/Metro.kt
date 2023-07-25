package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class Metro(val id: Int, val name: String)

object Metros : IntIdTable() {
    val name = varchar("name", 128)
}
