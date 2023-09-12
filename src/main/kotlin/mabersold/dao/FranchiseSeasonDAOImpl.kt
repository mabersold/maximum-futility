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
import org.jetbrains.exposed.sql.select
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
            MetricType.TOTAL_CHAMPIONSHIPS -> championshipResults()
            MetricType.CHAMPIONSHIP_APPEARANCES -> postSeasonResults(metricType, appearedInChampionshipTotal, postSeasonOpportunities)
            MetricType.QUALIFIED_FOR_PLAYOFFS -> postSeasonResults(metricType, qualifiedForPostseasonTotal, postSeasonOpportunities)
            MetricType.ADVANCED_IN_PLAYOFFS -> calculateAdvancedInPlayoffs()
        }
    }

    override suspend fun activeMetros(): List<String> {
        val activeMetros = mutableListOf<String>()
        dbQuery {
            TransactionManager.current().exec(activeMetrosQuery) { rs ->

                while (rs.next()) {
                    activeMetros.add(rs.getString(1))
                }
                activeMetros
            }
        }
        return activeMetros
    }

    override suspend fun getBySeason(seasonId: Int) = dbQuery {
        FranchiseSeasons.select { FranchiseSeasons.seasonId eq seasonId }
            .map(::resultRowToFranchiseSeason)
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

    private suspend fun championshipResults(): List<MetroData> {
        val metroDataList = mutableListOf<MetroData>()
        dbQuery {
            TransactionManager.current().exec(adjustedChampionshipQuery) { rs ->
                while (rs.next()) {
                    metroDataList.add(MetroData(
                        name = rs.getString(1),
                        metricType = MetricType.TOTAL_CHAMPIONSHIPS,
                        total = rs.getInt(2),
                        opportunities = rs.getInt(3)
                    ))
                }
            }
        }
        return metroDataList
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
        private val adjustedChampionshipQuery = """
            SELECT main.metro_name,
            main.won_championship,
            main.postseason_opportunities - COALESCE(adj.TOTALS, 0) AS adjusted_postseason_opportunities
            FROM (
                SELECT m.name AS metro_name,
                COUNT(DISTINCT CASE WHEN f.WON_CHAMPIONSHIP THEN f.ID END) AS won_championship,
                COUNT(DISTINCT CASE WHEN f.QUALIFIED_FOR_POSTSEASON IS NOT NULL THEN f.id end) AS postseason_opportunities
                FROM METROS m
                JOIN FRANCHISESEASONS f ON m.ID = f.METRO_ID
                LEFT JOIN SEASONS s ON f.SEASON_ID = s.ID
                GROUP BY m.name
            ) AS main
            LEFT JOIN (
                SELECT subq.metro_name, SUM(subq.totals) AS TOTALS
                FROM (
                    SELECT m.NAME AS metro_name, COUNT(1) - 1 AS totals
                    FROM METROS m
                    JOIN (
                        SELECT DISTINCT METRO_ID, LEAGUE_ID, SEASON_ID
                        FROM FRANCHISESEASONS
                        WHERE won_championship = true
                    ) championship_seasons ON m.ID = championship_seasons.METRO_ID
                    JOIN FRANCHISESEASONS fs ON m.ID = fs.METRO_ID AND fs.SEASON_ID = championship_seasons.SEASON_ID
                    LEFT JOIN SEASONS s ON fs.SEASON_ID = s.ID
                    JOIN LEAGUES l ON l.ID = s.LEAGUE_ID
                    GROUP BY m.NAME, s.LEAGUE_ID, s.ID
                    HAVING totals > 0
                ) AS subq
            GROUP BY subq.metro_name
            ) AS adj ON main.metro_name = adj.metro_name;
        """.trimIndent()
        private val activeMetrosQuery = """
            SELECT distinct(m.name)
            FROM FRANCHISESEASONS f 
            JOIN seasons s ON s.ID = f.SEASON_ID 
            JOIN METROS m ON m.ID = f.metro_id
            JOIN (
                SELECT LEAGUE_ID, MAX(END_YEAR) AS MOST_RECENT_SEASON
                FROM seasons
                GROUP BY LEAGUE_ID
            ) recent_seasons
            ON s.LEAGUE_ID = recent_seasons.LEAGUE_ID AND s.END_YEAR = recent_seasons.MOST_RECENT_SEASON
            GROUP BY m.id, m.name
        """.trimIndent()
    }
}