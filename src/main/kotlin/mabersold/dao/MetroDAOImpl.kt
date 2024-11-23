package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.Metro
import mabersold.models.db.Metros
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

class MetroDAOImpl : MetroDAO {
    private fun resultRowToMetro(row: ResultRow): Metro {
        return Metro(
            id = row[Metros.id].value,
            name = row[Metros.name],
            label = row[Metros.label]
        )
    }

    override suspend fun all(): List<Metro> = dbQuery {
        Metros.selectAll().map(::resultRowToMetro)
    }

    override suspend fun get(id: Int): Metro? = dbQuery {
        Metros.selectAll().where { Metros.id eq id }
            .map(::resultRowToMetro)
            .singleOrNull()
    }
}