package mabersold.dao

import java.sql.ResultSet
import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.MetricType
import mabersold.models.MetroData
import mabersold.models.db.FranchisePostseason
import mabersold.models.db.FranchisePostseasons
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager

class FranchisePostSeasonDAOImpl : FranchisePostSeasonDAO {
    private fun resultRowToFranchisePostseason(row: ResultRow) = FranchisePostseason(
        id = row[FranchisePostseasons.id].value,
        postSeasonId = row[FranchisePostseasons.postSeasonId].value,
        franchiseId = row[FranchisePostseasons.franchiseId].value,
        metroId = row[FranchisePostseasons.metroId].value,
        leagueId = row[FranchisePostseasons.leagueId].value,
        roundsWon = row[FranchisePostseasons.roundsWon],
        appearedInChampionship = row[FranchisePostseasons.appearedInChampionship],
        wonChampionship = row[FranchisePostseasons.wonChampionship]
    )

    private fun resultSetToMetroData(rs: ResultSet) = MetroData(
        name = rs.getString(1),
        metricType = MetricType.TOTAL_CHAMPIONSHIPS,
        total = rs.getInt(2),
        opportunities = rs.getInt(3)
    )

    override suspend fun all(): List<FranchisePostseason> = dbQuery {
        FranchisePostseasons.selectAll().map(::resultRowToFranchisePostseason)
    }

    override suspend fun postSeasonResultsByMetro(metricType: MetricType): List<MetroData> {
        val query = when(metricType) {
            MetricType.TOTAL_CHAMPIONSHIPS -> championshipsQuery
            MetricType.CHAMPIONSHIP_APPEARANCES -> championshipAppearancesQuery
            MetricType.ADVANCED_IN_PLAYOFFS -> advancedInPlayoffsQuery
            MetricType.QUALIFIED_FOR_PLAYOFFS -> qualifiedForPlayoffsQuery
            else -> throw Exception("Invalid metric type")
        }

        val metroDataList = mutableListOf<MetroData>()
        return dbQuery {
            TransactionManager.current().exec(query) { rs ->
                while (rs.next()) {
                    metroDataList.add(resultSetToMetroData(rs))
                }
            }

            metroDataList
        }
    }

    companion object {
        private const val wonChampionshipColumn = "       COUNT(DISTINCT CASE WHEN f.WON_CHAMPIONSHIP THEN f.ID END) AS won_championship,\n"
        private const val appearedInChampionshipColumn = "       COUNT(DISTINCT CASE WHEN f.APPEARED_IN_CHAMPIONSHIP THEN f.ID END) AS appeared_in_championship,\n"
        private const val qualifiedForPlayoffsColumn = "       COUNT(DISTINCT f.ID) AS qualified_for_playoffs,\n"

        private const val commonQueryStart = "SELECT m.NAME,\n"
        private const val commonQueryEnd = """
                COUNT(DISTINCT p.ID) AS postseason_opportunities
                FROM METROS m
                JOIN FRANCHISEPOSTSEASONS f ON m.ID = f.METRO_ID
                LEFT JOIN FranchiseSeasons fs ON m.ID = fs.METRO_ID
                LEFT JOIN Seasons s ON fs.SEASON_ID = s.ID
                LEFT JOIN Postseasons p ON s.LEAGUE_ID = p.LEAGUE_ID AND s.END_YEAR = p."YEAR"
                GROUP BY m.NAME;
        """

        private const val championshipsQuery = "$commonQueryStart$wonChampionshipColumn$commonQueryEnd"
        private const val championshipAppearancesQuery = "$commonQueryStart$appearedInChampionshipColumn$commonQueryEnd"
        private const val advancedInPlayoffsQuery = """
            SELECT m.NAME,
                COUNT(DISTINCT CASE WHEN f.ROUNDS_WON > 0 AND p.NUMBER_OF_ROUNDS > 1 THEN f.ID END) AS advanced_in_playoffs,
                COUNT(DISTINCT CASE WHEN p.NUMBER_OF_ROUNDS  > 1 THEN p.ID END) AS postseason_opportunities
            FROM METROS m
            JOIN FRANCHISEPOSTSEASONS f ON m.ID = f.METRO_ID
            LEFT JOIN FranchiseSeasons fs ON m.ID = fs.METRO_ID
            LEFT JOIN Seasons s ON fs.SEASON_ID = s.ID
            LEFT JOIN Postseasons p ON s.LEAGUE_ID = p.LEAGUE_ID AND s.END_YEAR = p."YEAR"
            GROUP BY m.NAME;
        """
        private const val qualifiedForPlayoffsQuery = "$commonQueryStart$qualifiedForPlayoffsColumn$commonQueryEnd"
    }
}