package mabersold.dao

import mabersold.models.db.MetroLeagueYear

interface MetroLeagueYearDAO {
    suspend fun all(startYear: Int, endYear: Int, leagueIds: Set<Int>): List<MetroLeagueYear>
}