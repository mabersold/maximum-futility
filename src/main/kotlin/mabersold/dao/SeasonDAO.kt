package mabersold.dao

import mabersold.models.db.Season

interface SeasonDAO {
    suspend fun all(): List<Season>
    suspend fun get(id: Int): Season?

    suspend fun allByLeagueId(leagueId: Int): List<Season>
    suspend fun create(
        name: String,
        startYear: Int,
        endYear: Int,
        leagueId: Int,
        totalMajorDivisions: Int,
        totalMinorDivisions: Int,
        postSeasonRounds: Int?
    ): Season?
}