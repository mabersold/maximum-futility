package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class League(val id: Int, val name: String, val sport: String, val label: String)
