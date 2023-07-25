package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class League(val id: Int, val name: String, val sport: String)

object Leagues : IntIdTable() {
    val name = varchar("name", 128)
    val sport = varchar("sport", 64)
}
