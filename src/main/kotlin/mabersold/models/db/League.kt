package mabersold.models.db

import mabersold.models.api.League
import org.jetbrains.exposed.dao.id.IntIdTable

data class League(val id: Int, val name: String, val sport: String) {
    fun asApiLeague(): League {
        return League(
            id = this.id,
            name = this.name,
            sport = this.sport
        )
    }
}

object Leagues : IntIdTable() {
    val name = varchar("name", 128)
    val sport = varchar("sport", 64)
}
