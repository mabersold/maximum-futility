package mabersold.services

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mabersold.dao.FranchiseSeasonDAO
import mabersold.dao.LeagueDAO
import mabersold.dao.SeasonDAO
import mabersold.models.db.FranchiseSeason
import mabersold.models.db.League
import mabersold.models.db.Season
import mabersold.models.db.Standing
import mabersold.models.db.Standing.FIRST
import mabersold.models.db.Standing.FIRST_TIED
import mabersold.models.db.Standing.LAST
import mabersold.models.db.Standing.LAST_TIED
import mabersold.services.SeasonDataService.Companion.WARN_CONFERENCES_DO_NOT_MATCH
import mabersold.services.SeasonDataService.Companion.WARN_DIVISIONS_DO_NOT_MATCH
import mabersold.services.SeasonDataService.Companion.WARN_MULTIPLE_FIRST_IN_CONFERENCE
import mabersold.services.SeasonDataService.Companion.WARN_MULTIPLE_FIRST_IN_DIVISION
import mabersold.services.SeasonDataService.Companion.WARN_MULTIPLE_FIRST_IN_LEAGUE
import mabersold.services.SeasonDataService.Companion.WARN_MULTIPLE_LAST_IN_CONFERENCE
import mabersold.services.SeasonDataService.Companion.WARN_MULTIPLE_LAST_IN_DIVISION
import mabersold.services.SeasonDataService.Companion.WARN_MULTIPLE_LAST_IN_LEAGUE
import mabersold.services.SeasonDataService.Companion.WARN_NOT_ENOUGH_BEST_IN_CONFERENCE
import mabersold.services.SeasonDataService.Companion.WARN_NOT_ENOUGH_BEST_IN_DIVISION
import mabersold.services.SeasonDataService.Companion.WARN_NOT_ENOUGH_WORST_IN_CONFERENCE
import mabersold.services.SeasonDataService.Companion.WARN_NOT_ENOUGH_WORST_IN_DIVISION
import mabersold.services.SeasonDataService.Companion.WARN_NO_BEST_IN_LEAGUE
import mabersold.services.SeasonDataService.Companion.WARN_NO_WORST_IN_LEAGUE
import mabersold.services.SeasonDataService.Companion.WARN_SHOULD_NOT_HAVE_POSTSEASON_DATA
import mabersold.services.SeasonDataService.Companion.WARN_TOO_MANY_CHAMPIONS
import mabersold.services.SeasonDataService.Companion.WARN_TOO_MANY_ROUNDS_WON
import mabersold.services.SeasonDataService.Companion.WARN_TOO_MANY_TEAMS_IN_CHAMPIONSHIP
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SeasonDataServiceTest {
    private val seasonDao = mockk<SeasonDAO>()
    private val franchiseSeasonDAO = mockk<FranchiseSeasonDAO>()
    private val leagueDao = mockk<LeagueDAO>()
    private val seasonDataService = SeasonDataService(seasonDao, franchiseSeasonDAO, leagueDao)

    companion object {
        private const val SEASON_ID = 1
        private const val LEAGUE_ID = 1
        private const val SEASON_NAME = "1995 Major League Baseball Season"
        private const val YEAR = 1995
        private const val MAJOR_DIVISIONS = 2
        private const val MINOR_DIVISIONS = 6
        private const val POSTSEASON_ROUNDS = 3
        private const val NL = "NL"
        private const val AL = "AL"
        private const val NL_WEST = "NL West"
        private const val NL_CENTRAL = "NL Central"
        private const val NL_EAST = "NL East"
        private const val AL_WEST = "AL West"
        private const val AL_CENTRAL = "AL Central"
        private const val AL_EAST = "AL East"
    }

    @Test
    fun `gets a season summary with no warnings`() = runTest {
        // Arrange
        coEvery { seasonDao.get(any()) } returns createSeason()
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns createBaseballSeason()
        coEvery { leagueDao.get(any()) } returns createLeague()

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(SEASON_NAME, summary.name)
        assertEquals(LEAGUE_ID, summary.league.id)
        assertEquals(MAJOR_DIVISIONS, summary.totalMajorDivisions)
        assertEquals(MINOR_DIVISIONS, summary.totalMinorDivisions)
        assertEquals(listOf(AL, NL), summary.structure.groups.map { it.name })
        assertEquals(listOf(AL_CENTRAL, AL_EAST, AL_WEST, NL_CENTRAL, NL_EAST, NL_WEST), summary.structure.groups.flatMap { it.groups }.map { it.name })
        assertEquals(0, summary.warnings.size)
        assertEquals(listOf("Atlanta Braves", "Cleveland Indians", "Seattle Mariners", "New York Yankees", "Colorado Rockies", "Cincinnati Reds", "Los Angeles Dodgers", "Boston Red Sox").sorted(), summary.teamsInPostseason)
        assertEquals(listOf("Atlanta Braves", "Cleveland Indians", "Seattle Mariners", "Cincinnati Reds").sorted(), summary.teamsAdvancedInPostseason)
        assertEquals(listOf("Atlanta Braves", "Cleveland Indians").sorted(), summary.teamsInChampionship)
        assertEquals("Atlanta Braves", summary.champion)
        assertEquals(listOf("Cleveland Indians"), summary.structure.finishedFirst)
        assertEquals(listOf("Minnesota Twins", "Toronto Blue Jays"), summary.structure.finishedLast)

        val alResults = summary.structure.groups.first { it.name == AL }
        assertEquals(listOf("Cleveland Indians"), alResults.finishedFirst)
        assertEquals(listOf("Minnesota Twins", "Toronto Blue Jays"), alResults.finishedLast)

        val nlResults = summary.structure.groups.first { it.name == NL }
        assertEquals(listOf("Atlanta Braves"), nlResults.finishedFirst)
        assertEquals(listOf("Pittsburgh Pirates"), nlResults.finishedLast)

        val alWestResults = alResults.groups.first { it.name == AL_WEST }
        assertEquals(listOf("Seattle Mariners"), alWestResults.finishedFirst)
        assertEquals(listOf("Oakland Athletics"), alWestResults.finishedLast)

        val alCentralResults = alResults.groups.first { it.name == AL_CENTRAL }
        assertEquals(listOf("Cleveland Indians"), alCentralResults.finishedFirst)
        assertEquals(listOf("Minnesota Twins"), alCentralResults.finishedLast)

        val alEastResults = alResults.groups.first { it.name == AL_EAST }
        assertEquals(listOf("Boston Red Sox"), alEastResults.finishedFirst)
        assertEquals(listOf("Toronto Blue Jays"), alEastResults.finishedLast)

        val nlWestResults = nlResults.groups.first { it.name == NL_WEST }
        assertEquals(listOf("Los Angeles Dodgers"), nlWestResults.finishedFirst)
        assertEquals(listOf("San Francisco Giants"), nlWestResults.finishedLast)

        val nlCentralResults = nlResults.groups.first { it.name == NL_CENTRAL }
        assertEquals(listOf("Cincinnati Reds"), nlCentralResults.finishedFirst)
        assertEquals(listOf("Pittsburgh Pirates"), nlCentralResults.finishedLast)

        val nlEastResults = nlResults.groups.first { it.name == NL_EAST }
        assertEquals(listOf("Atlanta Braves"), nlEastResults.finishedFirst)
        assertEquals(listOf("Montreal Expos"), nlEastResults.finishedLast)
    }

    @Test
    fun `produces warning when there are too many best in league`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3"),
            createFranchiseSeason("Team 4", leaguePosition = LAST),
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_MULTIPLE_FIRST_IN_LEAGUE, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when one team is FIRST_TIED for league position and another is FIRST`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", leaguePosition = FIRST_TIED, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", leaguePosition = LAST),
            createFranchiseSeason("Team 4")
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_MULTIPLE_FIRST_IN_LEAGUE, summary.warnings.first().message)
    }

    @Test
    fun `does not produce warning when multiple tied for best in league`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST_TIED, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", leaguePosition = FIRST_TIED, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", leaguePosition = LAST),
            createFranchiseSeason("Team 4")
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(0, summary.warnings.size)
    }

    @Test
    fun `produces warning when there are too many worst in league`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", leaguePosition = LAST),
            createFranchiseSeason("Team 4", leaguePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_MULTIPLE_LAST_IN_LEAGUE, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when one team is LAST_TIED for league position and another is LAST`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", leaguePosition = LAST_TIED),
            createFranchiseSeason("Team 4", leaguePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_MULTIPLE_LAST_IN_LEAGUE, summary.warnings.first().message)
    }

    @Test
    fun `does not produce warning when multiple tied for worst in league`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", leaguePosition = LAST_TIED),
            createFranchiseSeason("Team 4", leaguePosition = LAST_TIED)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(0, summary.warnings.size)
    }

    @Test
    fun `produces warning when number of conferences does not match`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(2, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", conference = "Conference 1", leaguePosition = FIRST, conferencePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", conference = "Conference 1", leaguePosition = LAST, conferencePosition = LAST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", conference = "Conference 2", conferencePosition = FIRST),
            createFranchiseSeason("Team 4", conference = "Conference 3", conferencePosition = LAST),
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_CONFERENCES_DO_NOT_MATCH, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when number of divisions does not match`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 2)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", division = "Division 1", leaguePosition = FIRST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", division = "Division 1", leaguePosition = LAST, divisionPosition = LAST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", division = "Division 2", divisionPosition = FIRST),
            createFranchiseSeason("Team 4", division = "Division 3", divisionPosition = LAST),
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_DIVISIONS_DO_NOT_MATCH, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are too many champions`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 3", leaguePosition = LAST),
            createFranchiseSeason("Team 4")
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_TOO_MANY_CHAMPIONS, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are too many teams in the championship`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", qualifiedForPostseason = true, roundsWon = 1, appearedInChampionship = true),
            createFranchiseSeason("Team 4", leaguePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_TOO_MANY_TEAMS_IN_CHAMPIONSHIP, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there is no best overall in league`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3"),
            createFranchiseSeason("Team 4", leaguePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_NO_BEST_IN_LEAGUE, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there is no worst overall in league`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3"),
            createFranchiseSeason("Team 4")
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_NO_WORST_IN_LEAGUE, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are not enough best in conference`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(2, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", conference = "Conference 1", leaguePosition = FIRST, conferencePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", conference = "Conference 1", conferencePosition = LAST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", conference = "Conference 2"),
            createFranchiseSeason("Team 4", conference = "Conference 2", leaguePosition = LAST, conferencePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_NOT_ENOUGH_BEST_IN_CONFERENCE, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are not enough worst in conference`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(2, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", conference = "Conference 1", leaguePosition = FIRST, conferencePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", conference = "Conference 1", conferencePosition = LAST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", conference = "Conference 2", conferencePosition = FIRST),
            createFranchiseSeason("Team 4", conference = "Conference 2", leaguePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_NOT_ENOUGH_WORST_IN_CONFERENCE, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are not enough best in division`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 2)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", division = "Division 1", leaguePosition = FIRST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", division = "Division 1", divisionPosition = LAST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", division = "Division 2"),
            createFranchiseSeason("Team 4", division = "Division 2", leaguePosition = LAST, divisionPosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_NOT_ENOUGH_BEST_IN_DIVISION, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are not enough worst in division`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 2)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", division = "Division 1", leaguePosition = FIRST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", division = "Division 1", divisionPosition = LAST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", division = "Division 2", divisionPosition = FIRST),
            createFranchiseSeason("Team 4", division = "Division 2", leaguePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_NOT_ENOUGH_WORST_IN_DIVISION, summary.warnings.first().message)
    }

    @Test
    fun `produces warning when a team wins more postseason rounds than are possible`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 4, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3"),
            createFranchiseSeason("Team 4", leaguePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_TOO_MANY_ROUNDS_WON.format("Team 1"), summary.warnings.first().message)
    }

    @Test
    fun `produces warning when multiple teams win more postseason rounds than are possible`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 4, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 4, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3"),
            createFranchiseSeason("Team 4", leaguePosition = LAST)
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_TOO_MANY_ROUNDS_WON.format("Team 1, Team 2"), summary.warnings.first().message)
    }

    @Test
    fun `produces warning when team has not qualified for playoffs but has playoff metrics`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 0)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", leaguePosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 3", roundsWon = 1),
            createFranchiseSeason("Team 4", leaguePosition = LAST, roundsWon = 0, appearedInChampionship = true),
            createFranchiseSeason("Team 5", roundsWon = 0, wonChampionship = true),
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertTrue(summary.warnings.any { it.message ==  WARN_SHOULD_NOT_HAVE_POSTSEASON_DATA.format("Team 3, Team 4, Team 5")})
    }

    @Test
    fun `produces warning when there are too many best in conference`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(3, 0, 1)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", "Conference 1", leaguePosition = FIRST, conferencePosition = FIRST, qualifiedForPostseason = true, roundsWon = 1, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", "Conference 1", conferencePosition = FIRST),
            createFranchiseSeason("Team 3", "Conference 1", conferencePosition = LAST),
            createFranchiseSeason("Team 4", "Conference 2", conferencePosition = FIRST, qualifiedForPostseason = true, roundsWon = 0, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 5", "Conference 2", conferencePosition = FIRST_TIED),
            createFranchiseSeason("Team 6", "Conference 2", leaguePosition = LAST, conferencePosition = LAST),
            createFranchiseSeason("Team 7", "Conference 3", conferencePosition = FIRST),
            createFranchiseSeason("Team 8", "Conference 3"),
            createFranchiseSeason("Team 9", "Conference 3", conferencePosition = LAST),
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_MULTIPLE_FIRST_IN_CONFERENCE.format("Conference 1, Conference 2"), summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are too many last in conference`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(3, 0, 1)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", "Conference 1", leaguePosition = FIRST, conferencePosition = FIRST, qualifiedForPostseason = true, roundsWon = 1, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", "Conference 1", conferencePosition = LAST),
            createFranchiseSeason("Team 3", "Conference 1", conferencePosition = LAST),
            createFranchiseSeason("Team 4", "Conference 2", conferencePosition = FIRST, qualifiedForPostseason = true, roundsWon = 0, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 5", "Conference 2", conferencePosition = LAST_TIED),
            createFranchiseSeason("Team 6", "Conference 2", leaguePosition = LAST, conferencePosition = LAST),
            createFranchiseSeason("Team 7", "Conference 3", conferencePosition = FIRST),
            createFranchiseSeason("Team 8", "Conference 3"),
            createFranchiseSeason("Team 9", "Conference 3", conferencePosition = LAST),
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_MULTIPLE_LAST_IN_CONFERENCE.format("Conference 1, Conference 2"), summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are too many best in division`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 3, 1)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", division = "Division 1", leaguePosition = FIRST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 1, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", division = "Division 1", divisionPosition = FIRST),
            createFranchiseSeason("Team 3", division = "Division 1", divisionPosition = LAST),
            createFranchiseSeason("Team 4", division = "Division 2", divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 0, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 5", division = "Division 2", divisionPosition = FIRST_TIED),
            createFranchiseSeason("Team 6", division = "Division 2", leaguePosition = LAST, divisionPosition = LAST),
            createFranchiseSeason("Team 7", division = "Division 3", divisionPosition = FIRST),
            createFranchiseSeason("Team 8", division = "Division 3"),
            createFranchiseSeason("Team 9", division = "Division 3", divisionPosition = LAST),
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_MULTIPLE_FIRST_IN_DIVISION.format("Division 1, Division 2"), summary.warnings.first().message)
    }

    @Test
    fun `produces warning when there are too many last in division`() = runTest {
        // Arrange
        coEvery { leagueDao.get(any()) } returns createLeague()
        coEvery { seasonDao.get(any()) } returns createSeason(0, 3, 1)
        coEvery { franchiseSeasonDAO.getBySeason(any()) } returns listOf(
            createFranchiseSeason("Team 1", division = "Division 1", leaguePosition = FIRST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 1, appearedInChampionship = true, wonChampionship = true),
            createFranchiseSeason("Team 2", division = "Division 1", divisionPosition = LAST),
            createFranchiseSeason("Team 3", division = "Division 1", divisionPosition = LAST),
            createFranchiseSeason("Team 4", division = "Division 2", divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 0, appearedInChampionship = true, wonChampionship = false),
            createFranchiseSeason("Team 5", division = "Division 2", divisionPosition = LAST_TIED),
            createFranchiseSeason("Team 6", division = "Division 2", leaguePosition = LAST, divisionPosition = LAST),
            createFranchiseSeason("Team 7", division = "Division 3", divisionPosition = FIRST),
            createFranchiseSeason("Team 8", division = "Division 3"),
            createFranchiseSeason("Team 9", division = "Division 3", divisionPosition = LAST),
        )

        // Act
        val summary = seasonDataService.getSeasonReport(SEASON_ID)

        // Assert
        assertEquals(1, summary.warnings.size)
        assertEquals(WARN_MULTIPLE_LAST_IN_DIVISION.format("Division 1, Division 2"), summary.warnings.first().message)
    }

    private fun createLeague() = League(LEAGUE_ID, "MLB", "mlb", "Baseball")

    private fun createSeason(majorDivisions: Int = MAJOR_DIVISIONS, minorDivisions: Int = MINOR_DIVISIONS, postSeasonRounds: Int = POSTSEASON_ROUNDS) = Season(
        SEASON_ID,
        SEASON_NAME,
        YEAR,
        YEAR,
        LEAGUE_ID,
        majorDivisions,
        minorDivisions,
        postSeasonRounds
    )

    private fun createFranchiseSeason(
        teamName: String,
        conference: String? = null,
        division: String? = null,
        leaguePosition: Standing? = null,
        conferencePosition: Standing? = null,
        divisionPosition: Standing? = null,
        qualifiedForPostseason: Boolean = false,
        roundsWon: Int? = null,
        appearedInChampionship: Boolean = false,
        wonChampionship: Boolean = false
    ) = FranchiseSeason(
        1,
        SEASON_ID,
        1,
        1,
        teamName,
        LEAGUE_ID,
        conference,
        division,
        leaguePosition,
        conferencePosition,
        divisionPosition,
        qualifiedForPostseason,
        roundsWon,
        appearedInChampionship,
        wonChampionship
    )

    private fun createBaseballSeason(): List<FranchiseSeason> = listOf(
        createFranchiseSeason("Atlanta Braves", NL, NL_EAST, conferencePosition = FIRST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 3, appearedInChampionship = true, wonChampionship = true),
        createFranchiseSeason("Baltimore Orioles", AL, AL_EAST),
        createFranchiseSeason("Boston Red Sox", AL, AL_EAST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 0),
        createFranchiseSeason("California Angels", AL, AL_WEST),
        createFranchiseSeason("Chicago Cubs", NL, NL_CENTRAL),
        createFranchiseSeason("Chicago White Sox", AL, AL_CENTRAL),
        createFranchiseSeason("Cincinnati Reds", NL, NL_CENTRAL, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 1),
        createFranchiseSeason("Cleveland Indians", AL, AL_CENTRAL, FIRST, FIRST, FIRST, true, 2, appearedInChampionship = true, wonChampionship = false),
        createFranchiseSeason("Colorado Rockies", NL, NL_WEST, qualifiedForPostseason = true, roundsWon = 0),
        createFranchiseSeason("Detroit Tigers", AL, AL_CENTRAL),
        createFranchiseSeason("Florida Marlins", NL, NL_EAST),
        createFranchiseSeason("Houston Astros", NL, NL_CENTRAL),
        createFranchiseSeason("Kansas City Royals", AL, AL_CENTRAL),
        createFranchiseSeason("Los Angeles Dodgers", NL, NL_WEST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 0),
        createFranchiseSeason("Milwaukee Brewers", AL, AL_CENTRAL),
        createFranchiseSeason("Minnesota Twins", AL, AL_CENTRAL, LAST_TIED, LAST_TIED, LAST),
        createFranchiseSeason("Montreal Expos", NL, NL_EAST, divisionPosition = LAST),
        createFranchiseSeason("New York Mets", NL, NL_EAST),
        createFranchiseSeason("New York Yankees", AL, AL_EAST, qualifiedForPostseason = true, roundsWon = 0),
        createFranchiseSeason("Oakland Athletics", AL, AL_WEST, divisionPosition = LAST),
        createFranchiseSeason("Philadelphia Phillies", NL, NL_EAST),
        createFranchiseSeason("Pittsburgh Pirates", NL, NL_CENTRAL, conferencePosition = LAST, divisionPosition = LAST),
        createFranchiseSeason("San Diego Padres", NL, NL_WEST),
        createFranchiseSeason("San Francisco Giants", NL, NL_WEST, divisionPosition = LAST),
        createFranchiseSeason("Seattle Mariners", AL, AL_WEST, divisionPosition = FIRST, qualifiedForPostseason = true, roundsWon = 1),
        createFranchiseSeason("St. Louis Cardinals", NL, NL_CENTRAL),
        createFranchiseSeason("Texas Rangers", AL, AL_WEST),
        createFranchiseSeason("Toronto Blue Jays", AL, AL_EAST, LAST_TIED, LAST_TIED, LAST)
    )
}