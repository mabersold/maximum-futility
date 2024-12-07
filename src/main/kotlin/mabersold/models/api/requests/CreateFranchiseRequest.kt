package mabersold.models.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateFranchiseRequest(
    val name: String,
    @SerialName("is_defunct")
    val isDefunct: Boolean,
    @SerialName("league_label")
    val leagueLabel: String,
    val chapters: List<CreateChapterRequest>
)

@Serializable
data class CreateChapterRequest(
    @SerialName("team_name")
    val teamName: String,
    @SerialName("metro_label")
    val metroLabel: String,
    @SerialName("league_label")
    val leagueLabel: String,
    @SerialName("start_year")
    val startYear: Int,
    @SerialName("end_year")
    val endYear: Int? = null,
    @SerialName("conference_name")
    val conferenceName: String? = null,
    @SerialName("division_name")
    val divisionName: String? = null
)