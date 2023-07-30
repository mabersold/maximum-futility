package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.MetricType
import mabersold.models.MetroData
import mabersold.models.db.FranchiseSeason
import mabersold.models.db.FranchiseSeasons
import mabersold.models.db.Metros
import mabersold.models.db.Seasons
import mabersold.models.db.Standing
import org.jetbrains.exposed.sql.ExpressionAlias
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.case
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.Sum
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.selectAll

class FranchiseSeasonDAOImpl : FranchiseSeasonDAO {
    private fun resultRowToFranchiseSeason(row: ResultRow) = FranchiseSeason(
        id = row[FranchiseSeasons.id].value,
        seasonId = row[FranchiseSeasons.seasonId].value,
        franchiseId = row[FranchiseSeasons.franchiseId].value,
        metroId = row[FranchiseSeasons.metroId].value,
        teamName = row[FranchiseSeasons.teamName],
        leagueId = row[FranchiseSeasons.leagueId].value,
        conference = row[FranchiseSeasons.conference],
        division = row[FranchiseSeasons.division],
        leaguePosition = row[FranchiseSeasons.leaguePosition],
        conferencePosition = row[FranchiseSeasons.conferencePosition],
        divisionPosition = row[FranchiseSeasons.divisionPosition]
    )

    private fun resultRowToMetroData(row: ResultRow, metricType: MetricType, totalAlias: ExpressionAlias<Int?>, opportunityAlias: ExpressionAlias<Int>) = MetroData(
        name = row[Metros.name],
        metricType = metricType,
        total = row[totalAlias] ?: 0,
        opportunities = row[opportunityAlias].toInt()
    )

    override suspend fun all(): List<FranchiseSeason> = dbQuery {
        FranchiseSeasons.selectAll().map(::resultRowToFranchiseSeason)
    }

    override suspend fun get(id: Int): FranchiseSeason? {
        return null
    }

    override suspend fun regularSeasonResultsByMetro(metricType: MetricType): List<MetroData> {
        val (total, opportunities) = when (metricType) {
            MetricType.BEST_OVERALL -> firstInLeagueTotal to totalSeasons
            MetricType.WORST_OVERALL -> lastInLeagueTotal to totalSeasons
            MetricType.BEST_CONFERENCE -> firstInConferenceTotal to totalSeasonsWithConferences
            MetricType.WORST_CONFERENCE -> lastInConferenceTotal to totalSeasonsWithConferences
            MetricType.BEST_DIVISION -> firstInDivisionTotal to totalSeasonsWithDivisions
            MetricType.WORST_DIVISION -> lastInDivisionTotal to totalSeasonsWithDivisions
            else -> throw Exception("Invalid metric type")
        }

        return dbQuery {
            (Metros innerJoin FranchiseSeasons innerJoin Seasons)
                .slice(
                    Metros.name,
                    total,
                    opportunities
                )
                .selectAll()
                .groupBy(Metros.id)
                .map{ resultRowToMetroData(it, metricType, total, opportunities) }
        }
    }

    companion object {
        private val totalSeasons = FranchiseSeasons.id.count().castTo<Int>(IntegerColumnType()).alias("seasons")
        private val totalSeasonsWithConferences = Sum(case().When(Seasons.totalMajorDivisions greaterEq 2, intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).castTo<Int>(IntegerColumnType()).alias("seasons_with_conferences")
        private val totalSeasonsWithDivisions = Sum(case().When(Seasons.totalMinorDivisions greaterEq 2, intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).castTo<Int>(IntegerColumnType()).alias("seasons_with_divisions")
        private val firstInLeagueTotal = Sum(case().When(FranchiseSeasons.leaguePosition inList listOf(Standing.FIRST, Standing.FIRST_TIED), intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("first_league_total")
        private val lastInLeagueTotal = Sum(case().When(FranchiseSeasons.leaguePosition inList listOf(Standing.LAST, Standing.LAST_TIED), intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("last_league_total")
        private val firstInConferenceTotal = Sum(case().When(FranchiseSeasons.conferencePosition inList listOf(Standing.FIRST, Standing.FIRST_TIED), intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("first_conference_total")
        private val lastInConferenceTotal = Sum(case().When(FranchiseSeasons.conferencePosition inList listOf(Standing.LAST, Standing.LAST_TIED), intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("last_conference_total")
        private val firstInDivisionTotal = Sum(case().When(FranchiseSeasons.divisionPosition inList listOf(Standing.FIRST, Standing.FIRST_TIED), intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("first_division_total")
        private val lastInDivisionTotal = Sum(case().When(FranchiseSeasons.divisionPosition inList listOf(Standing.LAST, Standing.LAST_TIED), intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("last_division_total")
    }
}