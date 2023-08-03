package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class Season(
    val id: Int,
    val name: String,
    val startYear: Int,
    val endYear: Int,
    val leagueId: Int,
    val totalMajorDivisions: Int,
    val totalMinorDivisions: Int,
    val postSeasonRounds: Int?
)

object Seasons : IntIdTable() {
    val name = varchar("name", 128)
    val startYear = integer("start_year")
    val endYear = integer("end_year")
    val leagueId = reference("league_id", Leagues)
    val totalMajorDivisions = integer("total_major_divisions")
    val totalMinorDivisions = integer("total_minor_divisions")
    val postSeasonRounds = integer("total_postseason_rounds").nullable()
}