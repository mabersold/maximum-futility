package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class Franchise(
    val id: Int,
    val name: String,
    val isDefunct: Boolean,
    val leagueId: Int,
    val league: String? = null
)

object Franchises : IntIdTable() {
    val name = varchar("name", 128)
    val isDefunct = bool("isDefunct")
    val leagueId = reference("league_id", Leagues)
}