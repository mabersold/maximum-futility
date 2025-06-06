package mabersold.services

import mabersold.dao.LeagueDAO
import mabersold.models.api.League

class LeagueDataService(private val leagueDAO: LeagueDAO) {
    suspend fun all() = leagueDAO.all().map { l ->
        League(
            id = l.id,
            name = l.name,
            sport = l.sport,
            label = l.label
        )
    }

    suspend fun create(name: String, sport: String, label: String): League? {
        val result = leagueDAO.create(name, sport, label)
        return result?.let {
            League(it.id, it.name, it.sport, it.label)
        }
    }

    suspend fun update(leagueId: Int, name: String?, sport: String?): League? {
        leagueDAO.update(leagueId, name, sport)

        return leagueDAO.get(leagueId)?.let {
            League(it.id, it.name, it.sport, it.label)
        }
    }

    suspend fun delete(leagueId: Int): Boolean {
        return leagueDAO.delete(leagueId)
    }
}