package mabersold.models

import mabersold.`a timeline from 1980-1989 with data`
import mabersold.`a timeline from 1990-1999 with no data`
import mabersold.`a timeline that is ready to be trimmed`
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FranchiseTimelineTest {
    @Test
    fun `total seasons is correct`() {
        assertEquals(10, `a timeline from 1980-1989 with data`.totalSeasons)
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

    @Test
    fun `seasonsWithPlayoffs is same as total seasons for range without omissions`() {
        assertEquals(10, `a timeline from 1980-1989 with data`.totalPostSeasons(League.MLB))
    }

    @Test
    fun `seasonsWithPlayoffs is one less than total seasons for range that includes a season with no playoffs`() {
        assertEquals(9, `a timeline from 1990-1999 with no data`.totalPostSeasons(League.MLB))
    }

    @Test
    fun `seasonsWithPlayoffs is same as total seasons when no league provided`() {
        assertEquals(10, `a timeline from 1990-1999 with no data`.totalPostSeasons())
    }

    @Test
    fun `totalRegularSeasons is correct for range without extra seasons`() {
        assertEquals(10, `a timeline from 1990-1999 with no data`.totalRegularSeasons(League.MLB))
    }

    @Test
    fun `totalRegularSeasons is correct for range with extra seasons`() {
        assertEquals(11, `a timeline from 1980-1989 with data`.totalRegularSeasons(League.MLB))
    }

    @Test
    fun `totalRegularSeasons is correct when no league provided`() {
        assertEquals(10, `a timeline from 1980-1989 with data`.totalRegularSeasons())
    }
}