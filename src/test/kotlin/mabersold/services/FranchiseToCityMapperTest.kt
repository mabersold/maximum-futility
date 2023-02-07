package mabersold.services

import mabersold.MOST_RECENT_COMPLETED_MLB_SEASON
import mabersold.`a franchise in Los Angeles`
import mabersold.`a franchise in New York`
import mabersold.`a franchise with timelines in different cities`
import mabersold.models.Metro
import kotlin.test.Test
import kotlin.test.assertEquals

class FranchiseToCityMapperTest {
    @Test
    fun `maps franchises to cities correctly`() {
        val franchises = listOf(`a franchise with timelines in different cities`)

        val mapper = FranchiseToCityMapper()

        val cities = mapper.mapToCities(franchises)

        assertEquals(2, cities.size)
        assertEquals(1, cities[0].franchises.size)
        assertEquals(1, cities[1].franchises.size)
        assertEquals(1900, cities[0].franchises[0].timeline[0].startSeason)
        assertEquals(1957, cities[0].franchises[0].timeline[0].endSeason)
        assertEquals(1958, cities[1].franchises[0].timeline[0].startSeason)
        assertEquals(MOST_RECENT_COMPLETED_MLB_SEASON, cities[1].franchises[0].timeline[0].endSeason)
    }

    @Test
    fun `maps multiple franchises correctly`() {
        val franchises = listOf(`a franchise with timelines in different cities`, `a franchise in New York`, `a franchise in Los Angeles`)

        val cities = FranchiseToCityMapper().mapToCities(franchises)

        assertEquals(2, cities.size)

        val newYork = cities.find { it.metroArea == Metro.NEW_YORK }!!
        assertEquals(2, newYork.franchises.size)
        val newsies = newYork.franchises.find { it.name == "New York Newsies" }!!
        assertEquals(1, newsies.timeline.size)
        assertEquals(1900, newsies.timeline.first().startSeason)
        val diversNewYork = newYork.franchises.find { it.name == "Brooklyn Divers" }!!
        assertEquals(1, diversNewYork.timeline.size)
        assertEquals(1900, diversNewYork.timeline.first().startSeason)
        assertEquals(1957, diversNewYork.timeline.first().endSeason)

        val losAngeles = cities.find { it.metroArea == Metro.LOS_ANGELES }!!
        assertEquals(2, losAngeles.franchises.size)
        val deco = losAngeles.franchises.find { it.name == "Los Angeles Art Deco Enthusiasts" }!!
        assertEquals(1, deco.timeline.size)
        assertEquals(1920, deco.timeline.first().startSeason)
        val diversLosAngeles = losAngeles.franchises.find { it.name == "Los Angeles Divers" }!!
        assertEquals(1, diversLosAngeles.timeline.size)
        assertEquals(1958, diversLosAngeles.timeline.first().startSeason)
        assertEquals(MOST_RECENT_COMPLETED_MLB_SEASON, diversLosAngeles.timeline.first().endSeason)
    }
}