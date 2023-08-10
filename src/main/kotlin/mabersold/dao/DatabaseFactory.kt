package mabersold.dao

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import kotlinx.coroutines.Dispatchers
import mabersold.models.db.FranchiseSeasons
import mabersold.models.db.Franchises
import mabersold.models.db.League
import mabersold.models.db.Leagues
import mabersold.models.db.Metros
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
            val tables = listOf(FranchiseSeasons, Seasons, Franchises, Metros, Leagues)

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

            // Populate franchise seasons and postseasons
            val franchiseSeasonData = mapOf(
                "data/baseball/seasons/arizona-diamondbacks.csv" to "Arizona Diamondbacks",
                "data/baseball/seasons/atlanta-braves.csv" to "Atlanta Braves",
                "data/baseball/seasons/baltimore-orioles.csv" to "Baltimore Orioles",
                "data/baseball/seasons/boston-red-sox.csv" to "Boston Red Sox",
                "data/baseball/seasons/chicago-cubs.csv" to "Chicago Cubs",
                "data/baseball/seasons/chicago-white-sox.csv" to "Chicago White Sox",
                "data/baseball/seasons/cincinnati-reds.csv" to "Cincinnati Reds",
                "data/baseball/seasons/cleveland-guardians.csv" to "Cleveland Guardians",
                "data/baseball/seasons/colorado-rockies.csv" to "Colorado Rockies",
                "data/baseball/seasons/detroit-tigers.csv" to "Detroit Tigers",
                "data/baseball/seasons/houston-astros.csv" to "Houston Astros",
                "data/baseball/seasons/kansas-city-royals.csv" to "Kansas City Royals",
                "data/baseball/seasons/los-angeles-angels.csv" to "Los Angeles Angels",
                "data/baseball/seasons/los-angeles-dodgers.csv" to "Los Angeles Dodgers",
                "data/baseball/seasons/miami-marlins.csv" to "Miami Marlins",
                "data/baseball/seasons/milwaukee-brewers.csv" to "Milwaukee Brewers",
                "data/baseball/seasons/minnesota-twins.csv" to "Minnesota Twins",
                "data/baseball/seasons/new-york-mets.csv" to "New York Mets",
                "data/baseball/seasons/new-york-yankees.csv" to "New York Yankees",
                "data/baseball/seasons/oakland-athletics.csv" to "Oakland Athletics",
                "data/baseball/seasons/philadelphia-phillies.csv" to "Philadelphia Phillies",
                "data/baseball/seasons/pittsburgh-pirates.csv" to "Pittsburgh Pirates",
                "data/baseball/seasons/san-diego-padres.csv" to "San Diego Padres",
                "data/baseball/seasons/san-francisco-giants.csv" to "San Francisco Giants",
                "data/baseball/seasons/seattle-mariners.csv" to "Seattle Mariners",
                "data/baseball/seasons/st-louis-cardinals.csv" to "St. Louis Cardinals",
                "data/baseball/seasons/tampa-bay-rays.csv" to "Tampa Bay Rays",
                "data/baseball/seasons/texas-rangers.csv" to "Texas Rangers",
                "data/baseball/seasons/toronto-blue-jays.csv" to "Toronto Blue Jays",
                "data/baseball/seasons/washington-nationals.csv" to "Washington Nationals",
                "data/basketball/seasons/atlanta-hawks.csv" to "Atlanta Hawks",
                "data/basketball/seasons/baltimore-bullets.csv" to "Baltimore Bullets (original)",
                "data/basketball/seasons/boston-celtics.csv" to "Boston Celtics",
                "data/basketball/seasons/brooklyn-nets.csv" to "Brooklyn Nets",
                "data/basketball/seasons/charlotte-hornets.csv" to "Charlotte Hornets",
                "data/basketball/seasons/chicago-bulls.csv" to "Chicago Bulls",
                "data/basketball/seasons/chicago-stags.csv" to "Chicago Stags",
                "data/basketball/seasons/cleveland-cavaliers.csv" to "Cleveland Cavaliers",
                "data/basketball/seasons/dallas-mavericks.csv" to "Dallas Mavericks",
                "data/basketball/seasons/denver-nuggets.csv" to "Denver Nuggets",
                "data/basketball/seasons/denver-nuggets-original.csv" to "Denver Nuggets (original)",
                "data/basketball/seasons/detroit-pistons.csv" to "Detroit Pistons",
                "data/basketball/seasons/golden-state-warriors.csv" to "Golden State Warriors",
                "data/basketball/seasons/houston-rockets.csv" to "Houston Rockets",
                "data/basketball/seasons/indiana-pacers.csv" to "Indiana Pacers",
                "data/basketball/seasons/indianapolis-jets.csv" to "Indianapolis Jets",
                "data/basketball/seasons/indianapolis-olympians.csv" to "Indianapolis Olympians",
                "data/basketball/seasons/los-angeles-clippers.csv" to "Los Angeles Clippers",
                "data/basketball/seasons/los-angeles-lakers.csv" to "Los Angeles Lakers",
                "data/basketball/seasons/memphis-grizzlies.csv" to "Memphis Grizzlies",
                "data/basketball/seasons/miami-heat.csv" to "Miami Heat",
                "data/basketball/seasons/milwaukee-bucks.csv" to "Milwaukee Bucks",
                "data/basketball/seasons/minnesota-timberwolves.csv" to "Minnesota Timberwolves",
                "data/basketball/seasons/new-orleans-pelicans.csv" to "New Orleans Pelicans",
                "data/basketball/seasons/new-york-knicks.csv" to "New York Knicks",
                "data/basketball/seasons/oklahoma-city-thunder.csv" to "Oklahoma City Thunder",
                "data/basketball/seasons/orlando-magic.csv" to "Orlando Magic",
                "data/basketball/seasons/philadelphia-76ers.csv" to "Philadelphia 76ers",
                "data/basketball/seasons/phoenix-suns.csv" to "Phoenix Suns",
                "data/basketball/seasons/pittsburgh-ironmen.csv" to "Pittsburgh Ironmen",
                "data/basketball/seasons/portland-trail-blazers.csv" to "Portland Trail Blazers",
                "data/basketball/seasons/providence-steamrollers.csv" to "Providence Steamrollers",
                "data/basketball/seasons/sacramento-kings.csv" to "Sacramento Kings",
                "data/basketball/seasons/san-antonio-spurs.csv" to "San Antonio Spurs",
                "data/basketball/seasons/st-louis-bombers.csv" to "St. Louis Bombers",
                "data/basketball/seasons/toronto-huskies.csv" to "Toronto Huskies",
                "data/basketball/seasons/toronto-raptors.csv" to "Toronto Raptors",
                "data/basketball/seasons/utah-jazz.csv" to "Utah Jazz",
                "data/basketball/seasons/washington-capitols.csv" to "Washington Capitols",
                "data/basketball/seasons/washington-wizards.csv" to "Washington Wizards",
                "data/football/seasons/arizona-cardinals-seasons.csv" to "Arizona Cardinals",
                "data/football/seasons/tampa-bay-buccaneers-seasons.csv" to "Tampa Bay Buccaneers",
                "data/hockey/seasons/arizona-coyotes-seasons.csv" to "Arizona Coyotes",
                "data/hockey/seasons/tampa-bay-lightning-seasons.csv" to "Tampa Bay Lightning",
            )

            franchiseSeasonData.forEach { (filename, franchiseName) ->
                populateFranchiseSeasons(filename, seasonIds, franchiseIds[franchiseName]!!, metroIds, leagueIds)
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
        it[postSeasonRounds] = csvRow["postseason_rounds"].getNullableInt()
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
                it[qualifiedForPostseason] = row["qualified_for_postseason"].getNullableBoolean()
                it[roundsWon] = row["rounds_won"].getNullableInt()
                it[appearedInChampionship] = row["appeared_in_championship"].getNullableBoolean()
                it[wonChampionship] = row["won_championship"].getNullableBoolean()
            }
        }
    }

    private fun String?.getNullableBoolean(): Boolean? = when (this) {
        "true" -> true
        "false" -> false
        else -> null
    }

    private fun String?.getNullableInt(): Int? = when (this) {
        null, "" -> null
        else -> this.toInt()
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}