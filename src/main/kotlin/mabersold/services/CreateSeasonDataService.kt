package mabersold.services

import io.ktor.server.plugins.BadRequestException
import mabersold.dao.ChapterDAO
import mabersold.dao.FranchiseSeasonDAO
import mabersold.dao.SeasonDAO
import mabersold.functions.prepareFranchiseSeasons
import mabersold.models.api.requests.CreateSeasonRequest
import mabersold.models.api.requests.ResultGroup

class CreateSeasonDataService(
    private val seasonDAO: SeasonDAO,
    private val chapterDAO: ChapterDAO,
    private val franchiseSeasonDAO: FranchiseSeasonDAO
) {
    suspend fun addSeason(request: CreateSeasonRequest) {
        val seasons = seasonDAO.allByLeagueId(request.leagueId)
        if (seasons.any { s -> s.startYear == request.startYear }) {
            throw BadRequestException("Season already exists in leagueId ${request.leagueId} for start year ${request.startYear}")
        }

        val franchiseIds = getAllFranchiseIds(request.standings)

        val chapters = chapterDAO.findByFranchiseIds(franchiseIds)

        val postseasonRounds = request.postseason?.rounds?.size

        val season = seasonDAO.create(
            request.name,
            request.startYear,
            request.endYear,
            request.leagueId,
            request.totalMajorDivisions,
            request.totalMinorDivisions,
            postseasonRounds
        )

        if (season?.id == null) {
            throw Error("This should not have happened")
        }

        val protoFranchiseSeasons = prepareFranchiseSeasons(season.id, request, chapters)

        franchiseSeasonDAO.createAll(protoFranchiseSeasons)
    }

    private fun getAllFranchiseIds(group: ResultGroup): List<Int> {
        if (group.subGroups.isNullOrEmpty()) {
            return group.results?.map { it.franchiseId } ?: emptyList()
        }

        return group.subGroups.flatMap { getAllFranchiseIds(it) }
    }
}