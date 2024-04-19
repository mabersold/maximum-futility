package mabersold.services

import mabersold.dao.FranchiseDAO
import mabersold.models.api.Franchise as ApiFranchise

class FranchiseDataService(private val franchiseDAO: FranchiseDAO) {
    suspend fun getFranchises(leagueId: Int): List<ApiFranchise> =
        franchiseDAO.allByLeagueId(leagueId).map { f ->
            ApiFranchise(
                id = f.id,
                name = f.name,
                isDefunct = f.isDefunct,
                leagueId = f.leagueId
            )
        }
}