package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class Chapter(
    val id: Int,
    val franchiseId: Int,
    val teamName: String,
    val metroId: Int,
    val startYear: Int,
    val endYear: Int?
)

object Chapters : IntIdTable() {
    val franchiseId = reference("franchise_id", Franchises)
    val metroId = reference("metro_id", Metros)
    val teamName = varchar("team_name", 128)
    val conferenceName = varchar("conference_name", 32).nullable()
    val divisionName = varchar("division_name", 32).nullable()
    val startYear = integer("start_year")
    val endYear = integer("end_year").nullable()
}