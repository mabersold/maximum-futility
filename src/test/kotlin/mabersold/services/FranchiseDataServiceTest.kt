package mabersold.services

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
}