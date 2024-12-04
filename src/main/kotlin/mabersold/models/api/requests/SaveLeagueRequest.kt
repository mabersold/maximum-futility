package mabersold.models.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class SaveLeagueRequest(
    val name: String? = null,
    val sport: String? = null,
    val label: String? = null
) {
    fun canCreate() = name?.isNotBlank() == true && sport?.isNotBlank() == true && label?.isNotBlank() == true
}
