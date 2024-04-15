package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val name: String,
    val groups: List<Group>,
    val teams: List<String>,
    val finishedFirst: List<String>,
    val finishedLast: List<String>
)
