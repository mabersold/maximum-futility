package mabersold.dao

import java.sql.ResultSet
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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.Sum
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager

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
        divisionPosition = row[FranchiseSeasons.divisionPosition],
        qualifiedForPostseason = row[FranchiseSeasons.qualifiedForPostseason],
        roundsWon = row[FranchiseSeasons.roundsWon],
        appearedInChampionship = row[FranchiseSeasons.appearedInChampionship],
        wonChampionship = row[FranchiseSeasons.wonChampionship]
    )

    private fun resultRowToMetroData(row: ResultRow, metricType: MetricType, totalAlias: ExpressionAlias<Int?>, opportunityAlias: ExpressionAlias<Int>) = MetroData(
        name = row[Metros.name],
        metricType = metricType,
        total = row[totalAlias] ?: 0,
        opportunities = row[opportunityAlias].toInt()
    )

    private fun resultSetToMetroData(rs: ResultSet) = MetroData(
        name = rs.getString(1),
        metricType = MetricType.TOTAL_CHAMPIONSHIPS,
        total = rs.getInt(2),
        opportunities = rs.getInt(3)
    )

    override suspend fun all(): List<FranchiseSeason> = dbQuery {
        FranchiseSeasons.selectAll().map(::resultRowToFranchiseSeason)
    }

    override suspend fun get(id: Int): FranchiseSeason? {
        return null
    }

    override suspend fun resultsByMetro(metricType: MetricType): List<MetroData> {
        return when (metricType) {
            MetricType.BEST_OVERALL -> regularSeasonResults(metricType, firstInLeagueTotal, totalSeasons)
            MetricType.WORST_OVERALL -> regularSeasonResults(metricType, lastInLeagueTotal, totalSeasons)
            MetricType.BEST_CONFERENCE -> regularSeasonResults(metricType, firstInConferenceTotal, totalSeasonsWithConferences)
            MetricType.WORST_CONFERENCE -> regularSeasonResults(metricType, lastInConferenceTotal, totalSeasonsWithConferences)
            MetricType.BEST_DIVISION -> regularSeasonResults(metricType, firstInDivisionTotal, totalSeasonsWithDivisions)
            MetricType.WORST_DIVISION -> regularSeasonResults(metricType, lastInDivisionTotal, totalSeasonsWithDivisions)
            MetricType.TOTAL_CHAMPIONSHIPS -> postSeasonResults(metricType, wonChampionshipTotal, postSeasonOpportunities)
            MetricType.CHAMPIONSHIP_APPEARANCES -> postSeasonResults(metricType, appearedInChampionshipTotal, postSeasonOpportunities)
            MetricType.QUALIFIED_FOR_PLAYOFFS -> postSeasonResults(metricType, qualifiedForPostseasonTotal, postSeasonOpportunities)
            MetricType.ADVANCED_IN_PLAYOFFS -> calculateAdvancedInPlayoffs()
        }
    }

    private suspend fun regularSeasonResults(metricType: MetricType, totalAlias: ExpressionAlias<Int?>, opportunityAlias: ExpressionAlias<Int>) = dbQuery {
        (Metros innerJoin FranchiseSeasons innerJoin Seasons)
            .slice(
                Metros.name,
                totalAlias,
                opportunityAlias
            )
            .selectAll()
            .groupBy(Metros.id)
            .map{ resultRowToMetroData(it, metricType, totalAlias, opportunityAlias) }
    }

    private suspend fun postSeasonResults(metricType: MetricType, totalAlias: ExpressionAlias<Int?>, opportunityAlias: ExpressionAlias<Int>) = dbQuery {
        (Metros innerJoin FranchiseSeasons innerJoin Seasons)
            .slice(
                Metros.name,
                totalAlias,
                opportunityAlias
            )
            .selectAll()
            .groupBy(Metros.id)
            .map{ resultRowToMetroData(it, metricType, totalAlias, opportunityAlias) }
    }

    private suspend fun calculateAdvancedInPlayoffs(): List<MetroData> {
        val metroDataList = mutableListOf<MetroData>()
        dbQuery {
            TransactionManager.current().exec(advancingInPlayoffsQuery) { rs ->
                while (rs.next()) {
                    metroDataList.add(resultSetToMetroData(rs))
                }
            }
        }

        return metroDataList
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
        private val wonChampionshipTotal = Sum(case().When(FranchiseSeasons.wonChampionship eq true, intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("won_championship")
        private val appearedInChampionshipTotal = Sum(case().When(FranchiseSeasons.appearedInChampionship eq true, intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("appeared_in_championship")
        private val qualifiedForPostseasonTotal = Sum(case().When(FranchiseSeasons.qualifiedForPostseason eq true, intLiteral(1)).Else(intLiteral(0)), IntegerColumnType()).alias("qualified_for_postseason")
        private val postSeasonOpportunities = FranchiseSeasons.qualifiedForPostseason.count().castTo<Int>(IntegerColumnType()).alias("postseason_opportunities")
        private val advancingInPlayoffsQuery = """
            SELECT m.name,
            COUNT(DISTINCT CASE WHEN f.ROUNDS_WON > 0 AND s.TOTAL_POSTSEASON_ROUNDS > 1 THEN f.ID end) AS advanced_in_playoffs,
            COUNT(DISTINCT CASE WHEN s.TOTAL_POSTSEASON_ROUNDS > 1 THEN s.ID END) AS postseason_opportunities
            FROM METROS m
            JOIN FRANCHISESEASONS f ON m.ID = f.METRO_ID
            LEFT JOIN SEASONS s ON f.SEASON_ID = s.ID
            group BY m.name
        """.trimIndent()
    }
}