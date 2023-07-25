package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.Leagues
import mabersold.models.db.Franchise
import mabersold.models.db.Franchises
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class FranchiseDAOImpl : FranchiseDAO {
    private fun resultRowToFranchise(row: ResultRow) = Franchise(
        id = row[Franchises.id].value,
        name = row[Franchises.name],
        isDefunct = row[Franchises.isDefunct],
        leagueId = row[Franchises.leagueId].value
    )

    override suspend fun all(): List<Franchise> = dbQuery {
        Franchises.selectAll().map(::resultRowToFranchise)
    }

    override suspend fun get(id: Int): Franchise? = dbQuery {
        (Franchises innerJoin Leagues).select { Franchises.id eq id }
            .map(::resultRowToFranchise)
            .singleOrNull()
    }

    override suspend fun create(name: String, isDefunct: Boolean, leagueId: Int): Franchise? = dbQuery {
        val insertStatement = Franchises.insert {
            it[Franchises.name] = name
            it[Franchises.isDefunct] = isDefunct
            it[Franchises.leagueId] = leagueId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToFranchise)
    }
}