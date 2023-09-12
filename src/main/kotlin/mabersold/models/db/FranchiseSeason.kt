package mabersold.models.db

import org.jetbrains.exposed.dao.id.IntIdTable

data class FranchiseSeason(
    val id: Int,
    val seasonId: Int,
    val franchiseId: Int,
    val metroId: Int,
    val teamName: String,
    val leagueId: Int,
    val conference: String?,
    val division: String?,
    val leaguePosition: Standing?,
    val conferencePosition: Standing?,
    val divisionPosition: Standing?,
    val qualifiedForPostseason: Boolean?,
    val roundsWon: Int?,
    val appearedInChampionship: Boolean?,
    val wonChampionship: Boolean?
) {
    val bestOverall: Boolean get() = BEST.contains(this.leaguePosition)
    val worstOverall: Boolean get() = WORST.contains(this.leaguePosition)
    val bestInConference: Boolean get() = BEST.contains(this.conferencePosition)
    val worstInConference: Boolean get() = WORST.contains(this.conferencePosition)
    val bestInDivision: Boolean get() = BEST.contains(this.divisionPosition)
    val worstInDivision: Boolean get() = WORST.contains(this.divisionPosition)

    companion object {
        private val BEST = listOf(Standing.FIRST, Standing.FIRST_TIED)
        private val WORST = listOf(Standing.LAST, Standing.LAST_TIED)
    }
}

object FranchiseSeasons : IntIdTable() {
    val seasonId = reference("season_id", Seasons)
    val franchiseId = reference("franchise_id", Franchises)
    val metroId = reference("metro_id", Metros)
    val teamName = varchar("team_name", 128)
    val leagueId = reference("league_id", Leagues)
    val conference = varchar("conference", 128).nullable()
    val division = varchar("division", 128).nullable()
    val leaguePosition = enumerationByName("league_position", 128, Standing::class).nullable()
    val conferencePosition = enumerationByName("conference_position", 128, Standing::class).nullable()
    val divisionPosition = enumerationByName("division_position", 128, Standing::class).nullable()
    val qualifiedForPostseason = bool("qualified_for_postseason").nullable()
    val roundsWon = integer("rounds_won").nullable()
    val appearedInChampionship = bool("appeared_in_championship").nullable()
    val wonChampionship = bool("won_championship").nullable()
}

enum class Standing {
    FIRST,
    FIRST_TIED,
    LAST,
    LAST_TIED,
    NONE
}