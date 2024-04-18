package mabersold.models.api

import kotlinx.serialization.Serializable

@Serializable
data class MetroOptions(
    val yearRange: YearRange,
    val leagues: List<League>,
    val metrics: List<Map<String, String>>
)
