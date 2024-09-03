package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class Metro(
    val id: Int,
    val name: String
)
