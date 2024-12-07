package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.Chapter
import mabersold.models.db.Chapters
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert

class ChapterDAOImpl : ChapterDAO {
    private fun resultRowToChapter(row: ResultRow) = Chapter(
        id = row[Chapters.id].value,
        teamName = row[Chapters.teamName],
        franchiseId = row[Chapters.franchiseId].value,
        metroId = row[Chapters.metroId].value,
        leagueId = row[Chapters.leagueId].value,
        startYear = row[Chapters.startYear],
        endYear = row[Chapters.endYear],
        conferenceName = row[Chapters.conferenceName],
        divisionName = row[Chapters.divisionName]
    )

    override suspend fun create(
        teamName: String,
        franchiseId: Int,
        metroId: Int,
        leagueId: Int,
        startYear: Int,
        endYear: Int?,
        conference: String?,
        division: String?
    ): Chapter? = dbQuery {
        val insertStatement = Chapters.insert {
            it[Chapters.teamName] = teamName
            it[Chapters.franchiseId] = franchiseId
            it[Chapters.metroId] = metroId
            it[Chapters.leagueId] = leagueId
            it[Chapters.startYear] = startYear
            it[Chapters.endYear] = endYear
            it[conferenceName] = conference
            it[divisionName] = division
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToChapter)
    }
}