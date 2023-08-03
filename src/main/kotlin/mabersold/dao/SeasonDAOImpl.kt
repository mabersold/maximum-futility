package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.Leagues
import mabersold.models.db.Season
import mabersold.models.db.Seasons
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
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
        (Seasons innerJoin Leagues).select { Seasons.id eq id }
            .map(::resultRowToSeason)
            .singleOrNull()
    }
}