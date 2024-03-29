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
                "data/basketball/seasons/anderson-packers.csv" to "Anderson Packers",
                "data/basketball/seasons/atlanta-hawks.csv" to "Atlanta Hawks",
                "data/basketball/seasons/baltimore-bullets.csv" to "Baltimore Bullets (original)",
                "data/basketball/seasons/boston-celtics.csv" to "Boston Celtics",
                "data/basketball/seasons/brooklyn-nets.csv" to "Brooklyn Nets",
                "data/basketball/seasons/charlotte-hornets.csv" to "Charlotte Hornets",
                "data/basketball/seasons/chicago-bulls.csv" to "Chicago Bulls",
                "data/basketball/seasons/chicago-stags.csv" to "Chicago Stags",
                "data/basketball/seasons/cleveland-cavaliers.csv" to "Cleveland Cavaliers",
                "data/basketball/seasons/cleveland-rebels.csv" to "Cleveland Rebels",
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
                "data/basketball/seasons/sheboygan-red-skins.csv" to "Sheboygan Red Skins",
                "data/basketball/seasons/st-louis-bombers.csv" to "St. Louis Bombers",
                "data/basketball/seasons/toronto-huskies.csv" to "Toronto Huskies",
                "data/basketball/seasons/toronto-raptors.csv" to "Toronto Raptors",
                "data/basketball/seasons/utah-jazz.csv" to "Utah Jazz",
                "data/basketball/seasons/washington-capitols.csv" to "Washington Capitols",
                "data/basketball/seasons/washington-wizards.csv" to "Washington Wizards",
                "data/football/seasons/arizona-cardinals.csv" to "Arizona Cardinals",
                "data/football/seasons/atlanta-falcons.csv" to "Atlanta Falcons",
                "data/football/seasons/baltimore-colts.csv" to "Baltimore Colts",
                "data/football/seasons/baltimore-ravens.csv" to "Baltimore Ravens",
                "data/football/seasons/buffalo-bills.csv" to "Buffalo Bills",
                "data/football/seasons/carolina-panthers.csv" to "Carolina Panthers",
                "data/football/seasons/chicago-bears.csv" to "Chicago Bears",
                "data/football/seasons/cincinnati-bengals.csv" to "Cincinnati Bengals",
                "data/football/seasons/cleveland-browns.csv" to "Cleveland Browns",
                "data/football/seasons/dallas-cowboys.csv" to "Dallas Cowboys",
                "data/football/seasons/denver-broncos.csv" to "Denver Broncos",
                "data/football/seasons/detroit-lions.csv" to "Detroit Lions",
                "data/football/seasons/green-bay-packers.csv" to "Green Bay Packers",
                "data/football/seasons/houston-texans.csv" to "Houston Texans",
                "data/football/seasons/indianapolis-colts.csv" to "Indianapolis Colts",
                "data/football/seasons/jacksonville-jaguars.csv" to "Jacksonville Jaguars",
                "data/football/seasons/kansas-city-chiefs.csv" to "Kansas City Chiefs",
                "data/football/seasons/las-vegas-raiders.csv" to "Las Vegas Raiders",
                "data/football/seasons/los-angeles-chargers.csv" to "Los Angeles Chargers",
                "data/football/seasons/los-angeles-rams.csv" to "Los Angeles Rams",
                "data/football/seasons/miami-dolphins.csv" to "Miami Dolphins",
                "data/football/seasons/minnesota-vikings.csv" to "Minnesota Vikings",
                "data/football/seasons/new-england-patriots.csv" to "New England Patriots",
                "data/football/seasons/new-orleans-saints.csv" to "New Orleans Saints",
                "data/football/seasons/new-york-giants.csv" to "New York Giants",
                "data/football/seasons/new-york-jets.csv" to "New York Jets",
                "data/football/seasons/philadelphia-eagles.csv" to "Philadelphia Eagles",
                "data/football/seasons/pittsburgh-steelers.csv" to "Pittsburgh Steelers",
                "data/football/seasons/san-francisco-49ers.csv" to "San Francisco 49ers",
                "data/football/seasons/seattle-seahawks.csv" to "Seattle Seahawks",
                "data/football/seasons/tampa-bay-buccaneers.csv" to "Tampa Bay Buccaneers",
                "data/football/seasons/tennessee-titans.csv" to "Tennessee Titans",
                "data/football/seasons/washington-commanders.csv" to "Washington Commanders",
                "data/football/seasons/boston-yanks.csv" to "Boston Yanks",
                "data/football/seasons/brooklyn-dodgers.csv" to "Brooklyn Dodgers",
                "data/football/seasons/cincinnati-reds-nfl.csv" to "Cincinnati Reds (NFL)",
                "data/hockey/seasons/anaheim-ducks.csv" to "Anaheim Ducks",
                "data/hockey/seasons/arizona-coyotes.csv" to "Arizona Coyotes",
                "data/hockey/seasons/boston-bruins.csv" to "Boston Bruins",
                "data/hockey/seasons/buffalo-sabres.csv" to "Buffalo Sabres",
                "data/hockey/seasons/calgary-flames.csv" to "Calgary Flames",
                "data/hockey/seasons/california-golden-seals.csv" to "California Golden Seals",
                "data/hockey/seasons/carolina-hurricanes.csv" to "Carolina Hurricanes",
                "data/hockey/seasons/chicago-blackhawks.csv" to "Chicago Blackhawks",
                "data/hockey/seasons/colorado-avalanche.csv" to "Colorado Avalanche",
                "data/hockey/seasons/columbus-blue-jackets.csv" to "Columbus Blue Jackets",
                "data/hockey/seasons/dallas-stars.csv" to "Dallas Stars",
                "data/hockey/seasons/detroit-red-wings.csv" to "Detroit Red Wings",
                "data/hockey/seasons/edmonton-oilers.csv" to "Edmonton Oilers",
                "data/hockey/seasons/florida-panthers.csv" to "Florida Panthers",
                "data/hockey/seasons/los-angeles-kings.csv" to "Los Angeles Kings",
                "data/hockey/seasons/minnesota-wild.csv" to "Minnesota Wild",
                "data/hockey/seasons/montreal-canadiens.csv" to "Montreal Canadiens",
                "data/hockey/seasons/montreal-maroons.csv" to "Montreal Maroons",
                "data/hockey/seasons/nashville-predators.csv" to "Nashville Predators",
                "data/hockey/seasons/new-jersey-devils.csv" to "New Jersey Devils",
                "data/hockey/seasons/new-york-americans.csv" to "New York Americans",
                "data/hockey/seasons/new-york-islanders.csv" to "New York Islanders",
                "data/hockey/seasons/new-york-rangers.csv" to "New York Rangers",
                "data/hockey/seasons/ottawa-senators.csv" to "Ottawa Senators",
                "data/hockey/seasons/ottawa-senators-original.csv" to "Ottawa Senators (original)",
                "data/hockey/seasons/philadelphia-flyers.csv" to "Philadelphia Flyers",
                "data/hockey/seasons/pittsburgh-penguins.csv" to "Pittsburgh Penguins",
                "data/hockey/seasons/pittsburgh-pirates-nhl.csv" to "Pittsburgh Pirates (NHL)",
                "data/hockey/seasons/san-jose-sharks.csv" to "San Jose Sharks",
                "data/hockey/seasons/seattle-kraken.csv" to "Seattle Kraken",
                "data/hockey/seasons/st-louis-blues.csv" to "St. Louis Blues",
                "data/hockey/seasons/tampa-bay-lightning.csv" to "Tampa Bay Lightning",
                "data/hockey/seasons/toronto-maple-leafs.csv" to "Toronto Maple Leafs",
                "data/hockey/seasons/vancouver-canucks.csv" to "Vancouver Canucks",
                "data/hockey/seasons/vegas-golden-knights.csv" to "Vegas Golden Knights",
                "data/hockey/seasons/washington-capitals.csv" to "Washington Capitals",
                "data/hockey/seasons/winnipeg-jets.csv" to "Winnipeg Jets",
            )

            franchiseSeasonData.forEach { (filename, franchiseName) ->
                populateFranchiseSeasons(filename, seasonIds, franchiseIds[franchiseName] ?: throw Exception("No franchise ID found for $franchiseName") , metroIds, leagueIds)
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