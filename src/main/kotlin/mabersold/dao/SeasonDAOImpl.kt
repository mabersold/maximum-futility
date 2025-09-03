package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.Leagues
import mabersold.models.db.Season
import mabersold.models.db.Seasons
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class SeasonDAOImpl : SeasonDAO {
    private fun resultRowToSeason(row: ResultRow) = Season(
        id = row[Seasons.id].value,
        name = row[Seasons.name],
        startYear = row[Seasons.startYear],
        endYear = row[Seasons.endYear],
        leagueId = row[Seasons.leagueId].value,
        totalMajorDivisions = row[Seasons.totalMajorDivisions],
        totalMinorDivisions = row[Seasons.totalMinorDivisions],
        postSeasonRounds = row[Seasons.postSeasonRounds]
    )

    override suspend fun all(): List<Season> = dbQuery {
        Seasons.selectAll().map(::resultRowToSeason)
    }

    override suspend fun get(id: Int): Season? = dbQuery {
        (Seasons innerJoin Leagues).selectAll().where { Seasons.id eq id }
            .map(::resultRowToSeason)
            .singleOrNull()
    }

    override suspend fun allByLeagueId(leagueId: Int): List<Season> = dbQuery {
        Seasons.selectAll().where { Seasons.leagueId eq leagueId }.map(::resultRowToSeason)
    }

    override suspend fun create(
        name: String,
        startYear: Int,
        endYear: Int,
        leagueId: Int,
        totalMajorDivisions: Int,
        totalMinorDivisions: Int,
        postSeasonRounds: Int?
    ): Season? = dbQuery {
        val insertStatement = Seasons.insert {
            it[Seasons.name] = name
            it[Seasons.startYear] = startYear
            it[Seasons.endYear] = endYear
            it[Seasons.leagueId] = leagueId
            it[Seasons.totalMajorDivisions] = totalMajorDivisions
            it[Seasons.totalMinorDivisions] = totalMinorDivisions
            it[Seasons.postSeasonRounds] = postSeasonRounds
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToSeason)
    }
}