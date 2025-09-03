package mabersold.models.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val id: Int,
    @SerialName("league_id")
    val leagueId: Int,
    val name: String,
    @SerialName("start_year")
    val startYear: Int,
    @SerialName("end_year")
    val endYear: Int
)
