package mabersold.services

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mabersold.dao.FranchiseSeasonDAO
import mabersold.models.FranchiseSeasonInfo
import mabersold.models.MetricType
import mabersold.models.Metro
import mabersold.models.db.Standing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetroDataServiceTest {
    private val franchiseSeasonDAO = mockk<FranchiseSeasonDAO>()
    private val metroDataService = MetroDataService(franchiseSeasonDAO)
    @Test
    fun `gets correct data for metros advancing in the postseason`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(5, Metro.NEW_YORK, "New York Yankees"),
            FranchiseSeasonInfoParams(5, Metro.NEW_YORK, "New York Yankees", postSeasonRounds = 0),
            FranchiseSeasonInfoParams(5, Metro.NEW_YORK, "New York Yankees", postSeasonRounds = 1),
            FranchiseSeasonInfoParams(3, Metro.NEW_YORK, "New York Yankees", postSeasonRounds = 3),
            FranchiseSeasonInfoParams(3, Metro.NEW_YORK, "New York Yankees", postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(2, Metro.NEW_YORK, "New York Knicks", postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, Metro.NEW_YORK, "New York Knicks", postSeasonRounds = 3, postSeasonRoundsWon = 0),
            FranchiseSeasonInfoParams(3, Metro.PHILADELPHIA, "Philadelphia Phillies", postSeasonRounds = 3, postSeasonRoundsWon = 1),
            FranchiseSeasonInfoParams(3, Metro.PHILADELPHIA, "Philadelphia Flyers", postSeasonRounds = 3, postSeasonRoundsWon = 0),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.ADVANCED_IN_PLAYOFFS)

        // Assert
        assertEquals(2, data.size)
        assertTrue(data.all { it.metricType == MetricType.ADVANCED_IN_PLAYOFFS })
        assertEquals(11, data.first { it.name == Metro.NEW_YORK.displayName }.opportunities)
        assertEquals(6, data.first { it.name == Metro.PHILADELPHIA.displayName }.opportunities)
        assertEquals(5, data.first { it.name == Metro.NEW_YORK.displayName }.total)
        assertEquals(3, data.first { it.name == Metro.PHILADELPHIA.displayName }.total)
    }

    @Test
    fun `gets correct data for metros winning best in division`() = runTest {
        // Arrange
        val franchiseSeasons = listOf(
            FranchiseSeasonInfoParams(2, Metro.ATLANTA, "Atlanta Braves", totalDivisions = 0),
            FranchiseSeasonInfoParams(5, Metro.ATLANTA, "Atlanta Braves", totalDivisions = 2),
            FranchiseSeasonInfoParams(3, Metro.ATLANTA, "Atlanta Braves", totalDivisions = 2, divisionPosition = Standing.FIRST),
            FranchiseSeasonInfoParams(2, Metro.SEATTLE, "Seattle Mariners", totalDivisions = 0),
            FranchiseSeasonInfoParams(7, Metro.SEATTLE, "Seattle Mariners", totalDivisions = 2),
            FranchiseSeasonInfoParams(1, Metro.SEATTLE, "Seattle Mariners", totalDivisions = 2, divisionPosition = Standing.FIRST_TIED),
            FranchiseSeasonInfoParams(2, Metro.PITTSBURGH, "Pittsburgh Pirates", divisionPosition = Standing.FIRST),
            FranchiseSeasonInfoParams(8, Metro.PITTSBURGH, "Pittsburgh Pirates", totalDivisions = 2),
            FranchiseSeasonInfoParams(10, Metro.ATLANTA, "Atlanta Hawks", totalDivisions = 8),
            FranchiseSeasonInfoParams(10, Metro.PITTSBURGH, "Pittsburgh Petunias"),
            FranchiseSeasonInfoParams(5, Metro.SEATTLE, "Seattle Seahawks", totalDivisions = 8),
            FranchiseSeasonInfoParams(2, Metro.SEATTLE, "Seattle Seahawks", totalDivisions = 8, divisionPosition = Standing.FIRST),
        ).flatMap { generateFranchiseSeasonInfoList(it) }

        coEvery { franchiseSeasonDAO.all() } returns franchiseSeasons

        // Act
        val data = metroDataService.getMetroDataByMetric(MetricType.BEST_DIVISION)

        // Assert
        assertEquals(3, data.size)
        assertTrue(data.all { it.metricType == MetricType.BEST_DIVISION })
        assertEquals(18, data.first { it.name == Metro.ATLANTA.displayName }.opportunities)
        assertEquals(15, data.first { it.name == Metro.SEATTLE.displayName }.opportunities)
        assertEquals(8, data.first { it.name == Metro.PITTSBURGH.displayName }.opportunities)
        assertEquals(3, data.first { it.name == Metro.ATLANTA.displayName }.total)
        assertEquals(3, data.first { it.name == Metro.SEATTLE.displayName }.total)
        assertEquals(0, data.first { it.name == Metro.PITTSBURGH.displayName }.total)
    }

    data class FranchiseSeasonInfoParams(
        val instances: Int,
        val metro: Metro,
        val teamName: String,
        val postSeasonRounds: Int? = null,
        val postSeasonRoundsWon: Int? = null,
        val totalDivisions: Int = 0,
        val divisionPosition: Standing? = null
    )

    private fun generateFranchiseSeasonInfoList(
        params: FranchiseSeasonInfoParams
    ): List<FranchiseSeasonInfo> {
        return (1..params.instances).map {
            FranchiseSeasonInfo(
                params.metro,
                params.teamName,
                null,
                null,
                null,
                null,
                params.divisionPosition,
                false,
                params.postSeasonRounds,
                params.postSeasonRoundsWon,
                false,
                false,
                params.totalDivisions
            )
        }
    }
}