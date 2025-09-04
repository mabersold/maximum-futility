package mabersold.functions

import io.ktor.server.plugins.BadRequestException
import kotlinx.serialization.json.Json
import mabersold.models.api.requests.CreateSeasonRequest
import mabersold.models.db.Chapter
import mabersold.models.db.Standing
import mabersold.models.intermediary.ProtoFranchiseSeason
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PrepareFranchiseSeasonsTest {
    val chapters = listOf(
        Chapter(1, "Team 1", 1, 7, 6, 1900, null, null, null),
        Chapter(2, "Team 2", 2, 8, 6, 1900, null, null, null),
        Chapter(3, "Team 3", 3, 9, 6, 1900, null, null, null),
        Chapter(4, "Team 4", 4, 10, 6, 1900, null, null, null),
        Chapter(5, "Team 5", 5, 11, 6, 1900, null, null, null),
        Chapter(6, "Team 6", 6, 12, 6, 1900, null, null, null),
        Chapter(7, "Team 7", 7, 13, 6, 1900, null, null, null),
        Chapter(8, "Team 8", 8, 7, 6, 1900, null, null, null),
        Chapter(9, "Team 9", 9, 7, 6, 1900, null, null, null),
        Chapter(10, "Team 10", 10, 15, 6, 1900, null, null, null),
        Chapter(11, "Team 11", 11, 16, 6, 1900, null, null, null),
        Chapter(12, "Team 12", 12, 17, 6, 1900, null, null, null),
        Chapter(13, "Team 13", 13, 18, 6, 1900, null, null, null),
        Chapter(14, "Team 14", 14, 19, 6, 1900, null, null, null),
        Chapter(15, "Team 15", 15, 20, 6, 1900, null, null, null),
        Chapter(16, "Team 16", 16, 7, 6, 1900, null, null, null),
        Chapter(17, "Team 17-18", 17, 25, 6, 2000, null, null, null),
        Chapter(18, "Team 17-18", 18, 26, 6, 2000, null, null, null)
    )
    @Test
    fun `throws an error if chapters list is empty`() {
        // Arrange
        val request = getRequest("basic-season")

        // Act
        val exception = assertFailsWith<BadRequestException> {
            prepareFranchiseSeasons(1, request, listOf())
        }

        // Assert
        assertEquals("No chapters available", exception.message)
    }

    @Test
    fun `returns a valid list for a simple season`() {
        // Arrange
        val request = getRequest("basic-season")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(4, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, Standing.FIRST)
        result[1].assertMatches("Team 2", 8, 33, 2, null)
        result[2].assertMatches("Team 3", 9, 33, 3, null)
        result[3].assertMatches("Team 4", 10, 33, 4, Standing.LAST)
    }

    @Test
    fun `returns a valid list for a simple season with tie for best overall`() {
        // Arrange
        val request = getRequest("basic-season-tied-best")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(4, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, Standing.FIRST_TIED)
        result[1].assertMatches("Team 2", 8, 33, 2, Standing.FIRST_TIED)
        result[2].assertMatches("Team 3", 9, 33, 3, null)
        result[3].assertMatches("Team 4", 10, 33, 4, Standing.LAST)
    }

    @Test
    fun `returns a valid list for a simple season with tie for last overall`() {
        // Arrange
        val request = getRequest("basic-season-tied-worst")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(4, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, Standing.FIRST)
        result[1].assertMatches("Team 2", 8, 33, 2, null)
        result[2].assertMatches("Team 3", 9, 33, 3, Standing.LAST_TIED)
        result[3].assertMatches("Team 4", 10, 33, 4, Standing.LAST_TIED)
    }

    @Test
    fun `returns a valid list for a simple season with ties both ways`() {
        // Arrange
        val request = getRequest("basic-season-tied-both-ways")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(4, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, Standing.FIRST_TIED)
        result[1].assertMatches("Team 2", 8, 33, 2, Standing.FIRST_TIED)
        result[2].assertMatches("Team 3", 9, 33, 3, Standing.LAST_TIED)
        result[3].assertMatches("Team 4", 10, 33, 4, Standing.LAST_TIED)
    }

    @Test
    fun `returns a valid list for a simple season with a merged franchise`() {
        // Arrange
        val request = getRequest("basic-season-merged-franchise")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(4, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, Standing.FIRST_TIED)
        result[1].assertMatches("Team 2", 8, 33, 2, Standing.FIRST_TIED)
        result[2].assertMatches("Team 17-18", 25, 33, 17, Standing.LAST)
        result[3].assertMatches("Team 17-18", 26, 33, 18, Standing.LAST)
    }

    @Test
    fun `returns a valid list for a simple season with a playoff`() {
        // Arrange
        val request = getRequest("basic-season-simple-playoff")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(4, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, Standing.FIRST, true, 1, true, true)
        result[1].assertMatches("Team 2", 8, 33, 2, null, true, 0, true, false)
        result[2].assertMatches("Team 3", 9, 33, 3, null, false)
        result[3].assertMatches("Team 4", 10, 33, 4, Standing.LAST, false)
    }

    @Test
    fun `returns a valid list for a season with conferences`() {
        // Arrange
        val request = getRequest("basic-season-with-conferences")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(8, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, Standing.FIRST_TIED, expectedConference = "Conference 1", expectedConferencePosition = Standing.FIRST)
        result[1].assertMatches("Team 2", 8, 33, 2, null, expectedConference = "Conference 1")
        result[2].assertMatches("Team 3", 9, 33, 3, null, expectedConference = "Conference 1")
        result[3].assertMatches("Team 4", 10, 33, 4, Standing.LAST_TIED, expectedConference = "Conference 1", expectedConferencePosition = Standing.LAST)
        result[4].assertMatches("Team 5", 11, 33, 5, Standing.FIRST_TIED, expectedConference = "Conference 2", expectedConferencePosition = Standing.FIRST)
        result[5].assertMatches("Team 6", 12, 33, 6, null, expectedConference = "Conference 2")
        result[6].assertMatches("Team 7", 13, 33, 7, null, expectedConference = "Conference 2")
        result[7].assertMatches("Team 8", 7, 33, 8, Standing.LAST_TIED, expectedConference = "Conference 2", expectedConferencePosition = Standing.LAST)
    }

    @Test
    fun `returns a valid list for a season with conferences with some ties`() {
        // Arrange
        val request = getRequest("basic-season-with-conferences-and-ties")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(8, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, null, expectedConference = "Conference 1", expectedConferencePosition = Standing.FIRST_TIED)
        result[1].assertMatches("Team 2", 8, 33, 2, null, expectedConference = "Conference 1", expectedConferencePosition = Standing.FIRST_TIED)
        result[2].assertMatches("Team 3", 9, 33, 3, null, expectedConference = "Conference 1")
        result[3].assertMatches("Team 4", 10, 33, 4, Standing.LAST, expectedConference = "Conference 1", expectedConferencePosition = Standing.LAST)
        result[4].assertMatches("Team 5", 11, 33, 5, Standing.FIRST, expectedConference = "Conference 2", expectedConferencePosition = Standing.FIRST)
        result[5].assertMatches("Team 6", 12, 33, 6, null, expectedConference = "Conference 2")
        result[6].assertMatches("Team 7", 13, 33, 7, null, expectedConference = "Conference 2", expectedConferencePosition = Standing.LAST_TIED)
        result[7].assertMatches("Team 8", 7, 33, 8, null, expectedConference = "Conference 2", expectedConferencePosition = Standing.LAST_TIED)
    }

    @Test
    fun `returns a valid list for a season with divisions`() {
        // Arrange
        val request = getRequest("basic-season-with-divisions")

        // Act
        val result = prepareFranchiseSeasons(33, request, chapters)

        // Assert
        assertEquals(16, result.size)
        result[0].assertMatches("Team 1", 7, 33, 1, null, expectedConference = "Conference 1", expectedConferencePosition = Standing.FIRST_TIED, expectedDivision = "Division 1", expectedDivisionPosition = Standing.FIRST)
        result[1].assertMatches("Team 2", 8, 33, 2, null, expectedConference = "Conference 1", expectedDivision = "Division 1")
        result[2].assertMatches("Team 3", 9, 33, 3, null, expectedConference = "Conference 1", expectedDivision = "Division 1")
        result[3].assertMatches("Team 4", 10, 33, 4, null, expectedConference = "Conference 1", expectedConferencePosition = Standing.LAST_TIED, expectedDivision = "Division 1", expectedDivisionPosition = Standing.LAST)
        result[4].assertMatches("Team 5", 11, 33, 5, null, expectedConference = "Conference 1", expectedConferencePosition = Standing.FIRST_TIED, expectedDivision = "Division 2", expectedDivisionPosition = Standing.FIRST)
        result[5].assertMatches("Team 6", 12, 33, 6, null, expectedConference = "Conference 1", expectedDivision = "Division 2")
        result[6].assertMatches("Team 7", 13, 33, 7, null, expectedConference = "Conference 1", expectedDivision = "Division 2")
        result[7].assertMatches("Team 8", 7, 33, 8, null, expectedConference = "Conference 1", expectedConferencePosition = Standing.LAST_TIED, expectedDivision = "Division 2", expectedDivisionPosition = Standing.LAST)
        result[8].assertMatches("Team 9", 7, 33, 9, null, expectedConference = "Conference 2", expectedDivision = "Division 3", expectedDivisionPosition = Standing.FIRST_TIED)
        result[9].assertMatches("Team 10", 15, 33, 10, null, expectedConference = "Conference 2", expectedDivision = "Division 3", expectedDivisionPosition = Standing.FIRST_TIED)
        result[10].assertMatches("Team 11", 16, 33, 11, null, expectedConference = "Conference 2", expectedDivision = "Division 3", expectedDivisionPosition = Standing.LAST_TIED)
        result[11].assertMatches("Team 12", 17, 33, 12, null, expectedConference = "Conference 2", expectedDivision = "Division 3", expectedDivisionPosition = Standing.LAST_TIED)
        result[12].assertMatches("Team 13", 18, 33, 13, Standing.FIRST, expectedConference = "Conference 2", expectedConferencePosition = Standing.FIRST, expectedDivision = "Division 4", expectedDivisionPosition = Standing.FIRST)
        result[13].assertMatches("Team 14", 19, 33, 14, null, expectedConference = "Conference 2", expectedDivision = "Division 4")
        result[14].assertMatches("Team 15", 20, 33, 15, null, expectedConference = "Conference 2", expectedDivision = "Division 4")
        result[15].assertMatches("Team 16", 7, 33, 16, Standing.LAST, expectedConference = "Conference 2", expectedConferencePosition = Standing.LAST, expectedDivision = "Division 4", expectedDivisionPosition = Standing.LAST)
    }

    private fun ProtoFranchiseSeason.assertMatches(
        expectedTeamName: String,
        expectedMetroId: Int,
        expectedSeasonId: Int,
        expectedFranchiseId: Int,
        expectedLeaguePosition: Standing?,
        expectedPostseasonQualifier: Boolean? = null,
        expectedRoundsWon: Int? = null,
        expectedReachedChampionship: Boolean? = null,
        expectedChampion: Boolean? = null,
        expectedConference: String? = null,
        expectedConferencePosition: Standing? = null,
        expectedDivision: String? = null,
        expectedDivisionPosition: Standing? = null,
        expectedLeagueId: Int? = 6,
    ) {
        assertEquals(expectedTeamName, teamName)
        assertEquals(expectedMetroId, metroId)
        assertEquals(expectedSeasonId, seasonId)
        assertEquals(expectedFranchiseId, franchiseId)
        assertEquals(expectedLeaguePosition, leaguePosition)
        assertEquals(expectedPostseasonQualifier, qualifiedForPostseason, "Postseason qualification did not match")
        assertEquals(expectedRoundsWon, roundsWon, "Postseason rounds won did not match")
        assertEquals(expectedReachedChampionship, appearedInChampionship, "Appeared in championship did not match")
        assertEquals(expectedChampion, wonChampionship, "Won championship did not match")
        assertEquals(expectedConference, conferenceName)
        assertEquals(expectedConferencePosition, conferencePosition)
        assertEquals(expectedDivision, divisionName)
        assertEquals(expectedDivisionPosition, divisionPosition)
        assertEquals(expectedLeagueId, leagueId)
    }

    private fun getRequest(name: String): CreateSeasonRequest {
        val json = readResource()
        val requests = Json.decodeFromString<Map<String, CreateSeasonRequest>>(json)

        return requests[name] ?: throw Error("Could not find example $name")
    }

    private fun readResource(): String {
        return this::class.java.classLoader.getResource("CreateSeasonRequests.json")?.readText()
            ?: error("Resource not found")
    }
}