package mabersold.dao

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import kotlinx.coroutines.Dispatchers
import mabersold.models.db.Chapters
import mabersold.models.db.FranchiseSeasons
import mabersold.models.db.Franchises
import mabersold.models.db.League
import mabersold.models.db.Leagues
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
            val tables = listOf(Chapters, FranchiseSeasons, Seasons, Franchises, Metros, Leagues)

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
                    sport = row[Leagues.sport],
                    label = row[Leagues.label]
                )
            }

            // Populate franchises
            populate("data/mlb/mlb-franchises.csv", leagues, ::insertFranchise)
            populate("data/nba/nba-franchises.csv", leagues, ::insertFranchise)
            populate("data/nhl/nhl-franchises.csv", leagues, ::insertFranchise)
            populate("data/nfl/nfl-franchises.csv", leagues, ::insertFranchise)
            populate("data/wnba/wnba-franchises.csv", leagues, ::insertFranchise)
            populate("data/mls/mls-franchises.csv", leagues, ::insertFranchise)

            val franchiseIds: Map<String, Int> = Franchises.selectAll().associate { row ->
                row[Franchises.name] to row[Franchises.id].value
            }

            // Populate seasons
            populate("data/mlb/mlb-seasons.csv", leagues, ::insertSeason)
            populate("data/nba/nba-seasons.csv", leagues, ::insertSeason)
            populate("data/nhl/nhl-seasons.csv", leagues, ::insertSeason)
            populate("data/nfl/nfl-seasons.csv", leagues, ::insertSeason)
            populate("data/wnba/wnba-seasons.csv", leagues, ::insertSeason)
            populate("data/mls/mls-seasons.csv", leagues, ::insertSeason)

            val seasonIds: Map<Pair<Int, Int>, Int> = Seasons.selectAll().associate { row ->
                Pair(row[Seasons.leagueId].value, row[Seasons.startYear]) to row[Seasons.id].value
            }

            // Populate franchise seasons and postseasons
            val franchiseSeasonData = mapOf(
                "data/mlb/seasons/arizona-diamondbacks.csv" to "Arizona Diamondbacks",
                "data/mlb/seasons/atlanta-braves.csv" to "Atlanta Braves",
                "data/mlb/seasons/baltimore-orioles.csv" to "Baltimore Orioles",
                "data/mlb/seasons/boston-red-sox.csv" to "Boston Red Sox",
                "data/mlb/seasons/chicago-cubs.csv" to "Chicago Cubs",
                "data/mlb/seasons/chicago-white-sox.csv" to "Chicago White Sox",
                "data/mlb/seasons/cincinnati-reds.csv" to "Cincinnati Reds",
                "data/mlb/seasons/cleveland-guardians.csv" to "Cleveland Guardians",
                "data/mlb/seasons/colorado-rockies.csv" to "Colorado Rockies",
                "data/mlb/seasons/detroit-tigers.csv" to "Detroit Tigers",
                "data/mlb/seasons/houston-astros.csv" to "Houston Astros",
                "data/mlb/seasons/kansas-city-royals.csv" to "Kansas City Royals",
                "data/mlb/seasons/los-angeles-angels.csv" to "Los Angeles Angels",
                "data/mlb/seasons/los-angeles-dodgers.csv" to "Los Angeles Dodgers",
                "data/mlb/seasons/miami-marlins.csv" to "Miami Marlins",
                "data/mlb/seasons/milwaukee-brewers.csv" to "Milwaukee Brewers",
                "data/mlb/seasons/minnesota-twins.csv" to "Minnesota Twins",
                "data/mlb/seasons/new-york-mets.csv" to "New York Mets",
                "data/mlb/seasons/new-york-yankees.csv" to "New York Yankees",
                "data/mlb/seasons/oakland-athletics.csv" to "Oakland Athletics",
                "data/mlb/seasons/philadelphia-phillies.csv" to "Philadelphia Phillies",
                "data/mlb/seasons/pittsburgh-pirates.csv" to "Pittsburgh Pirates",
                "data/mlb/seasons/san-diego-padres.csv" to "San Diego Padres",
                "data/mlb/seasons/san-francisco-giants.csv" to "San Francisco Giants",
                "data/mlb/seasons/seattle-mariners.csv" to "Seattle Mariners",
                "data/mlb/seasons/st-louis-cardinals.csv" to "St. Louis Cardinals",
                "data/mlb/seasons/tampa-bay-rays.csv" to "Tampa Bay Rays",
                "data/mlb/seasons/texas-rangers.csv" to "Texas Rangers",
                "data/mlb/seasons/toronto-blue-jays.csv" to "Toronto Blue Jays",
                "data/mlb/seasons/washington-nationals.csv" to "Washington Nationals",
                "data/nba/seasons/anderson-packers.csv" to "Anderson Packers",
                "data/nba/seasons/atlanta-hawks.csv" to "Atlanta Hawks",
                "data/nba/seasons/baltimore-bullets.csv" to "Baltimore Bullets (original)",
                "data/nba/seasons/boston-celtics.csv" to "Boston Celtics",
                "data/nba/seasons/brooklyn-nets.csv" to "Brooklyn Nets",
                "data/nba/seasons/charlotte-hornets.csv" to "Charlotte Hornets",
                "data/nba/seasons/chicago-bulls.csv" to "Chicago Bulls",
                "data/nba/seasons/chicago-stags.csv" to "Chicago Stags",
                "data/nba/seasons/cleveland-cavaliers.csv" to "Cleveland Cavaliers",
                "data/nba/seasons/cleveland-rebels.csv" to "Cleveland Rebels",
                "data/nba/seasons/dallas-mavericks.csv" to "Dallas Mavericks",
                "data/nba/seasons/denver-nuggets.csv" to "Denver Nuggets",
                "data/nba/seasons/denver-nuggets-original.csv" to "Denver Nuggets (original)",
                "data/nba/seasons/detroit-pistons.csv" to "Detroit Pistons",
                "data/nba/seasons/golden-state-warriors.csv" to "Golden State Warriors",
                "data/nba/seasons/houston-rockets.csv" to "Houston Rockets",
                "data/nba/seasons/indiana-pacers.csv" to "Indiana Pacers",
                "data/nba/seasons/indianapolis-jets.csv" to "Indianapolis Jets",
                "data/nba/seasons/indianapolis-olympians.csv" to "Indianapolis Olympians",
                "data/nba/seasons/los-angeles-clippers.csv" to "Los Angeles Clippers",
                "data/nba/seasons/los-angeles-lakers.csv" to "Los Angeles Lakers",
                "data/nba/seasons/memphis-grizzlies.csv" to "Memphis Grizzlies",
                "data/nba/seasons/miami-heat.csv" to "Miami Heat",
                "data/nba/seasons/milwaukee-bucks.csv" to "Milwaukee Bucks",
                "data/nba/seasons/minnesota-timberwolves.csv" to "Minnesota Timberwolves",
                "data/nba/seasons/new-orleans-pelicans.csv" to "New Orleans Pelicans",
                "data/nba/seasons/new-york-knicks.csv" to "New York Knicks",
                "data/nba/seasons/oklahoma-city-thunder.csv" to "Oklahoma City Thunder",
                "data/nba/seasons/orlando-magic.csv" to "Orlando Magic",
                "data/nba/seasons/philadelphia-76ers.csv" to "Philadelphia 76ers",
                "data/nba/seasons/phoenix-suns.csv" to "Phoenix Suns",
                "data/nba/seasons/pittsburgh-ironmen.csv" to "Pittsburgh Ironmen",
                "data/nba/seasons/portland-trail-blazers.csv" to "Portland Trail Blazers",
                "data/nba/seasons/providence-steamrollers.csv" to "Providence Steamrollers",
                "data/nba/seasons/sacramento-kings.csv" to "Sacramento Kings",
                "data/nba/seasons/san-antonio-spurs.csv" to "San Antonio Spurs",
                "data/nba/seasons/sheboygan-red-skins.csv" to "Sheboygan Red Skins",
                "data/nba/seasons/st-louis-bombers.csv" to "St. Louis Bombers",
                "data/nba/seasons/toronto-huskies.csv" to "Toronto Huskies",
                "data/nba/seasons/toronto-raptors.csv" to "Toronto Raptors",
                "data/nba/seasons/utah-jazz.csv" to "Utah Jazz",
                "data/nba/seasons/washington-capitols.csv" to "Washington Capitols",
                "data/nba/seasons/washington-wizards.csv" to "Washington Wizards",
                "data/nfl/seasons/arizona-cardinals.csv" to "Arizona Cardinals",
                "data/nfl/seasons/atlanta-falcons.csv" to "Atlanta Falcons",
                "data/nfl/seasons/baltimore-colts.csv" to "Baltimore Colts",
                "data/nfl/seasons/baltimore-ravens.csv" to "Baltimore Ravens",
                "data/nfl/seasons/buffalo-bills.csv" to "Buffalo Bills",
                "data/nfl/seasons/carolina-panthers.csv" to "Carolina Panthers",
                "data/nfl/seasons/chicago-bears.csv" to "Chicago Bears",
                "data/nfl/seasons/cincinnati-bengals.csv" to "Cincinnati Bengals",
                "data/nfl/seasons/cleveland-browns.csv" to "Cleveland Browns",
                "data/nfl/seasons/dallas-cowboys.csv" to "Dallas Cowboys",
                "data/nfl/seasons/denver-broncos.csv" to "Denver Broncos",
                "data/nfl/seasons/detroit-lions.csv" to "Detroit Lions",
                "data/nfl/seasons/green-bay-packers.csv" to "Green Bay Packers",
                "data/nfl/seasons/houston-texans.csv" to "Houston Texans",
                "data/nfl/seasons/indianapolis-colts.csv" to "Indianapolis Colts",
                "data/nfl/seasons/jacksonville-jaguars.csv" to "Jacksonville Jaguars",
                "data/nfl/seasons/kansas-city-chiefs.csv" to "Kansas City Chiefs",
                "data/nfl/seasons/las-vegas-raiders.csv" to "Las Vegas Raiders",
                "data/nfl/seasons/los-angeles-chargers.csv" to "Los Angeles Chargers",
                "data/nfl/seasons/los-angeles-rams.csv" to "Los Angeles Rams",
                "data/nfl/seasons/miami-dolphins.csv" to "Miami Dolphins",
                "data/nfl/seasons/minnesota-vikings.csv" to "Minnesota Vikings",
                "data/nfl/seasons/new-england-patriots.csv" to "New England Patriots",
                "data/nfl/seasons/new-orleans-saints.csv" to "New Orleans Saints",
                "data/nfl/seasons/new-york-giants.csv" to "New York Giants",
                "data/nfl/seasons/new-york-jets.csv" to "New York Jets",
                "data/nfl/seasons/philadelphia-eagles.csv" to "Philadelphia Eagles",
                "data/nfl/seasons/pittsburgh-steelers.csv" to "Pittsburgh Steelers",
                "data/nfl/seasons/san-francisco-49ers.csv" to "San Francisco 49ers",
                "data/nfl/seasons/seattle-seahawks.csv" to "Seattle Seahawks",
                "data/nfl/seasons/tampa-bay-buccaneers.csv" to "Tampa Bay Buccaneers",
                "data/nfl/seasons/tennessee-titans.csv" to "Tennessee Titans",
                "data/nfl/seasons/washington-commanders.csv" to "Washington Commanders",
                "data/nfl/seasons/boston-yanks.csv" to "Boston Yanks",
                "data/nfl/seasons/brooklyn-dodgers.csv" to "Brooklyn Dodgers",
                "data/nfl/seasons/cincinnati-reds-nfl.csv" to "Cincinnati Reds (NFL)",
                "data/nhl/seasons/anaheim-ducks.csv" to "Anaheim Ducks",
                "data/nhl/seasons/arizona-coyotes.csv" to "Arizona Coyotes",
                "data/nhl/seasons/boston-bruins.csv" to "Boston Bruins",
                "data/nhl/seasons/buffalo-sabres.csv" to "Buffalo Sabres",
                "data/nhl/seasons/calgary-flames.csv" to "Calgary Flames",
                "data/nhl/seasons/california-golden-seals.csv" to "California Golden Seals",
                "data/nhl/seasons/carolina-hurricanes.csv" to "Carolina Hurricanes",
                "data/nhl/seasons/chicago-blackhawks.csv" to "Chicago Blackhawks",
                "data/nhl/seasons/colorado-avalanche.csv" to "Colorado Avalanche",
                "data/nhl/seasons/columbus-blue-jackets.csv" to "Columbus Blue Jackets",
                "data/nhl/seasons/dallas-stars.csv" to "Dallas Stars",
                "data/nhl/seasons/detroit-red-wings.csv" to "Detroit Red Wings",
                "data/nhl/seasons/edmonton-oilers.csv" to "Edmonton Oilers",
                "data/nhl/seasons/florida-panthers.csv" to "Florida Panthers",
                "data/nhl/seasons/los-angeles-kings.csv" to "Los Angeles Kings",
                "data/nhl/seasons/minnesota-wild.csv" to "Minnesota Wild",
                "data/nhl/seasons/montreal-canadiens.csv" to "Montreal Canadiens",
                "data/nhl/seasons/montreal-maroons.csv" to "Montreal Maroons",
                "data/nhl/seasons/nashville-predators.csv" to "Nashville Predators",
                "data/nhl/seasons/new-jersey-devils.csv" to "New Jersey Devils",
                "data/nhl/seasons/new-york-americans.csv" to "New York Americans",
                "data/nhl/seasons/new-york-islanders.csv" to "New York Islanders",
                "data/nhl/seasons/new-york-rangers.csv" to "New York Rangers",
                "data/nhl/seasons/ottawa-senators.csv" to "Ottawa Senators",
                "data/nhl/seasons/ottawa-senators-original.csv" to "Ottawa Senators (original)",
                "data/nhl/seasons/philadelphia-flyers.csv" to "Philadelphia Flyers",
                "data/nhl/seasons/pittsburgh-penguins.csv" to "Pittsburgh Penguins",
                "data/nhl/seasons/pittsburgh-pirates-nhl.csv" to "Pittsburgh Pirates (NHL)",
                "data/nhl/seasons/san-jose-sharks.csv" to "San Jose Sharks",
                "data/nhl/seasons/seattle-kraken.csv" to "Seattle Kraken",
                "data/nhl/seasons/st-louis-blues.csv" to "St. Louis Blues",
                "data/nhl/seasons/tampa-bay-lightning.csv" to "Tampa Bay Lightning",
                "data/nhl/seasons/toronto-maple-leafs.csv" to "Toronto Maple Leafs",
                "data/nhl/seasons/vancouver-canucks.csv" to "Vancouver Canucks",
                "data/nhl/seasons/vegas-golden-knights.csv" to "Vegas Golden Knights",
                "data/nhl/seasons/washington-capitals.csv" to "Washington Capitals",
                "data/nhl/seasons/winnipeg-jets.csv" to "Winnipeg Jets",
                "data/wnba/franchises/atlanta-dream.csv" to "Atlanta Dream",
                "data/wnba/franchises/charlotte-sting.csv" to "Charlotte Sting",
                "data/wnba/franchises/chicago-sky.csv" to "Chicago Sky",
                "data/wnba/franchises/cleveland-rockers.csv" to "Cleveland Rockers",
                "data/wnba/franchises/connecticut-sun.csv" to "Connecticut Sun",
                "data/wnba/franchises/dallas-wings.csv" to "Dallas Wings",
                "data/wnba/franchises/houston-comets.csv" to "Houston Comets",
                "data/wnba/franchises/indiana-fever.csv" to "Indiana Fever",
                "data/wnba/franchises/las-vegas-aces.csv" to "Las Vegas Aces",
                "data/wnba/franchises/los-angeles-sparks.csv" to "Los Angeles Sparks",
                "data/wnba/franchises/miami-sol.csv" to "Miami Sol",
                "data/wnba/franchises/minnesota-lynx.csv" to "Minnesota Lynx",
                "data/wnba/franchises/new-york-liberty.csv" to "New York Liberty",
                "data/wnba/franchises/phoenix-mercury.csv" to "Phoenix Mercury",
                "data/wnba/franchises/portland-fire.csv" to "Portland Fire",
                "data/wnba/franchises/sacramento-monarchs.csv" to "Sacramento Monarchs",
                "data/wnba/franchises/seattle-storm.csv" to "Seattle Storm",
                "data/wnba/franchises/washington-mystics.csv" to "Washington Mystics",
                "data/mls/franchises/atlanta-united.csv" to "Atlanta United",
                "data/mls/franchises/austin-fc.csv" to "Austin FC",
                "data/mls/franchises/cf-montreal.csv" to "CF Montreal",
                "data/mls/franchises/charlotte-fc.csv" to "Charlotte FC",
                "data/mls/franchises/chicago-fire.csv" to "Chicago Fire",
                "data/mls/franchises/chivas-usa.csv" to "Chivas USA",
                "data/mls/franchises/colorado-rapids.csv" to "Colorado Rapids",
                "data/mls/franchises/columbus-crew.csv" to "Columbus Crew",
                "data/mls/franchises/dc-united.csv" to "DC United",
                "data/mls/franchises/fc-cincinnati.csv" to "FC Cincinnati",
                "data/mls/franchises/fc-dallas.csv" to "FC Dallas",
                "data/mls/franchises/houston-dynamo.csv" to "Houston Dynamo",
                "data/mls/franchises/inter-miami.csv" to "Inter Miami CF",
                "data/mls/franchises/la-galaxy.csv" to "LA Galaxy",
                "data/mls/franchises/lafc.csv" to "Los Angeles FC",
                "data/mls/franchises/miami-fusion.csv" to "Miami Fusion",
                "data/mls/franchises/minnesota-united.csv" to "Minnesota United",
                "data/mls/franchises/nashville-sc.csv" to "Nashville SC",
                "data/mls/franchises/new-england-revolution.csv" to "New England Revolution",
                "data/mls/franchises/nycfc.csv" to "New York City FC",
                "data/mls/franchises/orlando-city.csv" to "Orlando City SC",
                "data/mls/franchises/philadelphia-union.csv" to "Philadelphia Union",
                "data/mls/franchises/portland-timbers.csv" to "Portland Timbers",
                "data/mls/franchises/real-salt-lake.csv" to "Real Salt Lake",
                "data/mls/franchises/red-bull-new-york.csv" to "Red Bull New York",
                "data/mls/franchises/san-jose-earthquakes.csv" to "San Jose Earthquakes",
                "data/mls/franchises/seattle-sounders.csv" to "Seattle Sounders",
                "data/mls/franchises/sporting-kc.csv" to "Sporting KC",
                "data/mls/franchises/st-louis-city.csv" to "St. Louis City SC",
                "data/mls/franchises/tampa-bay-mutiny.csv" to "Tampa Bay Mutiny",
                "data/mls/franchises/toronto-fc.csv" to "Toronto FC",
                "data/mls/franchises/vancouver-whitecaps.csv" to "Vancouver Whitecaps"
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

    private fun insertFranchise(csvRow: Map<String, String>, leagues: List<League>) = Franchises.insert {
        it[name] = csvRow["name"]!!
        it[isDefunct] = csvRow["is_defunct"]!!.toBoolean()
        it[leagueId] = leagues.first { league -> league.name == csvRow["league"]!! }.id
        it[label] = csvRow["name"]!!.lowercase().replace(' ', '-').replace(".", "")
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