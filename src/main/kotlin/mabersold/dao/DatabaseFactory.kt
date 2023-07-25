package mabersold.dao

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import kotlinx.coroutines.Dispatchers
import mabersold.models.db.FranchisePostseasons
import mabersold.models.db.FranchiseSeasons
import mabersold.models.db.Franchises
import mabersold.models.db.League
import mabersold.models.db.Leagues
import mabersold.models.db.Metros
import mabersold.models.db.PostSeasons
import mabersold.models.db.Seasons
import mabersold.models.db.Standing
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(createSchema: Boolean = false) {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        if (createSchema) {
            createSchema(database)
        }
    }

    private fun createSchema(database: Database) {
        transaction(database) {
            val tables = listOf(FranchisePostseasons, FranchiseSeasons, Seasons, PostSeasons, Franchises, Metros, Leagues)

            // Drop all tables, if they exist
            tables.forEach { table ->
                if(table.exists()) {
                    SchemaUtils.drop(table)
                }
            }

            // Create tables
            tables.forEach { table ->
                SchemaUtils.create(table)
            }

            // Populate leagues and metro areas
            populate("data/leagues.csv", ::insertLeague)
            populate("data/metros.csv", ::insertMetro)

            val leagueIds: Map<String, Int> = Leagues.selectAll().associate { row ->
                row[Leagues.name] to row[Leagues.id].value
            }

            val metroIds: Map<String, Int> = Metros.selectAll().associate { row ->
                row[Metros.name] to row[Metros.id].value
            }

            val leagues = Leagues.selectAll().map { row ->
                League(
                    id = row[Leagues.id].value,
                    name = row[Leagues.name],
                    sport = row[Leagues.sport]
                )
            }

            // Populate franchises
            populate("data/baseball/mlb-franchises.csv", leagues, ::insertFranchise)
            populate("data/basketball/nba-franchises.csv", leagues, ::insertFranchise)
            populate("data/hockey/nhl-franchises.csv", leagues, ::insertFranchise)
            populate("data/football/nfl-franchises.csv", leagues, ::insertFranchise)

            val franchiseIds: Map<String, Int> = Franchises.selectAll().associate { row ->
                row[Franchises.name] to row[Franchises.id].value
            }

            // Populate seasons
            populate("data/baseball/mlb-seasons.csv", leagues, ::insertSeason)
            populate("data/basketball/nba-seasons.csv", leagues, ::insertSeason)
            populate("data/hockey/nhl-seasons.csv", leagues, ::insertSeason)
            populate("data/football/nfl-seasons.csv", leagues, ::insertSeason)

            val seasonIds: Map<Pair<Int, Int>, Int> = Seasons.selectAll().associate { row ->
                Pair(row[Seasons.leagueId].value, row[Seasons.startYear]) to row[Seasons.id].value
            }

            // Populate postseasons
            populate("data/baseball/mlb-postseasons.csv", leagues, ::insertPostSeason)
            populate("data/basketball/nba-postseasons.csv", leagues, ::insertPostSeason)
            populate("data/hockey/nhl-postseasons.csv", leagues, ::insertPostSeason)
            populate("data/football/nfl-postseasons.csv", leagues, ::insertPostSeason)

            val postseasonIds: Map<Pair<Int, Int>, Int> = PostSeasons.selectAll().associate { row ->
                Pair(row[PostSeasons.leagueId].value, row[PostSeasons.year]) to row[PostSeasons.id].value
            }

            // Populate franchise seasons and postseasons
            val franchiseSeasonData = mapOf(
                "data/baseball/seasons/arizona-diamondbacks-seasons.csv" to "Arizona Diamondbacks",
                "data/hockey/seasons/arizona-coyotes-seasons.csv" to "Arizona Coyotes",
                "data/basketball/seasons/phoenix-suns-seasons.csv" to "Phoenix Suns",
                "data/football/seasons/arizona-cardinals-seasons.csv" to "Arizona Cardinals"
            )

            val franchisePostSeasonData = mapOf(
                "data/baseball/postseasons/arizona-diamondbacks-postseasons.csv" to "Arizona Diamondbacks",
                "data/hockey/postseasons/arizona-coyotes-postseasons.csv" to "Arizona Coyotes",
                "data/basketball/postseasons/phoenix-suns-postseasons.csv" to "Phoenix Suns",
                "data/football/postseasons/arizona-cardinals-postseasons.csv" to "Arizona Cardinals"
            )

            franchiseSeasonData.forEach { (filename, franchiseName) ->
                populateFranchiseSeasons(filename, seasonIds, franchiseIds[franchiseName]!!, metroIds, leagueIds)
            }

            franchisePostSeasonData.forEach { (filename, franchiseName) ->
                populateFranchisePostseasons(filename, postseasonIds, franchiseIds[franchiseName]!!, metroIds, leagueIds)
            }
        }
    }

    private fun populate(fileName: String, insertFunction: (Map<String, String>) -> Unit) {
        val data = getCSVData(fileName)

        for (row in data) {
            insertFunction(row)
        }
    }

    private fun populate(fileName: String, leagues: List<League>, insertFunction: (Map<String, String>, List<League>) -> Unit) {
        val data = getCSVData(fileName)

        for (row in data) {
            insertFunction(row, leagues)
        }
    }

    private fun getCSVData(fileName: String): List<Map<String, String>> {
        val resource = this::class.java.classLoader.getResource(fileName)?.file ?: throw Exception("Could not find $fileName")

        val file = File(resource)
        return csvReader().readAllWithHeader(file)
    }

    private fun insertLeague(csvRow: Map<String, String>) = Leagues.insert {
        it[name] = csvRow["name"]!!
        it[sport] = csvRow["sport"]!!
    }

    private fun insertMetro(csvRow: Map<String, String>) = Metros.insert {
        it[name] = csvRow["name"]!!
    }

    private fun insertFranchise(csvRow: Map<String, String>, leagues: List<League>) = Franchises.insert {
        it[name] = csvRow["name"]!!
        it[isDefunct] = csvRow["is_defunct"]!!.toBoolean()
        it[leagueId] = leagues.first { league -> league.name == csvRow["league"]!! }.id
    }

    private fun insertSeason(csvRow: Map<String, String>, leagues: List<League>) = Seasons.insert {
        it[name] = csvRow["name"]!!
        it[startYear] = csvRow["start_year"]!!.toInt()
        it[endYear] = csvRow["end_year"]!!.toInt()
        it[leagueId] = leagues.first { league -> league.name == csvRow["league"]!! }.id
        it[totalMajorDivisions] = csvRow["total_major_divisions"]!!.toInt()
        it[totalMinorDivisions] = csvRow["total_minor_divisions"]!!.toInt()
    }

    private fun insertPostSeason(csvRow: Map<String, String>, leagues: List<League>) = PostSeasons.insert {
        it[name] = csvRow["name"]!!
        it[year] = csvRow["year"]!!.toInt()
        it[leagueId] = leagues.first { league -> league.name == csvRow["league"]!! }.id
        it[numberOfRounds] = csvRow["number_of_rounds"]!!.toInt()
    }

    private fun populateFranchiseSeasons(fileName: String, seasonIds: Map<Pair<Int, Int>, Int>, teamId: Int, metroIds: Map<String, Int>, leagueIds: Map<String, Int>) {
        val data = getCSVData(fileName)

        for (row in data) {
            FranchiseSeasons.insert {
                it[seasonId] = seasonIds[Pair(leagueIds[row["league"]], row["season"]!!.toInt())] ?: throw Exception("Could not find season ${row["season"]}")
                it[franchiseId] = teamId
                it[metroId] = metroIds[row["metro"]] ?: throw Exception("Could not find metro ${row["metro"]}")
                it[teamName] = row["team_name"] ?: throw Exception("Could not find team name")
                it[leagueId] = leagueIds[row["league"]] ?: throw Exception("Could not find league ${row["league"]}")
                it[conference] = if (row["conference"].isNullOrBlank()) null else row["conference"]
                it[division] = if (row["division"].isNullOrBlank()) null else row["division"]
                it[leaguePosition] = if (row["league_position"].isNullOrBlank()) null else Standing.valueOf(row["league_position"]!!)
                it[conferencePosition] = if (row["conference_position"].isNullOrBlank()) null else Standing.valueOf(row["conference_position"]!!)
                it[divisionPosition] = if (row["division_position"].isNullOrBlank()) null else Standing.valueOf(row["division_position"]!!)
            }
        }
    }

    private fun populateFranchisePostseasons(fileName: String, postseasonIds: Map<Pair<Int, Int>, Int>, teamId: Int, metroIds: Map<String, Int>, leagueIds: Map<String, Int>) {
        val data = getCSVData(fileName)

        for (row in data) {
            FranchisePostseasons.insert {
                it[postSeasonId] = postseasonIds[Pair(leagueIds[row["league"]], row["postseason"]!!.toInt())] ?: throw Exception("Could not find postseason ${row["postseason"]}")
                it[franchiseId] = teamId
                it[metroId] = metroIds[row["metro"]] ?: throw Exception("Could not find metro ${row["metro"]}")
                it[leagueId] = leagueIds[row["league"]] ?: throw Exception("Could not find league ${row["league"]}")
                it[roundsWon] = row["rounds_won"]!!.toInt()
                it[appearedInChampionship] = row["appeared_in_championship"]!!.toBoolean()
                it[wonChampionship] = row["won_championship"]!!.toBoolean()
            }
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}