package mabersold.models.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateFranchiseRequest(
    val name: String,
    val label: String,
    @SerialName("is_defunct")
    val isDefunct: Boolean,
    @SerialName("league_id")
    val leagueId: Int,
    val chapters: List<CreateChapterRequest>
)

@Serializable
data class CreateChapterRequest(
    @SerialName("team_name")
    val teamName: String,
    @SerialName("metro_id")
    val metroId: Int,
    @SerialName("league_id")
    val leagueId: Int,
    @SerialName("start_year")
    val startYear: Int,
    @SerialName("end_year")
    val endYear: Int? = null,
    @SerialName("conference_name")
    val conferenceName: String? = null,
    @SerialName("division_name")
    val divisionName: String? = null
)