package mabersold.services

import mabersold.MOST_RECENT_COMPLETED_MLB_SEASON
import mabersold.models.League
import mabersold.models.Metro
import kotlin.test.Test
import kotlin.test.assertEquals

class FranchiseDataServiceTest {
    private val franchiseDataService = FranchiseDataService()

    @Test
    fun `translates franchise data correctly from json file`() {
        // Given

        // When
        val franchises = franchiseDataService.getFranchiseData("test_franchises.json")

        // Then
        assertEquals(2, franchises.size)
        assertEquals(1, franchises.first().timeline.size)
        assertEquals(2, franchises.last().timeline.size)
        assertEquals("Enumclaw Rednecks", franchises.first().name)
        assertEquals("Bellevue Nimbys", franchises.last().name)
    }

    @Test
    fun `gets a list of franchises from a single source`() {
        // Given
        val sources = mapOf(League.MLB to listOf("test_franchises.json"))

        // When
        val franchises = franchiseDataService.getFranchiseData(sources)

        // Then
        assertEquals(2, franchises.size)
        assertEquals("Bellevue Nimbys", franchises[0].name)
        assertEquals(League.MLB, franchises[0].league)
        assertEquals(2, franchises[0].timeline.size)
        assertEquals("Enumclaw Rednecks", franchises[1].name)
        assertEquals(1, franchises[1].timeline.size)
        assertEquals(League.MLB, franchises[1].league)
    }

    @Test
    fun `gets a list of franchises from multiple sources`() {
        // Given
        val sources = mapOf(
            League.MLB to listOf("test_franchises.json"),
            League.NFL to listOf("test_franchises_nfl_new.json")
        )

        // When
        val franchises = franchiseDataService.getFranchiseData(sources)

        // Then
        assertEquals(4, franchises.size)

        assertEquals("Atlanta Disease Controllers", franchises[0].name)
        assertEquals(1, franchises[0].timeline.size)
        assertEquals(Metro.ATLANTA, franchises[0].timeline[0].metroArea)
        assertEquals(League.NFL, franchises[0].league)

        assertEquals("Baltimore Wires", franchises[1].name)
        assertEquals(1, franchises[1].timeline.size)
        assertEquals(Metro.BALTIMORE, franchises[1].timeline[0].metroArea)
        assertEquals(League.NFL, franchises[1].league)

        assertEquals("Bellevue Nimbys", franchises[2].name)
        assertEquals(2, franchises[2].timeline.size)
        assertEquals(Metro.SEATTLE, franchises[2].timeline[0].metroArea)
        assertEquals(Metro.SEATTLE, franchises[2].timeline[1].metroArea)
        assertEquals(League.MLB, franchises[2].league)

        assertEquals("Enumclaw Rednecks", franchises[3].name)
        assertEquals(1, franchises[3].timeline.size)
        assertEquals(Metro.SEATTLE, franchises[3].timeline[0].metroArea)
        assertEquals(League.MLB, franchises[3].league)
    }

    @Test
    fun `gets a list of franchises from multiple sources and merges franchises`() {
        // Given
        val sources = mapOf(
            League.MLB to listOf("test_franchises.json"),
            League.NFL to listOf("test_franchises_nfl_old.json", "test_franchises_nfl_new.json")
        )

        // When
        val franchises = franchiseDataService.getFranchiseData(sources)

        // Then
        assertEquals(5, franchises.size)

        assertEquals(2, franchises[0].timeline.size)
        assertEquals("Atlanta Disease Controllers", franchises[0].name)
        assertEquals(1960, franchises[0].firstSeason)
        assertEquals(1960, franchises[0].timeline[0].startSeason)
        assertEquals(1979, franchises[0].timeline[0].endSeason)
        assertEquals(1980, franchises[0].timeline[1].startSeason)
        assertEquals(MOST_RECENT_COMPLETED_MLB_SEASON, franchises[0].timeline[1].endSeason)
        assertEquals(League.NFL, franchises[0].league)

        assertEquals(1, franchises[1].timeline.size)
        assertEquals("Baltimore Wires", franchises[1].name)
        assertEquals(Metro.BALTIMORE, franchises[1].timeline[0].metroArea)
        assertEquals(League.NFL, franchises[1].league)

        assertEquals(2, franchises[2].timeline.size)
        assertEquals("Bellevue Nimbys", franchises[2].name)
        assertEquals(Metro.SEATTLE, franchises[2].timeline[0].metroArea)
        assertEquals(Metro.SEATTLE, franchises[2].timeline[1].metroArea)
        assertEquals(League.MLB, franchises[2].league)

        assertEquals(1, franchises[3].timeline.size)
        assertEquals("Buffalo Chips", franchises[3].name)
        assertEquals(Metro.BUFFALO, franchises[3].timeline[0].metroArea)
        assertEquals(League.NFL, franchises[3].league)

        assertEquals(1, franchises[4].timeline.size)
        assertEquals("Enumclaw Rednecks", franchises[4].name)
        assertEquals(Metro.SEATTLE, franchises[4].timeline[0].metroArea)
        assertEquals(League.MLB, franchises[4].league)
    }

    @Test
    fun `timelines are ordered correctly when merging franchises`() {
        // Given
        val sources = mapOf(
            League.NFL to listOf("test_franchises_nfl_new.json", "test_franchises_nfl_old.json")
        )

        // When
        val franchises = franchiseDataService.getFranchiseData(sources)

        // Then
        assertEquals(3, franchises.size)
        assertEquals(2, franchises[0].timeline.size)
        assertEquals(1960, franchises[0].timeline[0].startSeason)
        assertEquals(1980, franchises[0].timeline[1].startSeason)
    }
}