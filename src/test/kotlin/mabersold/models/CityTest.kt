package mabersold.models

import mabersold.`a city with an MLB franchise that has some seasons before divisional play`
import mabersold.`a city with an MLB franchise that played in a double season, and one other franchise`
import mabersold.`a city with an MLB franchise that played in a season with no postseason, and one other franchise`
import mabersold.`a city with no postseason history`
import mabersold.`a city with one franchise`
import mabersold.`a city with two franchises`
import mabersold.`a city with two franchises in the same league, and some championships`
import mabersold.`a city with two generic franchises`
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CityTest {
    @Test
    fun `calculates totals correctly for city with one franchise`() {
        val city = `a city with one franchise`

        assertEquals(20, city.totalSeasons)
        assertEquals(1, city.championships.total)
        assertEquals(2, city.championshipAppearances.total)
        assertEquals(3, city.advancedInPlayoffs.total)
        assertEquals(4, city.playoffAppearances.total)
        assertEquals(4, city.bestInDivision.total)
        assertEquals(2, city.bestInConference.total)
        assertEquals(1, city.bestOverall.total)
        assertEquals(3, city.worstInDivision.total)
        assertEquals(2, city.worstInConference.total)
        assertEquals(1, city.worstOverall.total)
    }

    @Test
    fun `calculates averages correctly for city with one franchise`() {
        val city = `a city with one franchise`

        assertEquals((1.0 / 20), city.championships.rate)
        assertEquals((2.0 / 20), city.championshipAppearances.rate)
        assertEquals((3.0 / 20), city.advancedInPlayoffs.rate)
        assertEquals((4.0 / 20), city.playoffAppearances.rate)
        assertEquals((4.0 / 20), city.bestInDivision.rate)
        assertEquals((2.0 / 20), city.bestInConference.rate)
        assertEquals((1.0 / 20), city.bestOverall.rate)
        assertEquals((3.0 / 20), city.worstInDivision.rate)
        assertEquals((2.0 / 20), city.worstInConference.rate)
        assertEquals((1.0 / 20), city.worstOverall.rate)
        assertEquals((1.0 / 2), city.successInFinals.rate)
        assertEquals((2.0 / 4), city.reachingFinalsPerPlayoffs.rate)
        assertEquals((3.0 / 4), city.advancingInPlayoffsPerPlayoffs.rate)
    }

    @Test
    fun `calculates totals correctly for city with two franchises`() {
        val city = `a city with two franchises`

        assertEquals(40, city.totalSeasons)
        assertEquals(2, city.championships.total)
        assertEquals(3, city.championshipAppearances.total)
        assertEquals(4, city.advancedInPlayoffs.total)
        assertEquals(6, city.playoffAppearances.total)
        assertEquals(5, city.bestInDivision.total)
        assertEquals(3, city.bestInConference.total)
        assertEquals(2, city.bestOverall.total)
        assertEquals(6, city.worstInDivision.total)
        assertEquals(4, city.worstInConference.total)
        assertEquals(2, city.worstOverall.total)
    }

    @Test
    fun `calculates averages correctly for city with two franchises`() {
        val city = `a city with two generic franchises`

        assertEquals((2.0 / 40), city.championships.rate)
        assertEquals((3.0 / 40), city.championshipAppearances.rate)
        assertEquals((4.0 / 40), city.advancedInPlayoffs.rate)
        assertEquals((6.0 / 40), city.playoffAppearances.rate)
        assertEquals((5.0 / 40), city.bestInDivision.rate)
        assertEquals((3.0 / 40), city.bestInConference.rate)
        assertEquals((2.0 / 40), city.bestOverall.rate)
        assertEquals((6.0 / 40), city.worstInDivision.rate)
        assertEquals((4.0 / 40), city.worstInConference.rate)
        assertEquals((2.0 / 40), city.worstOverall.rate)
        assertEquals((2.0 / 3), city.successInFinals.rate)
        assertEquals((3.0 / 6), city.reachingFinalsPerPlayoffs.rate)
        assertEquals((4.0 / 6), city.advancingInPlayoffsPerPlayoffs.rate)
    }

    @Test
    fun `calculates totals correctly for city with two franchises that have some seasons before divisional play`() {
        val city = `a city with an MLB franchise that has some seasons before divisional play`

        assertEquals(20, city.totalSeasons)
        assertEquals(11, city.totalSeasonsWithDivisions)
        assertEquals(2, city.bestInDivision.total)
        assertEquals(2, city.worstInDivision.total)
        assertEquals(4, city.bestInConference.total)
        assertEquals(4, city.worstInConference.total)
        assertEquals(4, city.bestOverall.total)
        assertEquals(4, city.worstOverall.total)
        assertEquals((2.0 / 11), city.bestInDivision.rate)
        assertEquals((2.0 / 11), city.worstInDivision.rate)
    }

    @Test
    fun `calculates averages correctly for city with two franchises that include a double-season`() {
        val city = `a city with an MLB franchise that played in a double season, and one other franchise`

        assertEquals(20, city.totalSeasons)
        assertEquals(21, city.totalRegularSeasons)
        assertEquals((4.0 / 21), city.bestInDivision.rate)
        assertEquals((4.0 / 21), city.bestInConference.rate)
        assertEquals((4.0 / 21), city.bestOverall.rate)
        assertEquals((4.0 / 21), city.worstInDivision.rate)
        assertEquals((4.0 / 21), city.worstInConference.rate)
        assertEquals((4.0 / 21), city.worstOverall.rate)
    }

    @Test
    fun `calculates averages correctly for city with two franchises that include a season with no postseason`() {
        val city = `a city with an MLB franchise that played in a season with no postseason, and one other franchise`

        assertEquals(20, city.totalSeasons)
        assertEquals(19, city.totalPostSeasons)
        assertEquals((4.0 / 19), city.championships.rate)
        assertEquals((4.0 / 19), city.championshipAppearances.rate)
        assertEquals((4.0 / 19), city.playoffAppearances.rate)
        assertEquals((4.0 / 19), city.advancedInPlayoffs.rate)
    }

    @Test
    fun `per-postseason stats are null when city has no postseason history`() {
        val city = `a city with no postseason history`

        assertNull(city.successInFinals.rate)
        assertNull(city.reachingFinalsPerPlayoffs.rate)
        assertNull(city.advancingInPlayoffsPerPlayoffs.rate)
    }

    @Test
    fun `calculates averages correctly for city with two franchises that have some seasons before multi-round playoffs`() {
        val city = `a city with an MLB franchise that has some seasons before divisional play`

        assertEquals((1.0 / 3), city.reachingFinalsPerPlayoffs.rate)
        assertEquals((2.0 / 3), city.advancingInPlayoffsPerPlayoffs.rate)
    }

    @Test
    fun `applies discount to a city with multiple franchises in the same league for championships`() {
        val city = `a city with two franchises in the same league, and some championships`

        assertEquals((2.0 / 18), city.championships.rate)
    }
}