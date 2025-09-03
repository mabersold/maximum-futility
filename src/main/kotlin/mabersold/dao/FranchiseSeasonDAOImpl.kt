package mabersold.dao

import io.ktor.server.html.insert
import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.FranchiseSeasonInfo
import mabersold.models.db.FranchiseSeason
import mabersold.models.db.FranchiseSeasons
import mabersold.models.db.Metros
import mabersold.models.db.Seasons
import mabersold.models.db.Standing
import mabersold.models.intermediary.ProtoFranchiseSeason
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
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

    private fun resultRowToFranchiseSeasonInfo(row: ResultRow) = FranchiseSeasonInfo(
        metro = row[Metros.name],
        teamName = row[FranchiseSeasons.teamName],
        conference = row[FranchiseSeasons.conference],
        division = row[FranchiseSeasons.division],
        leaguePosition = row[FranchiseSeasons.leaguePosition],
        conferencePosition = row[FranchiseSeasons.conferencePosition],
        divisionPosition = row[FranchiseSeasons.divisionPosition],
        qualifiedForPostseason = row[FranchiseSeasons.qualifiedForPostseason],
        roundsWon = row[FranchiseSeasons.roundsWon],
        appearedInChampionship = row[FranchiseSeasons.appearedInChampionship],
        wonChampionship = row[FranchiseSeasons.wonChampionship],
        totalConferences = row[Seasons.totalMajorDivisions],
        totalDivisions = row[Seasons.totalMinorDivisions],
        postSeasonRounds = row[Seasons.postSeasonRounds],
        leagueId = row[FranchiseSeasons.leagueId].value,
        seasonId = row[FranchiseSeasons.seasonId].value,
        startYear = row[Seasons.startYear],
        endYear = row[Seasons.endYear]
    )

    override suspend fun all(): List<FranchiseSeasonInfo> = dbQuery {
        (Metros innerJoin FranchiseSeasons innerJoin Seasons)
            .select(
                Metros.name,
                FranchiseSeasons.teamName,
                FranchiseSeasons.leagueId,
                FranchiseSeasons.conference,
                FranchiseSeasons.division,
                FranchiseSeasons.leaguePosition,
                FranchiseSeasons.conferencePosition,
                FranchiseSeasons.divisionPosition,
                FranchiseSeasons.qualifiedForPostseason,
                FranchiseSeasons.roundsWon,
                FranchiseSeasons.appearedInChampionship,
                FranchiseSeasons.wonChampionship,
                FranchiseSeasons.seasonId,
                Seasons.totalMajorDivisions,
                Seasons.totalMinorDivisions,
                Seasons.postSeasonRounds,
                Seasons.startYear,
                Seasons.endYear
            )
            .map(::resultRowToFranchiseSeasonInfo)
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
        FranchiseSeasons.selectAll().where { FranchiseSeasons.seasonId eq seasonId }
            .map(::resultRowToFranchiseSeason)
    }

    override suspend fun create(protoFranchiseSeason: ProtoFranchiseSeason): FranchiseSeason? = dbQuery {
        val insertStatement = FranchiseSeasons.insert {
            it[FranchiseSeasons.seasonId] = protoFranchiseSeason.seasonId
            it[FranchiseSeasons.franchiseId] = protoFranchiseSeason.franchiseId
            it[FranchiseSeasons.metroId] = protoFranchiseSeason.metroId
            it[FranchiseSeasons.teamName] = protoFranchiseSeason.teamName
            it[FranchiseSeasons.leagueId] = protoFranchiseSeason.leagueId
            it[FranchiseSeasons.conference] = protoFranchiseSeason.conferenceName
            it[FranchiseSeasons.division] = protoFranchiseSeason.divisionName
            it[FranchiseSeasons.leaguePosition] = protoFranchiseSeason.leaguePosition
            it[FranchiseSeasons.conferencePosition] = protoFranchiseSeason.conferencePosition
            it[FranchiseSeasons.divisionPosition] = protoFranchiseSeason.divisionPosition
            it[FranchiseSeasons.qualifiedForPostseason] = protoFranchiseSeason.qualifiedForPostseason
            it[FranchiseSeasons.roundsWon] = protoFranchiseSeason.roundsWon
            it[FranchiseSeasons.appearedInChampionship] = protoFranchiseSeason.appearedInChampionship
            it[FranchiseSeasons.wonChampionship] = protoFranchiseSeason.wonChampionship
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToFranchiseSeason)
    }

    override suspend fun createAll(protoFranchiseSeasons: List<ProtoFranchiseSeason>): List<FranchiseSeason> = dbQuery {
        FranchiseSeasons.batchInsert(protoFranchiseSeasons) { proto ->
            this[FranchiseSeasons.seasonId] = proto.seasonId
            this[FranchiseSeasons.franchiseId] = proto.franchiseId
            this[FranchiseSeasons.metroId] = proto.metroId
            this[FranchiseSeasons.teamName] = proto.teamName
            this[FranchiseSeasons.leagueId] = proto.leagueId
            this[FranchiseSeasons.conference] = proto.conferenceName
            this[FranchiseSeasons.division] = proto.divisionName
            this[FranchiseSeasons.leaguePosition] = proto.leaguePosition
            this[FranchiseSeasons.conferencePosition] = proto.conferencePosition
            this[FranchiseSeasons.divisionPosition] = proto.divisionPosition
            this[FranchiseSeasons.qualifiedForPostseason] = proto.qualifiedForPostseason
            this[FranchiseSeasons.roundsWon] = proto.roundsWon
            this[FranchiseSeasons.appearedInChampionship] = proto.appearedInChampionship
            this[FranchiseSeasons.wonChampionship] = proto.wonChampionship
        }.map(::resultRowToFranchiseSeason)
    }

    companion object {
        private val activeMetrosQuery = """
            SELECT distinct(m."name")
            FROM FRANCHISESEASONS f 
            JOIN seasons s ON s.ID = f.SEASON_ID 
            JOIN METROS m ON m.ID = f.metro_id
            JOIN (
                SELECT LEAGUE_ID, MAX(END_YEAR) AS MOST_RECENT_SEASON
                FROM seasons
                GROUP BY LEAGUE_ID
            ) recent_seasons
            ON s.LEAGUE_ID = recent_seasons.LEAGUE_ID AND s.END_YEAR = recent_seasons.MOST_RECENT_SEASON
            GROUP BY m.id, m."name"
        """.trimIndent()
    }
}