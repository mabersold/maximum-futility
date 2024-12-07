package mabersold.dao

import mabersold.models.db.Chapter

interface ChapterDAO {
    suspend fun create(
        teamName: String,
        franchiseId: Int,
        metroId: Int,
        leagueId: Int,
        startYear: Int,
        endYear: Int?,
        conference: String?,
        division: String?
    ): Chapter?
}