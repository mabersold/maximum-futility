package mabersold.models

import mabersold.`a timeline from 1980-1989 with data`
import mabersold.`a timeline from 1980-present with no data`
import mabersold.`a timeline that is ready to be trimmed`
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FranchiseTimelineTest {
    @Test
    fun `total seasons is correct`() {
        assertEquals(10, `a timeline from 1980-1989 with data`.totalSeasons)
    }

    @Test
    fun `championships per season is correct`() {
        assertEquals(0.1, `a timeline from 1980-1989 with data`.championshipsPerSeason)
    }

    @Test
    fun `championship appearances per season is correct`() {
        assertEquals(0.2, `a timeline from 1980-1989 with data`.championshipAppearancesPerSeason)
    }

    @Test
    fun `advanced in playoffs per season is correct`() {
        assertEquals(0.2, `a timeline from 1980-1989 with data`.advancedInPlayoffsPerSeason)
    }

    @Test
    fun `playoff appearances per season is correct`() {
        assertEquals(0.4, `a timeline from 1980-1989 with data`.playoffAppearancesPerSeason)
    }

    @Test
    fun `best in division per season is correct`() {
        assertEquals(0.4, `a timeline from 1980-1989 with data`.bestInDivisionPerSeason)
    }

    @Test
    fun `best in conference per season is correct`() {
        assertEquals(0.2, `a timeline from 1980-1989 with data`.bestInConferencePerSeason)
    }

    @Test
    fun `best overall per season is correct`() {
        assertEquals(0.1, `a timeline from 1980-1989 with data`.bestOverallPerSeason)
    }

    @Test
    fun `worst in division per season is correct`() {
        assertEquals(0.4, `a timeline from 1980-1989 with data`.worstInDivisionPerSeason)
    }

    @Test
    fun `worst in conference per season is correct`() {
        assertEquals(0.2, `a timeline from 1980-1989 with data`.worstInConferencePerSeason)
    }

    @Test
    fun `worst overall per season is correct`() {
        assertEquals(0.1, `a timeline from 1980-1989 with data`.worstOverallPerSeason)
    }

    @Test
    fun `isBefore returns true if timeline ends before given year`() {
        assertTrue(`a timeline from 1980-1989 with data`.isBefore(1990))
    }

    @Test
    fun `isBefore returns false if timeline ends after given year`() {
        assertFalse(`a timeline from 1980-1989 with data`.isBefore(1970))
    }

    @Test
    fun `isBefore returns false if timeline has no end year`() {
        assertFalse(`a timeline from 1980-present with no data`.isBefore(1990))
    }

    @Test
    fun `isWithin returns false if the timeline is entirely after the specified range`() {
        assertFalse(`a timeline from 1980-1989 with data`.isWithin(1970, 1979))
    }

    @Test
    fun `isWithin returns false if the timeline is entirely before the specified range`() {
        assertFalse(`a timeline from 1980-1989 with data`.isWithin(1995, 1998))
    }

    @Test
    fun `isWithin returns true if the timeline is entirely within the specified range`() {
        assertTrue(`a timeline from 1980-1989 with data`.isWithin(1982, 1988))
    }

    @Test
    fun `isWithin returns true if the end of the timeline is within the specified range`() {
        assertTrue(`a timeline from 1980-1989 with data`.isWithin(1985, 1995))
    }

    @Test
    fun `isWithin returns true if the front of the timeline is within the specified range`() {
        assertTrue(`a timeline from 1980-1989 with data`.isWithin(1970, 1980))
    }

    @Test
    fun `isWithin returns true if the timeline has no endSeason and `() {
        assertTrue(`a timeline from 1980-1989 with data`.isWithin(1970, 1980))
    }

    @Test
    fun `trim removes all data outside of range`() {
        val timeline = `a timeline that is ready to be trimmed`.trim(1985, 1994)
        assertEquals(10, timeline.totalSeasons)
        assertEquals(1985, timeline.startSeason)
        assertEquals(1994, timeline.endSeason)

        assertEquals(2, timeline.championships.size)
        assertTrue(timeline.championships.containsAll(listOf(1988, 1989)))

        assertEquals(3, timeline.championshipAppearances.size)
        assertTrue(timeline.championshipAppearances.containsAll(listOf(1987, 1988, 1989)))

        assertEquals(4, timeline.advancedInPlayoffs.size)
        assertTrue(timeline.advancedInPlayoffs.containsAll(listOf(1986, 1987, 1988, 1989)))

        assertEquals(5, timeline.playoffAppearances.size)
        assertTrue(timeline.playoffAppearances.containsAll(listOf(1985, 1986, 1987, 1988, 1989)))

        assertEquals(5, timeline.bestInDivision.size)
        assertTrue(timeline.bestInDivision.containsAll(listOf(1985, 1986, 1987, 1988, 1989)))

        assertEquals(3, timeline.bestInConference.size)
        assertTrue(timeline.bestInConference.containsAll(listOf(1985, 1987, 1989)))

        assertEquals(1, timeline.bestOverall.size)
        assertTrue(timeline.bestOverall.containsAll(listOf(1987)))

        assertEquals(4, timeline.worstInDivision.size)
        assertTrue(timeline.worstInDivision.containsAll(listOf(1990, 1991, 1992, 1993)))

        assertEquals(3, timeline.worstInConference.size)
        assertTrue(timeline.worstInConference.containsAll(listOf(1991, 1992, 1993)))

        assertEquals(2, timeline.worstOverall.size)
        assertTrue(timeline.worstOverall.containsAll(listOf(1992, 1993)))
    }
}