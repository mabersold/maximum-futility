package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class PostSeason(val id: Int, val name: String, val year: Int, val leagueId: Int, val numberOfRounds: Int)

object PostSeasons : IntIdTable() {
    val name = varchar("name", 128)
    val year = integer("year")
    val leagueId = reference("league_id", Leagues)
    val numberOfRounds = integer("number_of_rounds")
}