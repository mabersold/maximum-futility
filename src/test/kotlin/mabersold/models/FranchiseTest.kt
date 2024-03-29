package mabersold.models

import mabersold.MOST_RECENT_COMPLETED_MLB_SEASON
import mabersold.`a franchise in New York`
import mabersold.`a franchise that spans the start of multi-round playoffs`
import mabersold.`a franchise with two timelines and data`
import mabersold.`a generic franchise with two timelines and data`
import mabersold.`a really old franchise`
import mabersold.`an MLB franchise that has one timeline that straddles the creation of divisions`
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FranchiseTest {
    @Test
    fun `all data is correct`() {
        val franchise = `a generic franchise with two timelines and data`

        assertEquals(Metro.GRAND_RAPIDS, franchise.metroArea)
        assertEquals(20, franchise.totalSeasons)
        assertEquals(2, franchise.totalChampionships)
        assertEquals(4, franchise.championshipAppearances)
        assertEquals(4, franchise.advancedInPlayoffs)
        assertEquals(8, franchise.playoffAppearances)
        assertEquals(8, franchise.bestInDivision)
        assertEquals(4, franchise.bestInConference)
        assertEquals(2, franchise.bestOverall)
        assertEquals(8, franchise.worstInDivision)
        assertEquals(4, franchise.worstInConference)
        assertEquals(2, franchise.worstOverall)
        assertEquals(0.1, franchise.championshipsPerSeason)
        assertEquals(0.2, franchise.championshipAppearancesPerSeason)
        assertEquals(0.2, franchise.advancedInPlayoffsPerSeason)
        assertEquals(0.4, franchise.playoffAppearancesPerSeason)
        assertEquals(0.4, franchise.bestInDivisionPerSeason)
        assertEquals(0.2, franchise.bestInConferencePerSeason)
        assertEquals(0.1, franchise.bestOverallPerSeason)
        assertEquals(0.4, franchise.worstInDivisionPerSeason)
        assertEquals(0.2, franchise.worstInConferencePerSeason)
        assertEquals(0.1, franchise.worstOverallPerSeason)
    }

    @Test
    fun `within excludes any timelines that are outside of the range`() {
        val franchise = `a really old franchise`.within(1885, MOST_RECENT_COMPLETED_MLB_SEASON)

        assertEquals(2, franchise.timeline.size)
        assertEquals(1885, franchise.timeline.first().startSeason)
        assertEquals(1921, franchise.timeline.last().startSeason)
    }

    @Test
    fun `within excludes any timelines that are outside of an even smaller range`() {
        val franchise = `a really old franchise`.within(1885, 1887)

        assertEquals(1, franchise.timeline.size)
        assertEquals(1885, franchise.timeline.first().startSeason)
    }

    @Test
    fun `within trims results data outside of the range`() {
        val franchise = `a really old franchise`.within(1875, 1940)

        assertEquals(3, franchise.timeline.size)
        assertEquals(66, franchise.totalSeasons)
        assertEquals(5, franchise.totalChampionships)
        assertEquals(6, franchise.championshipAppearances)
        assertEquals(5, franchise.advancedInPlayoffs)
        assertEquals(6, franchise.playoffAppearances)
        assertEquals(6, franchise.bestInDivision)
        assertEquals(6, franchise.bestInConference)
        assertEquals(4, franchise.bestOverall)
        assertEquals(6, franchise.worstInDivision)
        assertEquals(6, franchise.worstInConference)
        assertEquals(4, franchise.worstOverall)
        assertEquals((5.0 / 66), franchise.championshipsPerSeason)
        assertEquals((6.0 / 66), franchise.championshipAppearancesPerSeason)
        assertEquals((5.0 / 66), franchise.advancedInPlayoffsPerSeason)
        assertEquals((6.0 / 66), franchise.playoffAppearancesPerSeason)
        assertEquals((6.0 / 66), franchise.bestInDivisionPerSeason)
        assertEquals((6.0 / 66), franchise.bestInConferencePerSeason)
        assertEquals((4.0 / 66), franchise.bestOverallPerSeason)
        assertEquals((6.0 / 66), franchise.worstInDivisionPerSeason)
        assertEquals((6.0 / 66), franchise.worstInConferencePerSeason)
        assertEquals((4.0 / 66), franchise.worstOverallPerSeason)
    }

    @Test
    fun `applies league correctly`() {
        val franchise = `a really old franchise`.withLeague(League.MLB)

        assertEquals(League.MLB, franchise.league)
        assertEquals((League.MLB.firstSeason..2022).toList().size, franchise.totalSeasons)
    }

    @Test
    fun `omits seasons without postseason in calculations`() {
        val franchise = `a franchise with two timelines and data`.withLeague(League.MLB)

        assertEquals(19, franchise.totalPostSeasons)
        assertEquals(2.0 / 19, franchise.championshipsPerSeason)
        assertEquals(4.0 / 19, franchise.championshipAppearancesPerSeason)
        assertEquals(4.0 / 19, franchise.advancedInPlayoffsPerSeason)
        assertEquals(8.0 / 19, franchise.playoffAppearancesPerSeason)
    }

    @Test
    fun `includes extra seasons in calculations`() {
        val franchise = `a franchise with two timelines and data`.withLeague(League.MLB)

        assertEquals(21, franchise.totalRegularSeasons)
        assertEquals((8.0 / 21), franchise.bestInDivisionPerSeason)
        assertEquals((4.0 / 21), franchise.bestInConferencePerSeason)
        assertEquals((2.0 / 21), franchise.bestOverallPerSeason)
        assertEquals((8.0 / 21), franchise.worstInDivisionPerSeason)
        assertEquals((4.0 / 21), franchise.worstInConferencePerSeason)
        assertEquals((2.0 / 21), franchise.worstOverallPerSeason)
    }

    @Test
    fun `omits divisional status from before divisions existed`() {
        val franchise = `an MLB franchise that has one timeline that straddles the creation of divisions`

        assertEquals(15, franchise.totalSeasonsWithDivisions)
        assertEquals((5.0 / 15), franchise.bestInDivisionPerSeason)
        assertEquals((5.0 / 15), franchise.worstInDivisionPerSeason)
    }

    @Test
    fun `winning percentage in finals is correct for franchise that has reached finals`() {
        val franchise = `a franchise with two timelines and data`

        assertEquals((2.0 / 4), franchise.winningPercentageInFinals)
    }

    @Test
    fun `winning percentage in finals is null for franchise that has never reached finals`() {
        val franchise = `a franchise in New York`

        assertNull(franchise.winningPercentageInFinals)
    }

    @Test
    fun `reaching finals per playoff appearance is correct for franchise that has reached finals`() {
        val franchise = `a franchise with two timelines and data`

        assertEquals((4.0 / 8), franchise.reachingFinalsPerPlayoffAppearance)
    }

    @Test
    fun `reaching finals per playoff appearance is null for franchise that does not have playoff appearances`() {
        val franchise = `a franchise in New York`

        assertNull(franchise.reachingFinalsPerPlayoffAppearance)
    }

    @Test
    fun `advancing in playoffs per playoff appearance is correct for franchise that has playoff appearances`() {
        val franchise = `a franchise with two timelines and data`

        assertEquals((4.0 / 8), franchise.advancingInPlayoffsPerPlayoffAppearance)
    }

    @Test
    fun `advancing in playoffs per playoff appearance is null for franchise that does not have playoff appearances`() {
        val franchise = `a franchise in New York`

        assertNull(franchise.advancingInPlayoffsPerPlayoffAppearance)
    }

    @Test
    fun `playoff appearance stats are correct for years before and after multi-round playoffs`() {
        val franchise = `an MLB franchise that has one timeline that straddles the creation of divisions`

        assertEquals((4.0 / 7), franchise.advancingInPlayoffsPerPlayoffAppearance)
        assertEquals((3.0 / 7), franchise.reachingFinalsPerPlayoffAppearance)
    }

    @Test
    fun `calculates multi-round playoff stat totals correctly`() {
        val franchise = `a franchise that spans the start of multi-round playoffs`

        assertEquals(3, franchise.championshipAppearancesInMultiRoundPlayoffYears)
        assertEquals(6, franchise.championshipAppearances)
        assertEquals(4, franchise.advancedInPlayoffsInMultiRoundPlayoffYears)
        assertEquals(8, franchise.advancedInPlayoffs)
        assertEquals(5, franchise.playoffAppearancesInMultiRoundPlayoffYears)
        assertEquals(10, franchise.playoffAppearances)
    }

    @Test
    fun `determines whether a franchise was active in a given season or not`() {
        val franchise = `a franchise with two timelines and data`

        assertFalse(franchise.playedInSeason(1970))
        assertTrue(franchise.playedInSeason(1985))
        assertFalse(franchise.playedInSeason(2005))
    }

    @Test
    fun `isWithin evaluates correctly`() {
        val franchise = `a franchise with two timelines and data`

        assertTrue(franchise.isWithin(1980, 1999))
        assertTrue(franchise.isWithin(1975, 1985))
        assertTrue(franchise.isWithin(1995, 2005))
        assertFalse(franchise.isWithin(1970, 1975))
        assertFalse(franchise.isWithin(2005, 2010))
    }
}