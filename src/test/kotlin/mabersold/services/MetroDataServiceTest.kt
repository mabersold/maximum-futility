package mabersold.services

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mabersold.dao.FranchiseSeasonDAO
import mabersold.dao.MetroDAO
import mabersold.models.FranchiseSeasonInfo
import mabersold.models.api.MetricType
import mabersold.models.db.Standing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetroDataServiceTest {
    private val franchiseSeasonDAO = mockk<FranchiseSeasonDAO>()
    private val metroDAO = mockk<MetroDAO>()
    private val metroDataService = MetroDataService(franchiseSeasonDAO, metroDAO)

    @Test
    fun `gets correct data for metros winning the championship`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 2, true, true, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 2, true, true, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 1, true, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 2, true, true, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 11, 1990, 1990),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 12, 1991, 1991),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 2, true, false, 2, 6, 2, 13, 1992, 1992),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 2, true, true, 2, 6, 2, 14, 1993, 1993),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 15, 1994, 1994),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 16, 1995, 1995),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 17, 1996, 1996),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 18, 1997, 1997),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 19, 1998, 1998),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 20, 1999, 1999),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 2, true, true, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 10, 1999, 1999),
        )

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.TOTAL_CHAMPIONSHIPS)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.TOTAL_CHAMPIONSHIPS })
        assertEquals(25, data.first { it.name == "New York" }.opportunities)
        assertEquals(4, data.first { it.name == "New York" }.total)
        assertEquals(1999, data.first { it.name == "New York" }.lastActiveYear)
        assertEquals(9, data.first { it.name == "Minneapolis" }.opportunities)
        assertEquals(1, data.first { it.name == "Minneapolis" }.total)
        assertEquals(1999, data.first { it.name == "Minneapolis" }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros appearing in championship`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees"),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", postSeasonRounds = 1, appearedInChampionship = true, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, "New York", "New York Knicks", postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, "New York", "New York Knicks", postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Phillies", postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Flyers", postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.CHAMPIONSHIP_APPEARANCES)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.CHAMPIONSHIP_APPEARANCES })
        assertEquals(21, data.first { it.name == "New York" }.opportunities)
        assertEquals(11, data.first { it.name == "New York" }.total)
        assertEquals(1921, data.first { it.name == "New York" }.lastActiveYear)
        assertEquals(6, data.first { it.name == "Philadelphia" }.opportunities)
        assertEquals(3, data.first { it.name == "Philadelphia" }.total)
        assertEquals(1906, data.first { it.name == "Philadelphia" }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros advancing in the postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees"),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", postSeasonRounds = 1, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", postSeasonRounds = 3, postSeasonRoundsWon = 1, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, "New York", "New York Knicks", postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, "New York", "New York Knicks", postSeasonRounds = 3, postSeasonRoundsWon = 0, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Phillies", postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Flyers", postSeasonRounds = 3, postSeasonRoundsWon = 0, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.ADVANCED_IN_PLAYOFFS)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.ADVANCED_IN_PLAYOFFS })
        assertEquals(11, data.first { it.name == "New York" }.opportunities)
        assertEquals(6, data.first { it.name == "Philadelphia" }.opportunities)
        assertEquals(5, data.first { it.name == "New York" }.total)
        assertEquals(3, data.first { it.name == "Philadelphia" }.total)
        assertEquals(1921, data.first { it.name == "New York" }.lastActiveYear)
        assertEquals(1906, data.first { it.name == "Philadelphia" }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros qualifying for postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees"),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", postSeasonRounds = 1, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", postSeasonRounds = 3, qualifiedForPostseason = true, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, "New York", "New York Knicks", postSeasonRounds = 3, qualifiedForPostseason = true),
            FranchiseSeasonInfoParams(3, "New York", "New York Knicks", postSeasonRounds = 3, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Phillies", postSeasonRounds = 3, qualifiedForPostseason = true),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Flyers", postSeasonRounds = 3, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.QUALIFIED_FOR_PLAYOFFS)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.QUALIFIED_FOR_PLAYOFFS })
        assertEquals(21, data.first { it.name == "New York" }.opportunities)
        assertEquals(5, data.first { it.name == "New York" }.total)
        assertEquals(6, data.first { it.name == "Philadelphia" }.opportunities)
        assertEquals(3, data.first { it.name == "Philadelphia" }.total)
        assertEquals(1921, data.first { it.name == "New York" }.lastActiveYear)
        assertEquals(1906, data.first { it.name == "Philadelphia" }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros winning best in division`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", totalDivisions = 0, seasonStart = 2000),
            FranchiseSeasonInfoParams(5, "Atlanta", "Atlanta Braves", totalDivisions = 2, seasonStart = 2002),
            FranchiseSeasonInfoParams(3, "Atlanta", "Atlanta Braves", totalDivisions = 2, divisionPosition = Standing.FIRST, seasonStart = 2007),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Mariners", totalDivisions = 0, seasonStart = 2000),
            FranchiseSeasonInfoParams(7, "Seattle", "Seattle Mariners", totalDivisions = 2, seasonStart = 2002),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", totalDivisions = 2, divisionPosition = Standing.FIRST_TIED, seasonStart = 2009),
            FranchiseSeasonInfoParams(2, "Pittsburgh", "Pittsburgh Pirates", divisionPosition = Standing.FIRST, seasonStart = 2000),
            FranchiseSeasonInfoParams(8, "Pittsburgh", "Pittsburgh Pirates", totalDivisions = 2, seasonStart = 2002),
            FranchiseSeasonInfoParams(10, "Atlanta", "Atlanta Hawks", totalDivisions = 8, seasonStart = 2000),
            FranchiseSeasonInfoParams(10, "Pittsburgh", "Pittsburgh Petunias", seasonStart = 2000),
            FranchiseSeasonInfoParams(5, "Seattle", "Seattle Seahawks", totalDivisions = 8, seasonStart = 2000),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Seahawks", totalDivisions = 8, divisionPosition = Standing.FIRST, seasonStart = 2005),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.BEST_DIVISION)

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_DIVISION })
        assertEquals(18, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(15, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(8, data.first { it.name == "Pittsburgh" }.opportunities)
        assertEquals(3, data.first { it.name == "Atlanta" }.total)
        assertEquals(3, data.first { it.name == "Seattle" }.total)
        assertEquals(0, data.first { it.name == "Pittsburgh" }.total)
        assertEquals(2010, data.first { it.name == "Atlanta" }.lastActiveYear)
        assertEquals(2010, data.first { it.name == "Seattle" }.lastActiveYear)
        assertEquals(2010, data.first { it.name == "Pittsburgh" }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros being last in division`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", totalDivisions = 0),
            FranchiseSeasonInfoParams(5, "Atlanta", "Atlanta Braves", totalDivisions = 2),
            FranchiseSeasonInfoParams(3, "Atlanta", "Atlanta Braves", totalDivisions = 2, divisionPosition = Standing.LAST),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Mariners", totalDivisions = 0),
            FranchiseSeasonInfoParams(7, "Seattle", "Seattle Mariners", totalDivisions = 2),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", totalDivisions = 2, divisionPosition = Standing.LAST_TIED),
            FranchiseSeasonInfoParams(2, "Pittsburgh", "Pittsburgh Pirates", divisionPosition = Standing.LAST),
            FranchiseSeasonInfoParams(8, "Pittsburgh", "Pittsburgh Pirates", totalDivisions = 2),
            FranchiseSeasonInfoParams(10, "Atlanta", "Atlanta Hawks", totalDivisions = 8),
            FranchiseSeasonInfoParams(10, "Pittsburgh", "Pittsburgh Petunias"),
            FranchiseSeasonInfoParams(5, "Seattle", "Seattle Seahawks", totalDivisions = 8),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Seahawks", totalDivisions = 8, divisionPosition = Standing.LAST),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.WORST_DIVISION)

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.WORST_DIVISION })
        assertEquals(18, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(15, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(8, data.first { it.name == "Pittsburgh" }.opportunities)
        assertEquals(3, data.first { it.name == "Atlanta" }.total)
        assertEquals(3, data.first { it.name == "Seattle" }.total)
        assertEquals(0, data.first { it.name == "Pittsburgh" }.total)
    }

    @Test
    fun `gets correct data for metros being first in conference`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", totalConferences = 0),
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", totalConferences = 2, conferencePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(5, "Atlanta", "Atlanta Braves", totalConferences = 2),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", totalConferences = 2, conferencePosition = Standing.FIRST_TIED),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Mariners", totalConferences = 2, conferencePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(7, "Seattle", "Seattle Mariners", totalConferences = 2),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", totalConferences = 0, conferencePosition = Standing.FIRST_TIED),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.BEST_CONFERENCE)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_CONFERENCE })
        assertEquals(8, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(9, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(3, data.first { it.name == "Atlanta" }.total)
        assertEquals(2, data.first { it.name == "Seattle" }.total)
    }

    @Test
    fun `gets correct data for metros being last in conference`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", totalConferences = 0),
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", totalConferences = 2, conferencePosition = Standing.LAST),
            FranchiseSeasonInfoParams(5, "Atlanta", "Atlanta Braves", totalConferences = 2),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", totalConferences = 2, conferencePosition = Standing.LAST_TIED),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Mariners", totalConferences = 2, conferencePosition = Standing.LAST),
            FranchiseSeasonInfoParams(7, "Seattle", "Seattle Mariners", totalConferences = 2),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", totalConferences = 0, conferencePosition = Standing.LAST_TIED),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.WORST_CONFERENCE)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.WORST_CONFERENCE })
        assertEquals(8, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(9, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(3, data.first { it.name == "Atlanta" }.total)
        assertEquals(2, data.first { it.name == "Seattle" }.total)
    }

    @Test
    fun `gets correct data for metros being first overall`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(8, "Atlanta", "Atlanta Braves"),
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", leaguePosition = Standing.FIRST_TIED),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Mariners", leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(7, "Seattle", "Seattle Mariners"),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", leaguePosition = Standing.FIRST_TIED),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.BEST_OVERALL)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_OVERALL })
        assertEquals(11, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(10, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(3, data.first { it.name == "Atlanta" }.total)
        assertEquals(3, data.first { it.name == "Seattle" }.total)
    }

    @Test
    fun `gets correct data for metros being last overall`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(8, "Atlanta", "Atlanta Braves"),
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", leaguePosition = Standing.LAST),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", leaguePosition = Standing.LAST_TIED),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Mariners", leaguePosition = Standing.LAST),
            FranchiseSeasonInfoParams(7, "Seattle", "Seattle Mariners"),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", leaguePosition = Standing.LAST_TIED),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.WORST_OVERALL)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.WORST_OVERALL })
        assertEquals(11, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(10, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(3, data.first { it.name == "Atlanta" }.total)
        assertEquals(3, data.first { it.name == "Seattle" }.total)
    }

    @Test
    fun `filters out all seasons before 'from' parameter`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 1984, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, "Atlanta", "Atlanta Braves", seasonStart = 1985),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 1994, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(14, "Atlanta", "Atlanta Braves", seasonStart = 1995),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 2009, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(10, "Seattle", "Seattle Mariners", seasonStart = 1990),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", seasonStart = 2000, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, "Seattle", "Seattle Mariners", seasonStart = 2001),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.BEST_OVERALL, from = 1995)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_OVERALL })
        assertEquals(16, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(16, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(2, data.first { it.name == "Atlanta" }.total)
        assertEquals(1, data.first { it.name == "Seattle" }.total)
        assertEquals(2010, data.first { it.name == "Atlanta" }.lastActiveYear)
        assertEquals(2010, data.first { it.name == "Seattle" }.lastActiveYear)
    }

    @Test
    fun `filters out all seasons after 'until' parameter`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 1984, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, "Atlanta", "Atlanta Braves", seasonStart = 1985),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 1994, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(14, "Atlanta", "Atlanta Braves", seasonStart = 1995),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 2009, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(10, "Seattle", "Seattle Mariners", seasonStart = 1990),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", seasonStart = 2000, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, "Seattle", "Seattle Mariners", seasonStart = 2001),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.BEST_OVERALL, until = 2005)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_OVERALL })
        assertEquals(21, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(15, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(2, data.first { it.name == "Atlanta" }.total)
        assertEquals(1, data.first { it.name == "Seattle" }.total)
        assertEquals(2005, data.first { it.name == "Atlanta" }.lastActiveYear)
        assertEquals(2005, data.first { it.name == "Seattle" }.lastActiveYear)
    }

    @Test
    fun `filters out all seasons for both 'from' and 'until' parameters`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 1984, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, "Atlanta", "Atlanta Braves", seasonStart = 1985),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 1994, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(14, "Atlanta", "Atlanta Braves", seasonStart = 1995),
            FranchiseSeasonInfoParams(1, "Atlanta", "Atlanta Braves", seasonStart = 2009, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(10, "Seattle", "Seattle Mariners", seasonStart = 1990),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", seasonStart = 2000, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, "Seattle", "Seattle Mariners", seasonStart = 2001),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.BEST_OVERALL, from = 1995, until = 2005)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_OVERALL })
        assertEquals(11, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(11, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(1, data.first { it.name == "Atlanta" }.total)
        assertEquals(1, data.first { it.name == "Seattle" }.total)
        assertEquals(2005, data.first { it.name == "Atlanta" }.lastActiveYear)
        assertEquals(2005, data.first { it.name == "Seattle" }.lastActiveYear)
    }

    @Test
    fun `filters leagues correctly`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, "Atlanta", "Atlanta Braves", totalDivisions = 0),
            FranchiseSeasonInfoParams(5, "Atlanta", "Atlanta Braves", totalDivisions = 2),
            FranchiseSeasonInfoParams(3, "Atlanta", "Atlanta Braves", totalDivisions = 2, divisionPosition = Standing.LAST),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Mariners", totalDivisions = 0),
            FranchiseSeasonInfoParams(7, "Seattle", "Seattle Mariners", totalDivisions = 2),
            FranchiseSeasonInfoParams(1, "Seattle", "Seattle Mariners", totalDivisions = 2, divisionPosition = Standing.LAST_TIED),
            FranchiseSeasonInfoParams(2, "Pittsburgh", "Pittsburgh Pirates", divisionPosition = Standing.LAST),
            FranchiseSeasonInfoParams(8, "Pittsburgh", "Pittsburgh Pirates", totalDivisions = 2),
            FranchiseSeasonInfoParams(10, "Atlanta", "Atlanta Hawks", totalDivisions = 8, leagueId = 2),
            FranchiseSeasonInfoParams(10, "Pittsburgh", "Pittsburgh Petunias", leagueId = 2),
            FranchiseSeasonInfoParams(5, "Seattle", "Seattle Seahawks", totalDivisions = 8, leagueId = 3),
            FranchiseSeasonInfoParams(2, "Seattle", "Seattle Seahawks", totalDivisions = 8, divisionPosition = Standing.LAST, leagueId = 3),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.WORST_DIVISION, leagueIds = setOf(1, 2))

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.WORST_DIVISION })
        assertEquals(18, data.first { it.name == "Atlanta" }.opportunities)
        assertEquals(8, data.first { it.name == "Seattle" }.opportunities)
        assertEquals(8, data.first { it.name == "Pittsburgh" }.opportunities)
        assertEquals(3, data.first { it.name == "Atlanta" }.total)
        assertEquals(1, data.first { it.name == "Seattle" }.total)
        assertEquals(0, data.first { it.name == "Pittsburgh" }.total)
    }

    @Test
    fun `gets correct data for championship winning percentage`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 2, true, true, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 2, true, true, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 1, true, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 0, false, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo("New York", "New York Yankees", "AL", "AL East", null, null, null, true, 2, 2, true, true, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, true, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, true, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo("New York", "New York Mets", "NL", "NL East", null, null, null, true, 2, 2, false, false, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 11, 1990, 1990),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 12, 1991, 1991),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 2, true, false, 2, 6, 2, 13, 1992, 1992),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 2, true, true, 2, 6, 2, 14, 1993, 1993),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 15, 1994, 1994),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 16, 1995, 1995),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 17, 1996, 1996),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 18, 1997, 1997),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 19, 1998, 1998),
            FranchiseSeasonInfo("New York", "New York Giants", "NFC", "NFC East", null, null, null, true, 2, 0, false, false, 2, 6, 2, 20, 1999, 1999),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 2, true, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo("Minneapolis", "Minnesota Twins", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo("Columbus", "Columbus Clubbers", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 2, false, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 2, true, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 1, true, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 0, false, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo("Cleveland", "Cleveland Guardians", "AL", "AL Central", null, null, null, true, 2, 2, true, false, 2, 6, 1, 10, 1999, 1999),
        )

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.CHAMPIONSHIPS_WINNING_RATE)

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.CHAMPIONSHIPS_WINNING_RATE })
        assertEquals(7, data.first { it.name == "New York" }.opportunities)
        assertEquals(1, data.first { it.name == "Minneapolis" }.opportunities)
        assertEquals(3, data.first { it.name == "Cleveland" }.opportunities)
        assertEquals(4, data.first { it.name == "New York" }.total)
        assertEquals(0, data.first { it.name == "Minneapolis" }.total)
        assertEquals(0, data.first { it.name == "Cleveland" }.total)
    }

    @Test
    fun `gets correct data for championship appearances per postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees"),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", qualifiedForPostseason = true, postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", qualifiedForPostseason = true, postSeasonRounds = 1, appearedInChampionship = true, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", qualifiedForPostseason = true, postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", qualifiedForPostseason = true, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, "New York", "New York Knicks", postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, "New York", "New York Knicks", qualifiedForPostseason = true, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Phillies", qualifiedForPostseason = true, postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Flyers", qualifiedForPostseason = true, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.CHAMPIONSHIP_APPEARANCES_PER_POSTSEASON)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.CHAMPIONSHIP_APPEARANCES_PER_POSTSEASON })
        assertEquals(19, data.first { it.name == "New York" }.opportunities)
        assertEquals(6, data.first { it.name == "Philadelphia" }.opportunities)
        assertEquals(11, data.first { it.name == "New York" }.total)
        assertEquals(3, data.first { it.name == "Philadelphia" }.total)
    }

    @Test
    fun `gets correct data for advancing in playoffs per postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees"),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, "New York", "New York Yankees", qualifiedForPostseason = true, postSeasonRounds = 1, postSeasonRoundsWon = 1, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", qualifiedForPostseason = true, postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, "New York", "New York Yankees", qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 1, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, "New York", "New York Knicks", qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, "New York", "New York Knicks", qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 0, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Phillies", qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, "Philadelphia", "Philadelphia Flyers", qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 0, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.ADVANCED_IN_PLAYOFFS_PER_POSTSEASON)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.ADVANCED_IN_PLAYOFFS_PER_POSTSEASON })
        assertEquals(11, data.first { it.name == "New York" }.opportunities)
        assertEquals(6, data.first { it.name == "Philadelphia" }.opportunities)
        assertEquals(5, data.first { it.name == "New York" }.total)
        assertEquals(3, data.first { it.name == "Philadelphia" }.total)
    }

    data class FranchiseSeasonInfoParams(
        val instances: Int,
        val metro: String,
        val teamName: String,
        val postSeasonRounds: Int? = null,
        val appearedInChampionship: Boolean = false,
        val postSeasonRoundsWon: Int? = null,
        val qualifiedForPostseason: Boolean = false,
        val leaguePosition: Standing? = null,
        val totalConferences: Int = 0,
        val conferencePosition: Standing? = null,
        val totalDivisions: Int = 0,
        val divisionPosition: Standing? = null,
        val leagueId: Int = 1,
        val seasonId: Int = 1,
        val seasonStart: Int = 1900
    )

    private fun generateFranchiseSeasonInfoList(
        params: FranchiseSeasonInfoParams
    ): List<FranchiseSeasonInfo> {
        return (1..params.instances).map { counter ->
            FranchiseSeasonInfo(
                params.metro,
                params.teamName,
                null,
                null,
                params.leaguePosition,
                params.conferencePosition,
                params.divisionPosition,
                params.qualifiedForPostseason,
                params.postSeasonRounds,
                params.postSeasonRoundsWon,
                params.appearedInChampionship,
                false,
                params.totalConferences,
                params.totalDivisions,
                params.leagueId,
                params.seasonId,
                params.seasonStart + counter,
                params.seasonStart + counter
            )
        }
    }
}