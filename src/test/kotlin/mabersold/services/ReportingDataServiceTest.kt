package mabersold.services

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mabersold.dao.FranchiseSeasonDAO
import mabersold.models.FranchiseSeasonInfo
import mabersold.models.api.MetricType
import mabersold.models.db.Standing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReportingDataServiceTest {
    private val franchiseSeasonDAO = mockk<FranchiseSeasonDAO>()
    private val reportingDataService = ReportingDataService(franchiseSeasonDAO)

    companion object {
        const val ATLANTA = "Atlanta"
        const val CLEVELAND = "Cleveland"
        const val COLUMBUS = "Columbus"
        const val MINNEAPOLIS = "Minneapolis"
        const val NEW_YORK = "New York"
        const val PHILADELPHIA = "Philadelphia"
        const val PITTSBURGH = "Pittsburgh"
        const val SEATTLE = "Seattle"

        const val AL = "AL"
        const val NL = "NL"
        const val AL_EAST = "AL East"
        const val AL_CENTRAL = "AL Central"
        const val NL_EAST = "NL East"
        const val BRAVES = "Atlanta Braves"
        const val CLUBBERS = "Columbus Clubbers"
        const val GUARDIANS = "Cleveland Guardians"
        const val MARINERS = "Seattle Mariners"
        const val METS = "New York Mets"
        const val PHILLIES = "Philadelphia Phillies"
        const val PIRATES = "Pittsburgh Pirates"
        const val TWINS = "Minnesota Twins"
        const val YANKEES = "New York Yankees"

        const val NFC = "NFC"
        const val NFC_EAST = "NFC East"
        const val GIANTS = "New York Giants"
        const val SEAHAWKS = "Seattle Seahawks"

        const val HAWKS = "Atlanta Hawks"
        const val KNICKS = "New York Knicks"

        const val FLYERS = "Philadelphia Flyers"
        
        const val PETUNIAS = "Pittsburgh Petunias"
    }

    @Test
    fun `gets correct data for metros winning the championship`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 2, true, true, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 2, true, true, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 1, true, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 2, true, true, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 11, 1990, 1990),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 12, 1991, 1991),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 2, true, false, 2, 6, 2, 13, 1992, 1992),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 2, true, true, 2, 6, 2, 14, 1993, 1993),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 15, 1994, 1994),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 16, 1995, 1995),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 17, 1996, 1996),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 18, 1997, 1997),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 19, 1998, 1998),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 20, 1999, 1999),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 2, true, true, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 10, 1999, 1999),
        )

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.TOTAL_CHAMPIONSHIPS)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.TOTAL_CHAMPIONSHIPS })
        assertEquals(25, data.first { it.name == NEW_YORK }.opportunities)
        assertEquals(4, data.first { it.name == NEW_YORK }.total)
        assertEquals(1999, data.first { it.name == NEW_YORK }.lastActiveYear)
        assertEquals(9, data.first { it.name == MINNEAPOLIS }.opportunities)
        assertEquals(1, data.first { it.name == MINNEAPOLIS }.total)
        assertEquals(1999, data.first { it.name == MINNEAPOLIS }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros appearing in championship`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, postSeasonRounds = 1, appearedInChampionship = true, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, NEW_YORK, KNICKS, postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, NEW_YORK, KNICKS, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, PHILLIES, postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, FLYERS, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.CHAMPIONSHIP_APPEARANCES)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.CHAMPIONSHIP_APPEARANCES })
        assertEquals(21, data.first { it.name == NEW_YORK }.opportunities)
        assertEquals(11, data.first { it.name == NEW_YORK }.total)
        assertEquals(1921, data.first { it.name == NEW_YORK }.lastActiveYear)
        assertEquals(6, data.first { it.name == PHILADELPHIA }.opportunities)
        assertEquals(3, data.first { it.name == PHILADELPHIA }.total)
        assertEquals(1906, data.first { it.name == PHILADELPHIA }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros advancing in the postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, postSeasonRounds = 1, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, postSeasonRounds = 3, postSeasonRoundsWon = 1, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, NEW_YORK, KNICKS, postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, NEW_YORK, KNICKS, postSeasonRounds = 3, postSeasonRoundsWon = 0, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, PHILLIES, postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, FLYERS, postSeasonRounds = 3, postSeasonRoundsWon = 0, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.ADVANCED_IN_PLAYOFFS)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.ADVANCED_IN_PLAYOFFS })
        assertEquals(11, data.first { it.name == NEW_YORK }.opportunities)
        assertEquals(6, data.first { it.name == PHILADELPHIA }.opportunities)
        assertEquals(5, data.first { it.name == NEW_YORK }.total)
        assertEquals(3, data.first { it.name == PHILADELPHIA }.total)
        assertEquals(1921, data.first { it.name == NEW_YORK }.lastActiveYear)
        assertEquals(1906, data.first { it.name == PHILADELPHIA }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros qualifying for postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, postSeasonRounds = 1, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, postSeasonRounds = 3, qualifiedForPostseason = true, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, NEW_YORK, KNICKS, postSeasonRounds = 3, qualifiedForPostseason = true),
            FranchiseSeasonInfoParams(3, NEW_YORK, KNICKS, postSeasonRounds = 3, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, PHILLIES, postSeasonRounds = 3, qualifiedForPostseason = true),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, FLYERS, postSeasonRounds = 3, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.QUALIFIED_FOR_PLAYOFFS)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.QUALIFIED_FOR_PLAYOFFS })
        assertEquals(21, data.first { it.name == NEW_YORK }.opportunities)
        assertEquals(5, data.first { it.name == NEW_YORK }.total)
        assertEquals(6, data.first { it.name == PHILADELPHIA }.opportunities)
        assertEquals(3, data.first { it.name == PHILADELPHIA }.total)
        assertEquals(1921, data.first { it.name == NEW_YORK }.lastActiveYear)
        assertEquals(1906, data.first { it.name == PHILADELPHIA }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros winning best in division`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, totalDivisions = 0, seasonStart = 2000),
            FranchiseSeasonInfoParams(5, ATLANTA, BRAVES, totalDivisions = 2, seasonStart = 2002),
            FranchiseSeasonInfoParams(3, ATLANTA, BRAVES, totalDivisions = 2, divisionPosition = Standing.FIRST, seasonStart = 2007),
            FranchiseSeasonInfoParams(2, SEATTLE, MARINERS, totalDivisions = 0, seasonStart = 2000),
            FranchiseSeasonInfoParams(7, SEATTLE, MARINERS, totalDivisions = 2, seasonStart = 2002),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, totalDivisions = 2, divisionPosition = Standing.FIRST_TIED, seasonStart = 2009),
            FranchiseSeasonInfoParams(2, PITTSBURGH, PIRATES, divisionPosition = Standing.FIRST, seasonStart = 2000),
            FranchiseSeasonInfoParams(8, PITTSBURGH, PIRATES, totalDivisions = 2, seasonStart = 2002),
            FranchiseSeasonInfoParams(10, ATLANTA, HAWKS, totalDivisions = 8, seasonStart = 2000),
            FranchiseSeasonInfoParams(10, PITTSBURGH, PETUNIAS, seasonStart = 2000),
            FranchiseSeasonInfoParams(5, SEATTLE, SEAHAWKS, totalDivisions = 8, seasonStart = 2000),
            FranchiseSeasonInfoParams(2, SEATTLE, SEAHAWKS, totalDivisions = 8, divisionPosition = Standing.FIRST, seasonStart = 2005),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.BEST_DIVISION)

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_DIVISION })
        assertEquals(18, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(15, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(8, data.first { it.name == PITTSBURGH }.opportunities)
        assertEquals(3, data.first { it.name == ATLANTA }.total)
        assertEquals(3, data.first { it.name == SEATTLE }.total)
        assertEquals(0, data.first { it.name == PITTSBURGH }.total)
        assertEquals(2010, data.first { it.name == ATLANTA }.lastActiveYear)
        assertEquals(2010, data.first { it.name == SEATTLE }.lastActiveYear)
        assertEquals(2010, data.first { it.name == PITTSBURGH }.lastActiveYear)
    }

    @Test
    fun `gets correct data for metros being last in division`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, totalDivisions = 0),
            FranchiseSeasonInfoParams(5, ATLANTA, BRAVES, totalDivisions = 2),
            FranchiseSeasonInfoParams(3, ATLANTA, BRAVES, totalDivisions = 2, divisionPosition = Standing.LAST),
            FranchiseSeasonInfoParams(2, SEATTLE, MARINERS, totalDivisions = 0),
            FranchiseSeasonInfoParams(7, SEATTLE, MARINERS, totalDivisions = 2),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, totalDivisions = 2, divisionPosition = Standing.LAST_TIED),
            FranchiseSeasonInfoParams(2, PITTSBURGH, PIRATES, divisionPosition = Standing.LAST),
            FranchiseSeasonInfoParams(8, PITTSBURGH, PIRATES, totalDivisions = 2),
            FranchiseSeasonInfoParams(10, ATLANTA, HAWKS, totalDivisions = 8),
            FranchiseSeasonInfoParams(10, PITTSBURGH, PETUNIAS),
            FranchiseSeasonInfoParams(5, SEATTLE, SEAHAWKS, totalDivisions = 8),
            FranchiseSeasonInfoParams(2, SEATTLE, SEAHAWKS, totalDivisions = 8, divisionPosition = Standing.LAST),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.WORST_DIVISION)

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.WORST_DIVISION })
        assertEquals(18, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(15, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(8, data.first { it.name == PITTSBURGH }.opportunities)
        assertEquals(3, data.first { it.name == ATLANTA }.total)
        assertEquals(3, data.first { it.name == SEATTLE }.total)
        assertEquals(0, data.first { it.name == PITTSBURGH }.total)
    }

    @Test
    fun `gets correct data for metros being first in conference`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, totalConferences = 0),
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, totalConferences = 2, conferencePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(5, ATLANTA, BRAVES, totalConferences = 2),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, totalConferences = 2, conferencePosition = Standing.FIRST_TIED),
            FranchiseSeasonInfoParams(2, SEATTLE, MARINERS, totalConferences = 2, conferencePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(7, SEATTLE, MARINERS, totalConferences = 2),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, totalConferences = 0, conferencePosition = Standing.FIRST_TIED),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.BEST_CONFERENCE)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_CONFERENCE })
        assertEquals(8, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(9, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(3, data.first { it.name == ATLANTA }.total)
        assertEquals(2, data.first { it.name == SEATTLE }.total)
    }

    @Test
    fun `gets correct data for metros being last in conference`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, totalConferences = 0),
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, totalConferences = 2, conferencePosition = Standing.LAST),
            FranchiseSeasonInfoParams(5, ATLANTA, BRAVES, totalConferences = 2),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, totalConferences = 2, conferencePosition = Standing.LAST_TIED),
            FranchiseSeasonInfoParams(2, SEATTLE, MARINERS, totalConferences = 2, conferencePosition = Standing.LAST),
            FranchiseSeasonInfoParams(7, SEATTLE, MARINERS, totalConferences = 2),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, totalConferences = 0, conferencePosition = Standing.LAST_TIED),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.WORST_CONFERENCE)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.WORST_CONFERENCE })
        assertEquals(8, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(9, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(3, data.first { it.name == ATLANTA }.total)
        assertEquals(2, data.first { it.name == SEATTLE }.total)
    }

    @Test
    fun `gets correct data for metros being first overall`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(8, ATLANTA, BRAVES),
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, leaguePosition = Standing.FIRST_TIED),
            FranchiseSeasonInfoParams(2, SEATTLE, MARINERS, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(7, SEATTLE, MARINERS),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, leaguePosition = Standing.FIRST_TIED),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.BEST_OVERALL)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_OVERALL })
        assertEquals(11, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(10, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(3, data.first { it.name == ATLANTA }.total)
        assertEquals(3, data.first { it.name == SEATTLE }.total)
    }

    @Test
    fun `gets correct data for metros being last overall`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(8, ATLANTA, BRAVES),
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, leaguePosition = Standing.LAST),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, leaguePosition = Standing.LAST_TIED),
            FranchiseSeasonInfoParams(2, SEATTLE, MARINERS, leaguePosition = Standing.LAST),
            FranchiseSeasonInfoParams(7, SEATTLE, MARINERS),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, leaguePosition = Standing.LAST_TIED),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.WORST_OVERALL)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.WORST_OVERALL })
        assertEquals(11, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(10, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(3, data.first { it.name == ATLANTA }.total)
        assertEquals(3, data.first { it.name == SEATTLE }.total)
    }

    @Test
    fun `filters out all seasons before 'from' parameter`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 1984, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, ATLANTA, BRAVES, seasonStart = 1985),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 1994, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(14, ATLANTA, BRAVES, seasonStart = 1995),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 2009, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(10, SEATTLE, MARINERS, seasonStart = 1990),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, seasonStart = 2000, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, SEATTLE, MARINERS, seasonStart = 2001),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.BEST_OVERALL, from = 1995)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_OVERALL })
        assertEquals(16, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(16, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(2, data.first { it.name == ATLANTA }.total)
        assertEquals(1, data.first { it.name == SEATTLE }.total)
        assertEquals(2010, data.first { it.name == ATLANTA }.lastActiveYear)
        assertEquals(2010, data.first { it.name == SEATTLE }.lastActiveYear)
    }

    @Test
    fun `filters out all seasons after 'until' parameter`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 1984, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, ATLANTA, BRAVES, seasonStart = 1985),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 1994, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(14, ATLANTA, BRAVES, seasonStart = 1995),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 2009, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(10, SEATTLE, MARINERS, seasonStart = 1990),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, seasonStart = 2000, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, SEATTLE, MARINERS, seasonStart = 2001),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.BEST_OVERALL, until = 2005)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_OVERALL })
        assertEquals(21, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(15, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(2, data.first { it.name == ATLANTA }.total)
        assertEquals(1, data.first { it.name == SEATTLE }.total)
        assertEquals(2005, data.first { it.name == ATLANTA }.lastActiveYear)
        assertEquals(2005, data.first { it.name == SEATTLE }.lastActiveYear)
    }

    @Test
    fun `filters out all seasons for both 'from' and 'until' parameters`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 1984, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, ATLANTA, BRAVES, seasonStart = 1985),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 1994, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(14, ATLANTA, BRAVES, seasonStart = 1995),
            FranchiseSeasonInfoParams(1, ATLANTA, BRAVES, seasonStart = 2009, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(10, SEATTLE, MARINERS, seasonStart = 1990),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, seasonStart = 2000, leaguePosition = Standing.FIRST),
            FranchiseSeasonInfoParams(9, SEATTLE, MARINERS, seasonStart = 2001),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.BEST_OVERALL, from = 1995, until = 2005)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_OVERALL })
        assertEquals(11, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(11, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(1, data.first { it.name == ATLANTA }.total)
        assertEquals(1, data.first { it.name == SEATTLE }.total)
        assertEquals(2005, data.first { it.name == ATLANTA }.lastActiveYear)
        assertEquals(2005, data.first { it.name == SEATTLE }.lastActiveYear)
    }

    @Test
    fun `filters leagues correctly`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, ATLANTA, BRAVES, totalDivisions = 0),
            FranchiseSeasonInfoParams(5, ATLANTA, BRAVES, totalDivisions = 2),
            FranchiseSeasonInfoParams(3, ATLANTA, BRAVES, totalDivisions = 2, divisionPosition = Standing.LAST),
            FranchiseSeasonInfoParams(2, SEATTLE, MARINERS, totalDivisions = 0),
            FranchiseSeasonInfoParams(7, SEATTLE, MARINERS, totalDivisions = 2),
            FranchiseSeasonInfoParams(1, SEATTLE, MARINERS, totalDivisions = 2, divisionPosition = Standing.LAST_TIED),
            FranchiseSeasonInfoParams(2, PITTSBURGH, PIRATES, divisionPosition = Standing.LAST),
            FranchiseSeasonInfoParams(8, PITTSBURGH, PIRATES, totalDivisions = 2),
            FranchiseSeasonInfoParams(10, ATLANTA, HAWKS, totalDivisions = 8, leagueId = 2),
            FranchiseSeasonInfoParams(10, PITTSBURGH, PETUNIAS, leagueId = 2),
            FranchiseSeasonInfoParams(5, SEATTLE, SEAHAWKS, totalDivisions = 8, leagueId = 3),
            FranchiseSeasonInfoParams(2, SEATTLE, SEAHAWKS, totalDivisions = 8, divisionPosition = Standing.LAST, leagueId = 3),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.WORST_DIVISION, leagueIds = setOf(1, 2))

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.WORST_DIVISION })
        assertEquals(18, data.first { it.name == ATLANTA }.opportunities)
        assertEquals(8, data.first { it.name == SEATTLE }.opportunities)
        assertEquals(8, data.first { it.name == PITTSBURGH }.opportunities)
        assertEquals(3, data.first { it.name == ATLANTA }.total)
        assertEquals(1, data.first { it.name == SEATTLE }.total)
        assertEquals(0, data.first { it.name == PITTSBURGH }.total)
    }

    @Test
    fun `gets correct data for championship winning percentage`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 2, true, true, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 2, true, true, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 1, true, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo(NEW_YORK, YANKEES, AL, AL_EAST, null, null, null, true, 2, 2, true, true, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, true, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, true, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo(NEW_YORK, METS, NL, NL_EAST, null, null, null, true, 2, 2, false, false, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 11, 1990, 1990),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 12, 1991, 1991),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 2, true, false, 2, 6, 2, 13, 1992, 1992),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 2, true, true, 2, 6, 2, 14, 1993, 1993),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 15, 1994, 1994),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 16, 1995, 1995),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 17, 1996, 1996),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 18, 1997, 1997),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 19, 1998, 1998),
            FranchiseSeasonInfo(NEW_YORK, GIANTS, NFC, NFC_EAST, null, null, null, true, 2, 0, false, false, 2, 6, 2, 20, 1999, 1999),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 2, true, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo(MINNEAPOLIS, TWINS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo(COLUMBUS, CLUBBERS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 10, 1999, 1999),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 2, false, false, 2, 6, 1, 1, 1990, 1990),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 2, true, false, 2, 6, 1, 2, 1991, 1991),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 1, true, false, 2, 6, 1, 3, 1992, 1992),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 4, 1993, 1993),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 5, 1994, 1994),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, null, null, false, false, 2, 6, 1, 6, 1995, 1995),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 7, 1996, 1996),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 8, 1997, 1997),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 0, false, false, 2, 6, 1, 9, 1998, 1998),
            FranchiseSeasonInfo(CLEVELAND, GUARDIANS, AL, AL_CENTRAL, null, null, null, true, 2, 2, true, false, 2, 6, 1, 10, 1999, 1999),
        )

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.CHAMPIONSHIPS_WINNING_RATE)

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.CHAMPIONSHIPS_WINNING_RATE })
        assertEquals(7, data.first { it.name == NEW_YORK }.opportunities)
        assertEquals(1, data.first { it.name == MINNEAPOLIS }.opportunities)
        assertEquals(3, data.first { it.name == CLEVELAND }.opportunities)
        assertEquals(4, data.first { it.name == NEW_YORK }.total)
        assertEquals(0, data.first { it.name == MINNEAPOLIS }.total)
        assertEquals(0, data.first { it.name == CLEVELAND }.total)
    }

    @Test
    fun `gets correct data for championship appearances per postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, qualifiedForPostseason = true, postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, qualifiedForPostseason = true, postSeasonRounds = 1, appearedInChampionship = true, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, qualifiedForPostseason = true, postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, qualifiedForPostseason = true, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, NEW_YORK, KNICKS, postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, NEW_YORK, KNICKS, qualifiedForPostseason = true, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, PHILLIES, qualifiedForPostseason = true, postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, FLYERS, qualifiedForPostseason = true, postSeasonRounds = 3, appearedInChampionship = true, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.CHAMPIONSHIP_APPEARANCES_PER_POSTSEASON)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.CHAMPIONSHIP_APPEARANCES_PER_POSTSEASON })
        assertEquals(19, data.first { it.name == NEW_YORK }.opportunities)
        assertEquals(6, data.first { it.name == PHILADELPHIA }.opportunities)
        assertEquals(11, data.first { it.name == NEW_YORK }.total)
        assertEquals(3, data.first { it.name == PHILADELPHIA }.total)
    }

    @Test
    fun `gets correct data for advancing in playoffs per postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, postSeasonRounds = 0, seasonStart = 1905),
            FranchiseSeasonInfoParams(5, NEW_YORK, YANKEES, qualifiedForPostseason = true, postSeasonRounds = 1, postSeasonRoundsWon = 1, seasonStart = 1910),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, qualifiedForPostseason = true, postSeasonRounds = 3, seasonStart = 1915),
            FranchiseSeasonInfoParams(3, NEW_YORK, YANKEES, qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 1, seasonStart = 1918),
            FranchiseSeasonInfoParams(2, NEW_YORK, KNICKS, qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, NEW_YORK, KNICKS, qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 0, seasonStart = 1902),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, PHILLIES, qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, PHILADELPHIA, FLYERS, qualifiedForPostseason = true, postSeasonRounds = 3, postSeasonRoundsWon = 0, seasonStart = 1903),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = reportingDataService.getMetroReportByMetric(MetricType.ADVANCED_IN_PLAYOFFS_PER_POSTSEASON)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.ADVANCED_IN_PLAYOFFS_PER_POSTSEASON })
        assertEquals(11, data.first { it.name == NEW_YORK }.opportunities)
        assertEquals(6, data.first { it.name == PHILADELPHIA }.opportunities)
        assertEquals(5, data.first { it.name == NEW_YORK }.total)
        assertEquals(3, data.first { it.name == PHILADELPHIA }.total)
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