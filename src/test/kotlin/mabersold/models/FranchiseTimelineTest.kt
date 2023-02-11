package mabersold.models

import mabersold.`a timeline from 1980-1989 with data`
import mabersold.`a timeline from 1990-1999 with data`
import mabersold.`a timeline from 1990-1999 with no data`
import mabersold.`a timeline that is ready to be trimmed`
import mabersold.`an old timeline that predates divisions`
import mabersold.`a timeline that straddles the creation of divisions`
import mabersold.`an old timeline with championship appearances and advancing in playoffs`
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

    @Test
    fun `totalSeasonsWithDivisions is correct for range when all seasons have divisions`() {
        assertEquals(10, `a timeline from 1990-1999 with no data`.totalSeasonsWithDivisions(League.MLB))
    }

    @Test
    fun `totalSeasonsWithDivisions is correct for range when no seasons have divisions`() {
        assertEquals(0, `an old timeline that predates divisions`.totalSeasonsWithDivisions(League.MLB))
    }

    @Test
    fun `totalSeasonsWithDivisions is correct for range when some of the seasons have divisions`() {
        assertEquals(5, `a timeline that straddles the creation of divisions`.totalSeasonsWithDivisions(League.MLB))
    }

    @Test
    fun `totalSeasonsWithDivisions is correct with no league applied`() {
        assertEquals(10, `a timeline that straddles the creation of divisions`.totalSeasonsWithDivisions())
    }

    @Test
    fun `bestInDivision is correct for range when all seasons have divisions`() {
        assertEquals(4, `a timeline from 1990-1999 with data`.bestInDivision(League.MLB).size)
        assertTrue(`a timeline from 1990-1999 with data`.bestInDivision(League.MLB).containsAll(listOf(1995, 1996, 1997, 1998)))
    }

    @Test
    fun `bestInDivision is correct for range when no seasons have divisions`() {
        assertEquals(0, `an old timeline that predates divisions`.bestInDivision(League.MLB).size)
    }

    @Test
    fun `bestInDivision is correct for range when some seasons have divisions`() {
        assertEquals(1, `a timeline that straddles the creation of divisions`.bestInDivision(League.MLB).size)
        assertTrue(`a timeline that straddles the creation of divisions`.bestInDivision(League.MLB).containsAll(listOf(1973)))
    }

    @Test
    fun `bestInDivision is correct when no league applied`() {
        assertEquals(2, `a timeline that straddles the creation of divisions`.bestInDivision().size)
        assertTrue(`a timeline that straddles the creation of divisions`.bestInDivision().containsAll(listOf(1964, 1973)))
    }

    @Test
    fun `worstInDivision is correct for range when all seasons have divisions`() {
        assertEquals(4, `a timeline from 1990-1999 with data`.worstInDivision(League.MLB).size)
        assertTrue(`a timeline from 1990-1999 with data`.worstInDivision(League.MLB).containsAll(listOf(1990, 1991, 1992, 1993)))
    }

    @Test
    fun `worstInDivision is correct for range when no seasons have divisions`() {
        assertEquals(0, `an old timeline that predates divisions`.worstInDivision(League.MLB).size)
    }

    @Test
    fun `worstInDivision is correct for range when some seasons have divisions`() {
        assertEquals(1, `a timeline that straddles the creation of divisions`.worstInDivision(League.MLB).size)
        assertTrue(`a timeline that straddles the creation of divisions`.worstInDivision(League.MLB).containsAll(listOf(1972)))
    }

    @Test
    fun `worstInDivision is correct when no league applied`() {
        assertEquals(2, `a timeline that straddles the creation of divisions`.worstInDivision().size)
        assertTrue(`a timeline that straddles the creation of divisions`.worstInDivision().containsAll(listOf(1965, 1972)))
    }

    @Test
    fun `totalSeasonsWithMultiRoundPlayoffs is correct for range when all seasons have multi-round playoffs`() {
        assertEquals(10, `a timeline from 1990-1999 with no data`.seasonsWithMultiRoundPlayoffs(League.MLB))
    }

    @Test
    fun `totalSeasonsWithMultiRoundPlayoffs is correct for range when no seasons have multi-round playoffs`() {
        assertEquals(0, `an old timeline that predates divisions`.seasonsWithMultiRoundPlayoffs(League.MLB))
    }

    @Test
    fun `totalSeasonsWithMultiRoundPlayoffs is correct for range when some of the seasons have multi-round playoffs`() {
        assertEquals(5, `a timeline that straddles the creation of divisions`.seasonsWithMultiRoundPlayoffs(League.MLB))
    }

    @Test
    fun `totalSeasonsWithMultiRoundPlayoffs is correct with no league applied`() {
        assertEquals(10, `a timeline that straddles the creation of divisions`.seasonsWithMultiRoundPlayoffs())
    }

    @Test
    fun `advancedInPlayoffs is correct for range when all seasons have multi-round playoffs`() {
        assertEquals(2, `a timeline from 1980-1989 with data`.advancedInPlayoffs(League.MLB).size)
        assertTrue(`a timeline from 1980-1989 with data`.advancedInPlayoffs(League.MLB).containsAll(listOf(1985, 1986)))
    }

    @Test
    fun `advancedInPlayoffs is correct for range when no seasons have multi-round playoffs`() {
        assertEquals(0, `an old timeline with championship appearances and advancing in playoffs`.advancedInPlayoffs(League.MLB).size)
    }

    @Test
    fun `advancedInPlayoffs is correct for range when some of the seasons have multi-round playoffs`() {
        assertEquals(2, `a timeline that straddles the creation of divisions`.advancedInPlayoffs(League.MLB).size)
        assertTrue(`a timeline that straddles the creation of divisions`.advancedInPlayoffs(League.MLB).containsAll(listOf(1971, 1973)))
    }

    @Test
    fun `advancedInPlayoffs is correct with no league applied`() {
        assertEquals(4, `a timeline that straddles the creation of divisions`.advancedInPlayoffs().size)
        assertTrue(`a timeline that straddles the creation of divisions`.advancedInPlayoffs().containsAll(listOf(1967, 1968, 1971, 1973)))
    }

    @Test
    fun `championshipAppearances is correct for range when all seasons have multi-round playoffs`() {
        assertEquals(2, `a timeline from 1980-1989 with data`.championshipAppearances(League.MLB).size)
        assertTrue(`a timeline from 1980-1989 with data`.championshipAppearances(League.MLB).containsAll(listOf(1985, 1986)))
    }

    @Test
    fun `championshipAppearances is correct for range when no seasons have multi-round playoffs`() {
        assertEquals(0, `an old timeline with championship appearances and advancing in playoffs`.championshipAppearances(League.MLB).size)
    }

    @Test
    fun `championshipAppearances is correct for range when some of the seasons have multi-round playoffs`() {
        assertEquals(1, `a timeline that straddles the creation of divisions`.championshipAppearances(League.MLB).size)
        assertTrue(`a timeline that straddles the creation of divisions`.championshipAppearances(League.MLB).containsAll(listOf(1973)))
    }

    @Test
    fun `championshipAppearances is correct with no league applied`() {
        assertEquals(2, `a timeline that straddles the creation of divisions`.championshipAppearances().size)
        assertTrue(`a timeline that straddles the creation of divisions`.championshipAppearances().containsAll(listOf(1964, 1973)))
    }

    @Test
    fun `playoffAppearances is correct for range when all seasons have multi-round playoffs`() {
        assertEquals(4, `a timeline from 1980-1989 with data`.playoffAppearances(League.MLB).size)
        assertTrue(`a timeline from 1980-1989 with data`.playoffAppearances(League.MLB).containsAll(listOf(1985, 1986, 1987, 1988)))
    }

    @Test
    fun `playoffAppearances is correct for range when no seasons have multi-round playoffs`() {
        assertEquals(0, `an old timeline with championship appearances and advancing in playoffs`.playoffAppearances(League.MLB).size)
    }

    @Test
    fun `playoffAppearances is correct for range when some of the seasons have multi-round playoffs`() {
        assertEquals(3, `a timeline that straddles the creation of divisions`.playoffAppearances(League.MLB).size)
        assertTrue(`a timeline that straddles the creation of divisions`.playoffAppearances(League.MLB).containsAll(listOf(1970, 1971, 1973)))
    }

    @Test
    fun `playoffAppearances is correct with no league applied`() {
        assertEquals(6, `a timeline that straddles the creation of divisions`.playoffAppearances().size)
        assertTrue(`a timeline that straddles the creation of divisions`.playoffAppearances().containsAll(listOf(1964, 1967, 1968, 1970, 1971, 1973)))
    }
}