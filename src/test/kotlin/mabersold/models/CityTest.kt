package mabersold.models

import mabersold.`a city with an MLB franchise that has some seasons before divisional play`
import mabersold.`a city with an MLB franchise that played in a double season, and one other franchise`
import mabersold.`a city with an MLB franchise that played in a season with no postseason, and one other franchise`
import mabersold.`a city with no postseason history`
import mabersold.`a city with one franchise`
import mabersold.`a city with two franchises`
import mabersold.`a city with two generic franchises`
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CityTest {
    @Test
    fun `calculates totals correctly for city with one franchise`() {
        val city = `a city with one franchise`

        assertEquals(20, city.totalSeasons)
        assertEquals(1, city.totalChampionships)
        assertEquals(2, city.championshipAppearances)
        assertEquals(3, city.advancedInPlayoffs)
        assertEquals(4, city.playoffAppearances)
        assertEquals(4, city.bestInDivision)
        assertEquals(2, city.bestInConference)
        assertEquals(1, city.bestOverall)
        assertEquals(3, city.worstInDivision)
        assertEquals(2, city.worstInConference)
        assertEquals(1, city.worstOverall)
    }

    @Test
    fun `calculates averages correctly for city with one franchise`() {
        val city = `a city with one franchise`

        assertEquals((1.0 / 20), city.championshipsPerSeason)
        assertEquals((2.0 / 20), city.championshipAppearancesPerSeason)
        assertEquals((3.0 / 20), city.advancedInPlayoffsPerSeason)
        assertEquals((4.0 / 20), city.playoffAppearancesPerSeason)
        assertEquals((4.0 / 20), city.bestInDivisionPerSeason)
        assertEquals((2.0 / 20), city.bestInConferencePerSeason)
        assertEquals((1.0 / 20), city.bestOverallPerSeason)
        assertEquals((3.0 / 20), city.worstInDivisionPerSeason)
        assertEquals((2.0 / 20), city.worstInConferencePerSeason)
        assertEquals((1.0 / 20), city.worstOverallPerSeason)
        assertEquals((1.0 / 2), city.winningPercentageInFinals)
        assertEquals((2.0 / 4), city.reachingFinalsPerPlayoffAppearance)
        assertEquals((3.0 / 4), city.advancingInPlayoffsPerPlayoffAppearance)
    }

    @Test
    fun `calculates totals correctly for city with two franchises`() {
        val city = `a city with two franchises`

        assertEquals(40, city.totalSeasons)
        assertEquals(2, city.totalChampionships)
        assertEquals(3, city.championshipAppearances)
        assertEquals(4, city.advancedInPlayoffs)
        assertEquals(6, city.playoffAppearances)
        assertEquals(5, city.bestInDivision)
        assertEquals(3, city.bestInConference)
        assertEquals(2, city.bestOverall)
        assertEquals(6, city.worstInDivision)
        assertEquals(4, city.worstInConference)
        assertEquals(2, city.worstOverall)
    }

    @Test
    fun `calculates averages correctly for city with two franchises`() {
        val city = `a city with two generic franchises`

        assertEquals((2.0 / 40), city.championshipsPerSeason)
        assertEquals((3.0 / 40), city.championshipAppearancesPerSeason)
        assertEquals((4.0 / 40), city.advancedInPlayoffsPerSeason)
        assertEquals((6.0 / 40), city.playoffAppearancesPerSeason)
        assertEquals((5.0 / 40), city.bestInDivisionPerSeason)
        assertEquals((3.0 / 40), city.bestInConferencePerSeason)
        assertEquals((2.0 / 40), city.bestOverallPerSeason)
        assertEquals((6.0 / 40), city.worstInDivisionPerSeason)
        assertEquals((4.0 / 40), city.worstInConferencePerSeason)
        assertEquals((2.0 / 40), city.worstOverallPerSeason)
        assertEquals((2.0 / 3), city.winningPercentageInFinals)
        assertEquals((3.0 / 6), city.reachingFinalsPerPlayoffAppearance)
        assertEquals((4.0 / 6), city.advancingInPlayoffsPerPlayoffAppearance)
    }

    @Test
    fun `calculates totals correctly for city with two franchises that have some seasons before divisional play`() {
        val city = `a city with an MLB franchise that has some seasons before divisional play`

        assertEquals(20, city.totalSeasons)
        assertEquals(11, city.totalSeasonsWithDivisions)
        assertEquals(2, city.bestInDivision)
        assertEquals(2, city.worstInDivision)
        assertEquals(4, city.bestInConference)
        assertEquals(4, city.worstInConference)
        assertEquals(4, city.bestOverall)
        assertEquals(4, city.worstOverall)
        assertEquals((2.0 / 11), city.bestInDivisionPerSeason)
        assertEquals((2.0 / 11), city.worstInDivisionPerSeason)
    }

    @Test
    fun `calculates averages correctly for city with two franchises that include a double-season`() {
        val city = `a city with an MLB franchise that played in a double season, and one other franchise`

        assertEquals(20, city.totalSeasons)
        assertEquals(21, city.totalRegularSeasons)
        assertEquals((4.0 / 21), city.bestInDivisionPerSeason)
        assertEquals((4.0 / 21), city.bestInConferencePerSeason)
        assertEquals((4.0 / 21), city.bestOverallPerSeason)
        assertEquals((4.0 / 21), city.worstInDivisionPerSeason)
        assertEquals((4.0 / 21), city.worstInConferencePerSeason)
        assertEquals((4.0 / 21), city.worstOverallPerSeason)
    }

    @Test
    fun `calculates averages correctly for city with two franchises that include a season with no postseason`() {
        val city = `a city with an MLB franchise that played in a season with no postseason, and one other franchise`

        assertEquals(20, city.totalSeasons)
        assertEquals(19, city.totalPostSeasons)
        assertEquals((4.0 / 19), city.championshipsPerSeason)
        assertEquals((4.0 / 19), city.championshipAppearancesPerSeason)
        assertEquals((4.0 / 19), city.playoffAppearancesPerSeason)
        assertEquals((4.0 / 19), city.advancedInPlayoffsPerSeason)
    }

    @Test
    fun `per-postseason stats are null when city has no postseason history`() {
        val city = `a city with no postseason history`

        assertNull(city.winningPercentageInFinals)
        assertNull(city.reachingFinalsPerPlayoffAppearance)
        assertNull(city.advancingInPlayoffsPerPlayoffAppearance)
    }

    @Test
    fun `calculates averages correctly for city with two franchises that have some seasons before multi-round playoffs`() {
        val city = `a city with an MLB franchise that has some seasons before divisional play`

        assertEquals((1.0 /3), city.reachingFinalsPerPlayoffAppearance)
        assertEquals((2.0 / 3), city.advancingInPlayoffsPerPlayoffAppearance)
    }
}