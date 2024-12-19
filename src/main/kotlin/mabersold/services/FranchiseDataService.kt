package mabersold.services

import mabersold.dao.ChapterDAO
import mabersold.dao.FranchiseDAO
import mabersold.models.api.Franchise
import mabersold.models.api.requests.CreateFranchiseRequest

class FranchiseDataService(
    private val franchiseDAO: FranchiseDAO,
    private val chapterDAO: ChapterDAO
) {
    suspend fun getFranchises(leagueId: Int): List<Franchise> =
        franchiseDAO.allByLeagueId(leagueId).map { f ->
            Franchise(
                id = f.id,
                name = f.name,
                label = f.label,
                isDefunct = f.isDefunct,
                leagueId = f.leagueId,
                league = f.league,
                chapters = listOf()
            )
        }

    suspend fun createFranchise(request: CreateFranchiseRequest): Franchise {
        println("Creating a single franchise called ${request.name}")
        // Create the franchise
        val newFranchise = franchiseDAO.create(
            request.name,
            request.label,
            request.isDefunct,
            request.leagueId
        )

        if (newFranchise == null) {
            throw RuntimeException("Unable to create new franchise")
        }

        // Create the chapters
        val newChapters = request.chapters.map { c ->
            chapterDAO.create(
                c.teamName,
                newFranchise.id,
                c.metroId,
                c.leagueId,
                c.startYear,
                c.endYear,
                c.conferenceName,
                c.divisionName
            )
        }

        val apiChapters = newChapters.mapNotNull { c -> c?.let { mabersold.models.api.Chapter(c.id, c.teamName, c.metroId, c.leagueId, c.startYear, c.endYear, c.conferenceName, c.divisionName) } }
        return newFranchise.let { Franchise(it.id, it.name, it.label, it.isDefunct, it.leagueId, it.league, apiChapters) }
    }

    suspend fun updateFranchise(franchiseId: Int, name: String?, isDefunct: Boolean?, leagueId: Int?): Franchise? {
        return franchiseDAO.update(franchiseId, name, isDefunct, leagueId)?.let { f ->
            Franchise(
                id = f.id,
                name = f.name,
                label = f.label,
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