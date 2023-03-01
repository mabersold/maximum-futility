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

        assertEquals(5, timeline.totalBestInDivisionInSeasonsWithDivisionalPlay)
        assertTrue(timeline.bestInDivisionInSeasonsWithDivisionalPlay.containsAll(listOf(1985, 1986, 1987, 1988, 1989)))

        assertEquals(3, timeline.bestInConference.size)
        assertTrue(timeline.bestInConference.containsAll(listOf(1985, 1987, 1989)))

        assertEquals(1, timeline.bestOverall.size)
        assertTrue(timeline.bestOverall.containsAll(listOf(1987)))

        assertEquals(4, timeline.totalWorstInDivisionInSeasonsWithDivisionalPlay)
        assertTrue(timeline.worstInDivisionInSeasonsWithDivisionalPlay.containsAll(listOf(1990, 1991, 1992, 1993)))

        assertEquals(3, timeline.worstInConference.size)
        assertTrue(timeline.worstInConference.containsAll(listOf(1991, 1992, 1993)))

        assertEquals(2, timeline.worstOverall.size)
        assertTrue(timeline.worstOverall.containsAll(listOf(1992, 1993)))
    }

    @Test
    fun `seasonsWithPlayoffs is same as total seasons for range without omissions`() {
        assertEquals(10, `a timeline from 1980-1989 with data`.totalPostSeasons)
    }

    @Test
    fun `seasonsWithPlayoffs is one less than total seasons for range that includes a season with no playoffs`() {
        assertEquals(9, `a timeline from 1990-1999 with no data`.totalPostSeasons)
    }

    @Test
    fun `totalRegularSeasons is correct for range without extra seasons`() {
        assertEquals(10, `a timeline from 1990-1999 with no data`.totalRegularSeasons)
    }

    @Test
    fun `totalRegularSeasons is correct for range with extra seasons`() {
        assertEquals(11, `a timeline from 1980-1989 with data`.totalRegularSeasons)
    }

    @Test
    fun `totalSeasonsWithDivisions is correct for range when all seasons have divisions`() {
        assertEquals(10, `a timeline from 1990-1999 with no data`.totalSeasonsWithDivisions)
    }

    @Test
    fun `totalSeasonsWithDivisions is correct for range when no seasons have divisions`() {
        assertEquals(0, `an old timeline that predates divisions`.totalSeasonsWithDivisions)
    }

    @Test
    fun `totalSeasonsWithDivisions is correct for range when some of the seasons have divisions`() {
        assertEquals(5, `a timeline that straddles the creation of divisions`.totalSeasonsWithDivisions)
    }

    @Test
    fun `bestInDivision is correct for range when all seasons have divisions`() {
        assertEquals(4, `a timeline from 1990-1999 with data`.totalBestInDivisionInSeasonsWithDivisionalPlay)
        assertTrue(`a timeline from 1990-1999 with data`.bestInDivisionInSeasonsWithDivisionalPlay.containsAll(listOf(1995, 1996, 1997, 1998)))
    }

    @Test
    fun `bestInDivision is correct for range when no seasons have divisions`() {
        assertEquals(0, `an old timeline that predates divisions`.totalBestInDivisionInSeasonsWithDivisionalPlay)
    }

    @Test
    fun `bestInDivision is correct for range when some seasons have divisions`() {
        assertEquals(1, `a timeline that straddles the creation of divisions`.totalBestInDivisionInSeasonsWithDivisionalPlay)
        assertTrue(`a timeline that straddles the creation of divisions`.bestInDivisionInSeasonsWithDivisionalPlay.containsAll(listOf(1973)))
    }

    @Test
    fun `worstInDivision is correct for range when all seasons have divisions`() {
        assertEquals(4, `a timeline from 1990-1999 with data`.totalWorstInDivisionInSeasonsWithDivisionalPlay)
        assertTrue(`a timeline from 1990-1999 with data`.worstInDivisionInSeasonsWithDivisionalPlay.containsAll(listOf(1990, 1991, 1992, 1993)))
    }

    @Test
    fun `worstInDivision is correct for range when no seasons have divisions`() {
        assertEquals(0, `an old timeline that predates divisions`.totalWorstInDivisionInSeasonsWithDivisionalPlay)
    }

    @Test
    fun `worstInDivision is correct for range when some seasons have divisions`() {
        assertEquals(1, `a timeline that straddles the creation of divisions`.totalWorstInDivisionInSeasonsWithDivisionalPlay)
        assertTrue(`a timeline that straddles the creation of divisions`.worstInDivisionInSeasonsWithDivisionalPlay.containsAll(listOf(1972)))
    }


    @Test
    fun `totalSeasonsWithMultiRoundPlayoffs is correct for range when all seasons have multi-round playoffs`() {
        assertEquals(10, `a timeline from 1990-1999 with no data`.totalSeasonsWithMultiRoundPlayoffs)
    }

    @Test
    fun `totalSeasonsWithMultiRoundPlayoffs is correct for range when no seasons have multi-round playoffs`() {
        assertEquals(0, `an old timeline that predates divisions`.totalSeasonsWithMultiRoundPlayoffs)
    }

    @Test
    fun `totalSeasonsWithMultiRoundPlayoffs is correct for range when some of the seasons have multi-round playoffs`() {
        assertEquals(5, `a timeline that straddles the creation of divisions`.totalSeasonsWithMultiRoundPlayoffs)
    }

    @Test
    fun `advancedInPlayoffs is correct for range when all seasons have multi-round playoffs`() {
        assertEquals(2, `a timeline from 1980-1989 with data`.totalAdvancedInPlayoffsInMultiRoundPlayoffSeasons)
        assertTrue(`a timeline from 1980-1989 with data`.advancedInPlayoffsInMultiRoundPlayoffSeasons.containsAll(listOf(1985, 1986)))
    }

    @Test
    fun `advancedInPlayoffs is correct for range when no seasons have multi-round playoffs`() {
        assertEquals(0, `an old timeline with championship appearances and advancing in playoffs`.totalAdvancedInPlayoffsInMultiRoundPlayoffSeasons)
    }

    @Test
    fun `advancedInPlayoffs is correct for range when some of the seasons have multi-round playoffs`() {
        assertEquals(2, `a timeline that straddles the creation of divisions`.totalAdvancedInPlayoffsInMultiRoundPlayoffSeasons)
        assertTrue(`a timeline that straddles the creation of divisions`.advancedInPlayoffsInMultiRoundPlayoffSeasons.containsAll(listOf(1971, 1973)))
    }

    @Test
    fun `totalChampionshipAppearancesInMultiRoundPlayoffYears is correct for range when all seasons have multi-round playoffs`() {
        assertEquals(2, `a timeline from 1980-1989 with data`.totalChampionshipAppearancesInMultiRoundPlayoffSeasons)
        assertTrue(`a timeline from 1980-1989 with data`.championshipAppearancesInMultiRoundPlayoffSeasons.containsAll(listOf(1985, 1986)))
    }

    @Test
    fun `totalChampionshipAppearancesInMultiRoundPlayoffYears is correct for range when no seasons have multi-round playoffs`() {
        assertEquals(0, `an old timeline with championship appearances and advancing in playoffs`.totalChampionshipAppearancesInMultiRoundPlayoffSeasons)
    }

    @Test
    fun `championshipAppearances is correct for range when some of the seasons have multi-round playoffs`() {
        assertEquals(1, `a timeline that straddles the creation of divisions`.totalChampionshipAppearancesInMultiRoundPlayoffSeasons)
        assertTrue(`a timeline that straddles the creation of divisions`.championshipAppearancesInMultiRoundPlayoffSeasons.containsAll(listOf(1973)))
    }

    @Test
    fun `playoffAppearances is correct for range when all seasons have multi-round playoffs`() {
        assertEquals(4, `a timeline from 1980-1989 with data`.totalPlayoffAppearancesInMultiRoundPlayoffSeasons)
        assertTrue(`a timeline from 1980-1989 with data`.playoffAppearancesInMultiRoundPlayoffSeasons.containsAll(listOf(1985, 1986, 1987, 1988)))
    }

    @Test
    fun `playoffAppearances is correct for range when no seasons have multi-round playoffs`() {
        assertEquals(0, `an old timeline with championship appearances and advancing in playoffs`.totalPlayoffAppearancesInMultiRoundPlayoffSeasons)
    }

    @Test
    fun `playoffAppearances is correct for range when some of the seasons have multi-round playoffs`() {
        assertEquals(3, `a timeline that straddles the creation of divisions`.totalPlayoffAppearancesInMultiRoundPlayoffSeasons)
        assertTrue(`a timeline that straddles the creation of divisions`.playoffAppearancesInMultiRoundPlayoffSeasons.containsAll(listOf(1970, 1971, 1973)))
    }
}