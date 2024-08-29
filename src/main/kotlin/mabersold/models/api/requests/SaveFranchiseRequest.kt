package mabersold.models.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveFranchiseRequest(
    val name: String? = null,
    @SerialName("is_defunct")
    val isDefunct: Boolean? = null,
    @SerialName("league_id")
    val leagueId: Int? = null
) {
    fun canCreate(): Boolean {
        return !name.isNullOrBlank() && isDefunct != null && leagueId != null
    }
}