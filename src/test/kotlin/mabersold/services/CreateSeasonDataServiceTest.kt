package mabersold.services

import io.ktor.server.plugins.BadRequestException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mabersold.dao.ChapterDAO
import mabersold.dao.FranchiseSeasonDAO
import mabersold.dao.SeasonDAO
import mabersold.functions.prepareFranchiseSeasons
import mabersold.models.api.requests.CreateSeasonRequest
import mabersold.models.db.Chapter
import mabersold.models.db.Season
import mabersold.models.db.Standing
import mabersold.models.intermediary.ProtoFranchiseSeason
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateSeasonDataServiceTest {
    private val seasonDao = mockk<SeasonDAO>()
    private val chapterDao = mockk<ChapterDAO>()
    private val franchiseSeasonDAO = mockk<FranchiseSeasonDAO>()
    private val service = CreateSeasonDataService(seasonDao, chapterDao, franchiseSeasonDAO)

    @Test
    fun `throws error if season has already been created`() = runTest {
        // Arrange
        val request = getRequest("1903-mlb")
        coEvery { seasonDao.allByLeagueId(request.leagueId) } returns listOf(
            Season(1, "1903 MLB Season", 1903, 1903, 1, 2, 0, 1)
        )

        // Act
        val exception = assertFailsWith<BadRequestException> {
            service.addSeason(request)
        }

        // Assert
        assertEquals(
            "Season already exists in leagueId ${request.leagueId} for start year ${request.startYear}",
            exception.message
        )
    }

    @Test
    fun `creates a season successfully`() = runTest {
        // Arrange
        val request = getRequest("basic-season")
        mockkStatic("mabersold.functions.PrepareFranchiseSeasonsKt")

        val expectedList = listOf(
            ProtoFranchiseSeason("Team 1", 7, 33, 1, 1, Standing.FIRST, null, null, null, null, null, null, null, null),
            ProtoFranchiseSeason("Team 2", 8, 33, 2, 1, null, null, null, null, null, null, null, null, null),
            ProtoFranchiseSeason("Team 3", 9, 33, 3, 1, null, null, null, null, null, null, null, null, null),
            ProtoFranchiseSeason("Team 4", 10, 33, 4, 1, Standing.LAST, null, null, null, null, null, null, null, null),
        )

        val chapterList = listOf(
            Chapter(1, "Team 1", 1, 7, 6, 1900, null, null, null),
            Chapter(2, "Team 2", 2, 8, 6, 1900, null, null, null),
            Chapter(3, "Team 3", 3, 9, 6, 1900, null, null, null),
            Chapter(4, "Team 4", 4, 10, 6, 1900, null, null, null)
        )

        coEvery { seasonDao.allByLeagueId(request.leagueId) } returns listOf()
        coEvery { chapterDao.findByFranchiseIds(any()) } returns chapterList

        coEvery {
            seasonDao.create(
                request.name,
                request.startYear,
                request.endYear,
                request.leagueId,
                request.totalMajorDivisions,
                request.totalMinorDivisions,
                null
            )
        } returns Season(
            1,
            request.name,
            request.startYear,
            request.endYear,
            request.leagueId,
            request.totalMajorDivisions,
            request.totalMinorDivisions,
            null
        )

        every { prepareFranchiseSeasons(any(), any(), chapterList) }.returns(expectedList)
        coEvery { franchiseSeasonDAO.createAll(any()) }.returns(listOf())

        // Act
        service.addSeason(request)

        // Assert
        coVerify {
            seasonDao.create(
                request.name,
                request.startYear,
                request.endYear,
                request.leagueId,
                request.totalMajorDivisions,
                request.totalMinorDivisions,
                null
            )
        }

        coVerify {
            franchiseSeasonDAO.createAll(expectedList)
        }
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