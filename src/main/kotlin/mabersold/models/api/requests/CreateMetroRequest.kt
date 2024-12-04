package mabersold.models.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateMetroRequest(
    val name: String,
    val label: String
)