package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class FranchisePostseason(
    val id: Int,
    val postSeasonId: Int,
    val franchiseId: Int,
    val metroId: Int,
    val leagueId: Int,
    val roundsWon: Int,
    val appearedInChampionship: Boolean,
    val wonChampionship: Boolean
)

object FranchisePostseasons : IntIdTable() {
    val postSeasonId = reference("post_season_id", PostSeasons)
    val franchiseId = reference("franchise_id", Franchises)
    val metroId = reference("metro_id", Metros)
    val leagueId = reference("league_id", Leagues)
    val roundsWon = integer("rounds_won")
    val appearedInChampionship = bool("appeared_in_championship")
    val wonChampionship = bool("won_championship")
}
