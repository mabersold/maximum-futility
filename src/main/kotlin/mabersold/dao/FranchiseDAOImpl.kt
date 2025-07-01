package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.Leagues
import mabersold.models.db.Franchise
import mabersold.models.db.Franchises
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class FranchiseDAOImpl : FranchiseDAO {
    private fun resultRowToFranchise(row: ResultRow) = Franchise(
        id = row[Franchises.id].value,
        name = row[Franchises.name],
        label = row[Franchises.label],
        isDefunct = row[Franchises.isDefunct],
        leagueId = row[Franchises.leagueId].value,
        league = row.getOrNull(Leagues.name) ?: ""
    )

    override suspend fun allByLeagueId(leagueId: Int): List<Franchise> = dbQuery {
        (Franchises innerJoin Leagues).select(
            Franchises.id,
            Franchises.name,
            Franchises.label,
            Franchises.isDefunct,
            Franchises.leagueId,
            Leagues.name
        )
            .where { Franchises.leagueId eq leagueId }
            .map(::resultRowToFranchise)
    }

    override suspend fun get(id: Int): Franchise? = dbQuery {
        (Franchises innerJoin Leagues).selectAll().where { Franchises.id eq id }
            .map(::resultRowToFranchise)
            .singleOrNull()
    }

    override suspend fun create(name: String, label: String, isDefunct: Boolean, leagueId: Int): Franchise? = dbQuery {
        val insertStatement = Franchises.insert {
            it[Franchises.name] = name
            it[Franchises.label] = label
            it[Franchises.isDefunct] = isDefunct
            it[Franchises.leagueId] = leagueId
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToFranchise)
    }

    override suspend fun update(id: Int, name: String?, isDefunct: Boolean?, leagueId: Int?): Franchise? = dbQuery {
        val updated = Franchises.update({ Franchises.id eq id }) { update ->
            name?.let { update[Franchises.name] = it }
            isDefunct?.let { update[Franchises.isDefunct] = it }
            leagueId?.let { update[Franchises.leagueId] = it }
        }

        if (updated > 0) {
            Franchises.selectAll().where { Franchises.id eq id }
                .map(::resultRowToFranchise)
                .singleOrNull()
        } else {
            null
        }
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        val rowsDeleted = Franchises.deleteWhere {
            Franchises.id eq id
        }

        rowsDeleted == 1
    }
}