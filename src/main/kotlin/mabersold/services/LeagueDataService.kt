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
}