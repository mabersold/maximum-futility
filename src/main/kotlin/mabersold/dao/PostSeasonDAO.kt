package mabersold.dao

import mabersold.models.db.PostSeason

interface PostSeasonDAO {
    suspend fun all(): List<PostSeason>
    suspend fun get(id: Int): PostSeason?
}