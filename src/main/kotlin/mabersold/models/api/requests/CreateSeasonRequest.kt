package mabersold.models.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSeasonRequest(
    val name: String,
    @SerialName("start_year")
    val startYear: Int,
    @SerialName("end_year")
    val endYear: Int,
    @SerialName("league_id")
    val leagueId: Int,
    @SerialName("total_major_divisions")
    val totalMajorDivisions: Int,
    @SerialName("total_minor_divisions")
    val totalMinorDivisions: Int,
    val standings: ResultGroup,
    @SerialName("play_in")
    val playIn: Postseason? = null,
    val postseason: Postseason? = null
)

@Serializable
data class ResultGroup(
    val name: String? = null,
    val results: List<Result>? = null,
    @SerialName("sub_groups")
    val subGroups: List<ResultGroup>? = null
)

@Serializable
data class Result(
    @SerialName("franchise_id")
    val franchiseId: Int,
    val wins: Int,
    val losses: Int,
    val ties: Int? = null,
    @SerialName("overtime_losses")
    val overtimeLosses: Int? = null,
    val points: Int? = null
) {
    val winningRate = wins.toDouble() / (wins + losses)
}

@Serializable
data class Postseason(
    val rounds: List<Round>
)

@Serializable
data class Round(
    @SerialName("round_number")
    val roundNumber: Int,
    val matchups: List<Matchup>
)

@Serializable
data class Matchup(
    val winner: Participant,
    val loser: Participant,
    @SerialName("is_championship_round")
    val isChampionshipRound: Boolean? = false
)

@Serializable
data class Participant(
    @SerialName("franchise_id")
    val franchiseId: Int,
    @SerialName("games_won")
    val gamesWon: Int? = null
)