package mabersold.models.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateLeagueRequest(
    val name: String,
    val sport: String
)
