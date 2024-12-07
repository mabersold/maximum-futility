package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class Chapter(
    val id: Int,
    val teamName: String,
    val franchiseId: Int,
    val metroId: Int,
    val leagueId: Int,
    val startYear: Int,
    val endYear: Int?,
    val conferenceName: String?,
    val divisionName: String?
)

object Chapters : IntIdTable() {
    val teamName = varchar("team_name", 128)
    val franchiseId = reference("franchise_id", Franchises)
    val metroId = reference("metro_id", Metros)
    val leagueId = reference("league_id", Leagues)
    val startYear = integer("start_year")
    val endYear = integer("end_year").nullable()
    val conferenceName = varchar("conference_name", 32).nullable()
    val divisionName = varchar("division_name", 32).nullable()
}