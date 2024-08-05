package mabersold.services

import mabersold.dao.LeagueDAO
import mabersold.models.api.League
import mabersold.models.api.requests.CreateLeagueRequest

class LeagueDataService(private val leagueDAO: LeagueDAO) {
    suspend fun all() = leagueDAO.all().map { l ->
        League(
            id = l.id,
            name = l.name,
            sport = l.sport
        )
    }

    suspend fun create(request: CreateLeagueRequest): League? {
        val result = leagueDAO.create(request.name, request.sport)
        return result?.let {
            League(it.id, it.name, it.sport)
        }
    }

    suspend fun update(leagueId: Int, name: String?, sport: String?): League? {
        leagueDAO.update(leagueId, name, sport)

        return leagueDAO.get(leagueId)?.let {
            League(it.id, it.name, it.sport)
        }
    }

    suspend fun delete(leagueId: Int): Boolean {
        return leagueDAO.delete(leagueId)
    }
}