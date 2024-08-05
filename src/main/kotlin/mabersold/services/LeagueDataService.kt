package mabersold.services

import mabersold.dao.LeagueDAO
import mabersold.models.api.League

class LeagueDataService(private val leagueDAO: LeagueDAO) {
    suspend fun all() = leagueDAO.all().map { l ->
        League(
            id = l.id,
            name = l.name,
            sport = l.sport
        )
    }

    suspend fun update(leagueId: Int, name: String?, sport: String?): League? {
        leagueDAO.update(leagueId, name, sport)

        return leagueDAO.get(leagueId)?.let {
            League(it.id, it.name, it.sport)
        }
    }
}