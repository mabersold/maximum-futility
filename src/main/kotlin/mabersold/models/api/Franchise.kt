package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class Franchise(
    val id: Int,
    val name: String,
    val isDefunct: Boolean,
    val leagueId: Int,
    val league: String? = null,
    val chapters: List<Chapter>
)
