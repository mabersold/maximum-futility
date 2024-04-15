package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class Warning(
    val message: String
)
