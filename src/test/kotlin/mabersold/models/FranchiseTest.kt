package mabersold.models

import mabersold.MOST_RECENT_COMPLETED_MLB_SEASON
import mabersold.`a franchise with two timelines and data`
import mabersold.`a really old franchise`
import mabersold.`an MLB franchise that has one timeline that straddles the creation of divisions`
import kotlin.test.Test
import kotlin.test.assertEquals

class FranchiseTest {
    @Test
    fun `all data is correct`() {
        val franchise = `a franchise with two timelines and data`

        assertEquals(Metro.SEATTLE, franchise.metroArea)
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
        assertEquals((League.MLB.firstSeason..League.MLB.mostRecentFinishedSeason).toList().size, franchise.totalSeasons)
    }

    @Test
    fun `omits seasons without postseason in calculations`() {
        val franchise = `a franchise with two timelines and data`.withLeague(League.MLB)

        assertEquals(2.0 / 19, franchise.championshipsPerSeason)
        assertEquals(4.0 / 19, franchise.championshipAppearancesPerSeason)
        assertEquals(4.0 / 19, franchise.advancedInPlayoffsPerSeason)
        assertEquals(8.0 / 19, franchise.playoffAppearancesPerSeason)
    }

    @Test
    fun `includes extra seasons in calculations`() {
        val franchise = `a franchise with two timelines and data`.withLeague(League.MLB)

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

        assertEquals((5.0 / 15), franchise.bestInDivisionPerSeason)
        assertEquals((5.0 / 15), franchise.worstInDivisionPerSeason)
    }
}