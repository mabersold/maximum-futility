package mabersold.models.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateLeagueRequest(
    val name: String? = null,
    val sport: String? = null
)