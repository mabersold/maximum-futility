package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.MetroLeagueYear
import mabersold.models.db.MetroLeagueYears
import mabersold.models.db.Metros
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import kotlin.Int

class MetroLeagueYearDAOImpl : MetroLeagueYearDAO {
    fun resultRowToMetroLeagueYear(row: ResultRow): MetroLeagueYear {
        return MetroLeagueYear(
            row[MetroLeagueYears.id].value,
            row[MetroLeagueYears.year],
            row[MetroLeagueYears.leagueId].value,
            row[MetroLeagueYears.metroId].value,
            row[Metros.name],
            row[MetroLeagueYears.championships],
            row[MetroLeagueYears.championshipOpportunities],
            row[MetroLeagueYears.championshipAppearances],
            row[MetroLeagueYears.championshipAppearanceOpportunities],
            row[MetroLeagueYears.advancedInPostseason],
            row[MetroLeagueYears.advancedInPostseasonOpportunities],
            row[MetroLeagueYears.qualifiedForPostseason],
            row[MetroLeagueYears.qualifiedForPostseasonOpportunities],
            row[MetroLeagueYears.overallOpportunities],
            row[MetroLeagueYears.totalFirstOverall],
            row[MetroLeagueYears.totalLastOverall],
            row[MetroLeagueYears.conferenceOpportunities],
            row[MetroLeagueYears.totalFirstConference],
            row[MetroLeagueYears.totalLastConference],
            row[MetroLeagueYears.divisionOpportunities],
            row[MetroLeagueYears.totalFirstDivision],
            row[MetroLeagueYears.totalLastDivision],
        )
    }

    override suspend fun all(startYear: Int, endYear: Int, leagueIds: Set<Int>): List<MetroLeagueYear> = dbQuery {
        (MetroLeagueYears innerJoin Metros)
            .selectAll()
            .where { (MetroLeagueYears.year greaterEq startYear) and (MetroLeagueYears.year lessEq endYear) and (MetroLeagueYears.leagueId inList leagueIds) }
            .map(::resultRowToMetroLeagueYear)
    }
}
