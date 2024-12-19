package mabersold.services

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mabersold.dao.ChapterDAO
import mabersold.dao.FranchiseDAO
import mabersold.models.db.Franchise
import kotlin.test.Test
import kotlin.test.assertEquals

class FranchiseDataServiceTest {
    private val franchiseDAO = mockk<FranchiseDAO>()
    private val chapterDAO = mockk<ChapterDAO>()
    private val franchiseDataService = FranchiseDataService(franchiseDAO, chapterDAO)

    @Test
    fun `returns a list of franchises for a league`() = runTest {
        // Arrange
        val leagueId = 1
        val franchises = listOf(
            Franchise(1, "New York Yankees", "yankees", false, leagueId),
            Franchise(2, "Boston Red Sox", "redsox", false, leagueId),
            Franchise(3, "Baltimore Orioles", "orioles", false, leagueId),
            Franchise(4, "Tampa Bay Rays", "rays", false, leagueId),
            Franchise(5, "Toronto Blue Jays", "bluejays", false, leagueId)
        )

        coEvery { franchiseDAO.allByLeagueId(leagueId) } returns franchises

        // Act
        val result = franchiseDataService.getFranchises(leagueId)

        // Assert
        assertEquals(5, result.size)
        result.forEachIndexed { index, franchise ->
            assertEquals(franchises[index].id, franchise.id)
            assertEquals(franchises[index].name, franchise.name)
            assertEquals(franchises[index].isDefunct, franchise.isDefunct)
            assertEquals(franchises[index].leagueId, franchise.leagueId)
        }
    }
}