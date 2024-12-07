package mabersold.services

import mabersold.dao.ChapterDAO
import mabersold.dao.FranchiseDAO
import mabersold.dao.LeagueDAO
import mabersold.dao.MetroDAO
import mabersold.models.api.Franchise
import mabersold.models.api.requests.CreateFranchiseRequest

class FranchiseDataService(
    private val franchiseDAO: FranchiseDAO,
    private val metroDAO: MetroDAO,
    private val leagueDAO: LeagueDAO,
    private val chapterDAO: ChapterDAO
) {
    suspend fun getFranchises(leagueId: Int): List<Franchise> =
        franchiseDAO.allByLeagueId(leagueId).map { f ->
            Franchise(
                id = f.id,
                name = f.name,
                isDefunct = f.isDefunct,
                leagueId = f.leagueId,
                league = f.league,
                listOf()
            )
        }

    suspend fun createFranchise(request: CreateFranchiseRequest): Franchise? {
        // Get league by label
        val leagueLabels = request.chapters.map { it.leagueLabel }.plus(request.leagueLabel).distinct()
        val leagues = leagueDAO.allByLabel(leagueLabels)
        val leaguesByLabel = leagues.associate { it.label to it.id }

        // Get all metros by label
        val metroLabels = request.chapters.map { it.metroLabel }.distinct()
        val metros = metroDAO.allByLabel(metroLabels)
        val metrosByLabel = metros.associate { it.label to it.id }

        // Create the franchise
        val newFranchise = franchiseDAO.create(
            request.name,
            request.isDefunct,
            leaguesByLabel[request.leagueLabel]!!
        )

        val newChapters = request.chapters.map { c ->
            chapterDAO.create(
                c.teamName,
                newFranchise?.id!!,
                metrosByLabel[c.metroLabel]!!,
                leaguesByLabel[request.leagueLabel]!!,
                c.startYear,
                c.endYear,
                c.conferenceName,
                c.divisionName
            )
        }

        val apiChapters = newChapters.mapNotNull { c -> c?.let { mabersold.models.api.Chapter(c.id, c.teamName, c.metroId, c.leagueId, c.startYear, c.endYear, c.conferenceName, c.divisionName) } }
        return newFranchise?.let { Franchise(it.id, it.name, it.isDefunct, it.leagueId, it.league, apiChapters) }
    }

    suspend fun updateFranchise(franchiseId: Int, name: String?, isDefunct: Boolean?, leagueId: Int?): Franchise? {
        return franchiseDAO.update(franchiseId, name, isDefunct, leagueId)?.let { f ->
            Franchise(
                id = f.id,
                name = f.name,
                isDefunct = f.isDefunct,
                leagueId = f.leagueId,
                chapters = listOf()
            )
        }
    }

    suspend fun deleteFranchise(franchiseId: Int): Boolean {
        return franchiseDAO.delete(franchiseId)
    }
}