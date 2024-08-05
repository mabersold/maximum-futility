package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.League
import mabersold.models.db.Leagues
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class LeagueDAOImpl : LeagueDAO {
    private fun resultRowToLeague(row: ResultRow) = League(
        id = row[Leagues.id].value,
        name = row[Leagues.name],
        sport = row[Leagues.sport]
    )

    override suspend fun all(): List<League> = dbQuery {
        Leagues.selectAll().map(::resultRowToLeague)
    }

    override suspend fun get(id: Int): League? = dbQuery {
        Leagues.selectAll().where { Leagues.id eq id }
            .map(::resultRowToLeague)
            .singleOrNull()
    }

    override suspend fun create(name: String, sport: String): League? = dbQuery {
        val insertStatement = Leagues.insert {
            it[Leagues.name] = name
            it[Leagues.sport] = sport
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToLeague)
    }

    override suspend fun update(id: Int, name: String?, sport: String?): Int = dbQuery {
        Leagues.update({ Leagues.id eq id}) { update ->
            name?.let { update[Leagues.name] = name }
            sport?.let { update[Leagues.sport] = sport }
        }
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        val rowsDeleted = Leagues.deleteWhere {
            Leagues.id eq id
        }
        rowsDeleted == 1
    }
}