package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val id: Int,
    val name: String,
    val startYear: Int,
    val endYear: Int
)
