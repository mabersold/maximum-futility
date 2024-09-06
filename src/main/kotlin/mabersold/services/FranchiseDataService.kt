package mabersold.services

import mabersold.dao.FranchiseDAO
import mabersold.models.api.Franchise

class FranchiseDataService(private val franchiseDAO: FranchiseDAO) {
    suspend fun getFranchises(leagueId: Int): List<Franchise> =
        franchiseDAO.allByLeagueId(leagueId).map { f ->
            Franchise(
                id = f.id,
                name = f.name,
                isDefunct = f.isDefunct,
                leagueId = f.leagueId,
                league = f.league
            )
        }

    suspend fun createFranchise(name: String, isDefunct: Boolean, leagueId: Int): Franchise? {
        val newFranchise = franchiseDAO.create(name, isDefunct, leagueId)

        return newFranchise?.let { f ->
            Franchise(
                id = f.id,
                name = f.name,
                isDefunct = f.isDefunct,
                leagueId = f.leagueId
            )
        }
    }

    suspend fun updateFranchise(franchiseId: Int, name: String?, isDefunct: Boolean?, leagueId: Int?): Franchise? {
        return franchiseDAO.update(franchiseId, name, isDefunct, leagueId)?.let { f ->
            Franchise(
                id = f.id,
                name = f.name,
                isDefunct = f.isDefunct,
                leagueId = f.leagueId
            )
        }
    }

    suspend fun deleteFranchise(franchiseId: Int): Boolean {
        return franchiseDAO.delete(franchiseId)
    }
}