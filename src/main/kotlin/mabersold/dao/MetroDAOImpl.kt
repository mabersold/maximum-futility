package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.Leagues
import mabersold.models.db.Metro
import mabersold.models.db.Metros
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
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

    override suspend fun create(name: String, label: String): Metro? = dbQuery {
        val insertStatement = Metros.insert {
            it[Metros.name] = name
            it[Metros.label] = label
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToMetro)
    }
}