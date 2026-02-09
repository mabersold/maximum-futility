package mabersold.dao

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import kotlinx.coroutines.Dispatchers
import mabersold.models.db.Chapters
import mabersold.models.db.FranchiseSeasons
import mabersold.models.db.Franchises
import mabersold.models.db.League
import mabersold.models.db.Leagues
import mabersold.models.db.MetroLeagueYears
import mabersold.models.db.Metros
import mabersold.models.db.Seasons
import mabersold.models.db.Standing
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.text.isNullOrBlank
import kotlin.text.toInt

object DatabaseFactory {
    fun init(createSchema: Boolean = false, populate: Boolean = false) {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        if (createSchema) {
            createSchema(database)
        }
        if (populate) {
            populateDB(database)
        }
    }

    private fun createSchema(database: Database) {
        transaction(database) {
            val tables = listOf(MetroLeagueYears, Chapters, FranchiseSeasons, Seasons, Franchises, Metros, Leagues)

            // Drop all tables, if they exist
            tables.forEach { table ->
                if (table.exists()) {
                    SchemaUtils.drop(table)
                }
            }

            // Create tables
            tables.forEach { table ->
                SchemaUtils.create(table)
            }
        }
    }

    private fun populateDB(database: Database) {
        transaction(database) {
            // Populate leagues and metro areas
            populate("data/leagues.csv", ::insertLeague)
            populate("data/metros.csv", ::insertMetro)

            val leagues = Leagues.selectAll().map { row ->
                League(
                    id = row[Leagues.id].value,
                    name = row[Leagues.name],
                    sport = row[Leagues.sport],
                    label = row[Leagues.label]
                )
            }

            // Populate franchises
            leagues.forEach { league ->
                populate("data/${league.label}/${league.label}-franchises.csv", ::insertFranchise)
                populate("data/${league.label}/${league.label}-seasons.csv", ::insertSeason)
                populate("data/${league.label}/${league.label}-franchise-seasons.csv", ::insertFranchiseSeason)
            }

            populate("data/metro-league-years.csv", ::insertMetroLeagueYear)
        }
    }

    private fun populate(fileName: String, insertFunction: (Map<String, String>) -> Unit) {
        val data = getCSVData(fileName)

        for (row in data) {
            insertFunction(row)
        }
    }

    private fun getCSVData(fileName: String): List<Map<String, String>> {
        val resource = this::class.java.classLoader.getResource(fileName)?.file ?: throw Exception("Could not find $fileName")

        val file = File(resource)
        return csvReader().readAllWithHeader(file)
    }

    private fun insertLeague(csvRow: Map<String, String>) = Leagues.insert {
        it[id] = EntityID(csvRow["id"]!!.toInt(), Leagues)
        it[name] = csvRow["name"]!!
        it[sport] = csvRow["sport"]!!
        it[label] = csvRow["label"]!!
    }

    private fun insertMetro(csvRow: Map<String, String>) = Metros.insert {
        it[id] = EntityID(csvRow["id"]!!.toInt(), Metros)
        it[name] = csvRow["name"]!!
        it[label] = csvRow["label"]!!
    }

    private fun insertFranchise(csvRow: Map<String, String>) = Franchises.insert {
        it[id] = EntityID(csvRow["id"]!!.toInt(), Franchises)
        it[name] = csvRow["name"]!!
        it[isDefunct] = csvRow["is_defunct"]!!.toBoolean()
        it[leagueId] = csvRow["league_id"]!!.toInt()
        it[label] = csvRow["label"]!!
    }

    private fun insertSeason(csvRow: Map<String, String>) = Seasons.insert {
        it[id] = EntityID(csvRow["id"]!!.toInt(), Seasons)
        it[name] = csvRow["name"]!!
        it[startYear] = csvRow["start_year"]!!.toInt()
        it[endYear] = csvRow["end_year"]!!.toInt()
        it[leagueId] = csvRow["league_id"]!!.toInt()
        it[totalMajorDivisions] = csvRow["total_major_divisions"]!!.toInt()
        it[totalMinorDivisions] = csvRow["total_minor_divisions"]!!.toInt()
        it[postSeasonRounds] = csvRow["postseason_rounds"].asNullableInt()
    }

    private fun insertFranchiseSeason(csvRow: Map<String, String>) = FranchiseSeasons.insert {
        it[franchiseId] = csvRow["franchise_id"]!!.toInt()
        it[seasonId] = csvRow["season_id"]!!.toInt()
        it[metroId] = csvRow["metro_id"]!!.toInt()
        it[teamName] = csvRow["team_name"] ?: throw Exception("Could not find team name")
        it[leagueId] = csvRow["league_id"]!!.toInt()
        it[conference] = if (csvRow["conference"].isNullOrBlank()) null else csvRow["conference"]
        it[division] = if (csvRow["division"].isNullOrBlank()) null else csvRow["division"]
        it[leaguePosition] = if (csvRow["league_position"].isNullOrBlank()) null else Standing.valueOf(csvRow["league_position"]!!)
        it[conferencePosition] = if (csvRow["conference_position"].isNullOrBlank()) null else Standing.valueOf(csvRow["conference_position"]!!)
        it[divisionPosition] = if (csvRow["division_position"].isNullOrBlank()) null else Standing.valueOf(csvRow["division_position"]!!)
        it[qualifiedForPostseason] = csvRow["qualified_for_postseason"].asNullableBoolean()
        it[roundsWon] = csvRow["rounds_won"].asNullableInt()
        it[appearedInChampionship] = csvRow["appeared_in_championship"].asNullableBoolean()
        it[wonChampionship] = csvRow["won_championship"].asNullableBoolean()
    }

    private fun insertMetroLeagueYear(csvRow: Map<String, String>) = MetroLeagueYears.insert {
        it[year] = csvRow["year"]!!.toInt()
        it[leagueId] = csvRow["league_id"]!!.toInt()
        it[metroId] = csvRow["metro_id"]!!.toInt()
        it[championships] = csvRow["championships"]!!.toInt()
        it[championshipOpportunities] = csvRow["opps_championships"]!!.toInt()
        it[championshipAppearances] = csvRow["appeared_in_championship"]!!.toInt()
        it[championshipAppearanceOpportunities] = csvRow["opps_appeared_in_championship"]!!.toInt()
        it[advancedInPostseason] = csvRow["advanced_in_postseason"]!!.toInt()
        it[advancedInPostseasonOpportunities] = csvRow["opps_advanced_in_postseason"]!!.toInt()
        it[qualifiedForPostseason] = csvRow["qualified_for_postseason"]!!.toInt()
        it[qualifiedForPostseasonOpportunities] = csvRow["opps_qualified_for_postseason"]!!.toInt()
        it[overallOpportunities] = csvRow["opps_overall"]!!.toInt()
        it[totalFirstOverall] = csvRow["first_overall"]!!.toInt()
        it[totalLastOverall] = csvRow["last_overall"]!!.toInt()
        it[conferenceOpportunities] = csvRow["opps_conference"]!!.toInt()
        it[totalFirstConference] = csvRow["first_conference"]!!.toInt()
        it[totalLastConference] = csvRow["last_conference"]!!.toInt()
        it[divisionOpportunities] = csvRow["opps_division"]!!.toInt()
        it[totalFirstDivision] = csvRow["first_division"]!!.toInt()
        it[totalLastDivision] = csvRow["last_division"]!!.toInt()
    }

    private fun String?.asNullableBoolean(): Boolean? = when (this) {
        "true" -> true
        "false" -> false
        else -> null
    }

    private fun String?.asNullableInt(): Int? = when (this) {
        null, "" -> null
        else -> this.toInt()
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}