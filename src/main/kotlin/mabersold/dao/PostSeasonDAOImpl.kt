package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.Leagues
import mabersold.models.db.PostSeason
import mabersold.models.db.PostSeasons
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class PostSeasonDAOImpl : PostSeasonDAO {
    private fun resultRowToPostSeason(row: ResultRow) = PostSeason(
        id = row[PostSeasons.id].value,
        name = row[PostSeasons.name],
        year = row[PostSeasons.year],
        leagueId = row[PostSeasons.leagueId].value,
        numberOfRounds = row[PostSeasons.numberOfRounds]
    )

    override suspend fun all(): List<PostSeason> = dbQuery {
        PostSeasons.selectAll().map(::resultRowToPostSeason)
    }

    override suspend fun get(id: Int): PostSeason? = dbQuery {
        (PostSeasons innerJoin Leagues).select { PostSeasons.id eq id }
            .map(::resultRowToPostSeason)
            .singleOrNull()
    }
}