package mabersold.services

import mabersold.dao.FranchiseSeasonDAO
import mabersold.dao.SeasonDAO
import mabersold.models.RegularSeasonResult
import mabersold.models.SeasonSummary
import mabersold.models.Warning
import mabersold.models.api.YearRange
import mabersold.models.db.FranchiseSeason
import mabersold.models.db.Season
import mabersold.models.db.Standing

class SeasonDataService(private val seasonDAO: SeasonDAO, private val franchiseSeasonDAO: FranchiseSeasonDAO) {
    companion object {
        const val WARN_MULTIPLE_FIRST_IN_LEAGUE = "More than one first in league"
        const val WARN_MULTIPLE_LAST_IN_LEAGUE = "More than one last in league"
        const val WARN_MULTIPLE_FIRST_IN_CONFERENCE = "The following conferences have more than one team in first place: %s"
        const val WARN_MULTIPLE_LAST_IN_CONFERENCE = "The following conferences have more than one team in last place: %s"
        const val WARN_MULTIPLE_FIRST_IN_DIVISION = "The following divisions have more than one team in first place: %s"
        const val WARN_MULTIPLE_LAST_IN_DIVISION = "The following divisions have more than one team in last place: %s"
        const val WARN_CONFERENCES_DO_NOT_MATCH = "Conferences do not match"
        const val WARN_DIVISIONS_DO_NOT_MATCH = "Divisions do not match"
        const val WARN_TOO_MANY_CHAMPIONS = "More than one champion"
        const val WARN_TOO_MANY_TEAMS_IN_CHAMPIONSHIP = "More than two teams in championship"
        const val WARN_NO_BEST_IN_LEAGUE = "No best in league"
        const val WARN_NO_WORST_IN_LEAGUE = "No worst in league"
        const val WARN_NOT_ENOUGH_BEST_IN_CONFERENCE = "Not enough best in conference"
        const val WARN_NOT_ENOUGH_WORST_IN_CONFERENCE = "Not enough worst in conference"
        const val WARN_NOT_ENOUGH_BEST_IN_DIVISION = "Not enough best in division"
        const val WARN_NOT_ENOUGH_WORST_IN_DIVISION = "Not enough worst in division"
        const val WARN_TOO_MANY_ROUNDS_WON = "The following teams won more postseason rounds than are possible: %s"
        const val WARN_SHOULD_NOT_HAVE_POSTSEASON_DATA = "The following teams did not qualify for playoffs, but have postseason data: %s"
    }

    suspend fun getSeasonSummary(seasonId: Int): SeasonSummary {
        val season = seasonDAO.get(seasonId) ?: throw Exception("Season not found")
        val franchiseSeasons = franchiseSeasonDAO.getBySeason(seasonId)

        val warnings = listOfNotNull(
            franchiseSeasons.hasExcessive(LeagueDivider.LEAGUE, Standing.FIRST).withWarning(WARN_MULTIPLE_FIRST_IN_LEAGUE),
            franchiseSeasons.hasExcessive(LeagueDivider.LEAGUE, Standing.LAST).withWarning(WARN_MULTIPLE_LAST_IN_LEAGUE),
            franchiseSeasons.hasExcessive(LeagueDivider.CONFERENCE, Standing.FIRST).withWarning(WARN_MULTIPLE_FIRST_IN_CONFERENCE.format(franchiseSeasons.getExcessive(LeagueDivider.CONFERENCE, Standing.FIRST))),
            franchiseSeasons.hasExcessive(LeagueDivider.CONFERENCE, Standing.LAST).withWarning(WARN_MULTIPLE_LAST_IN_CONFERENCE.format(franchiseSeasons.getExcessive(LeagueDivider.CONFERENCE, Standing.LAST))),
            franchiseSeasons.hasExcessive(LeagueDivider.DIVISION, Standing.FIRST).withWarning(WARN_MULTIPLE_FIRST_IN_DIVISION.format(franchiseSeasons.getExcessive(LeagueDivider.DIVISION, Standing.FIRST))),
            franchiseSeasons.hasExcessive(LeagueDivider.DIVISION, Standing.LAST).withWarning(WARN_MULTIPLE_LAST_IN_DIVISION.format(franchiseSeasons.getExcessive(LeagueDivider.DIVISION, Standing.LAST))),
            franchiseSeasons.conferencesDoNotMatch(season).withWarning(WARN_CONFERENCES_DO_NOT_MATCH),
            franchiseSeasons.divisionsDoNotMatch(season).withWarning(WARN_DIVISIONS_DO_NOT_MATCH),
            franchiseSeasons.tooManyChampions(season).withWarning(WARN_TOO_MANY_CHAMPIONS),
            franchiseSeasons.tooManyTeamsInChampionship(season).withWarning(WARN_TOO_MANY_TEAMS_IN_CHAMPIONSHIP),
            franchiseSeasons.noBestInLeague.withWarning(WARN_NO_BEST_IN_LEAGUE),
            franchiseSeasons.noWorstInLeague.withWarning(WARN_NO_WORST_IN_LEAGUE),
            franchiseSeasons.notEnoughBestInConference(season).withWarning(WARN_NOT_ENOUGH_BEST_IN_CONFERENCE),
            franchiseSeasons.notEnoughWorstInConference(season).withWarning(WARN_NOT_ENOUGH_WORST_IN_CONFERENCE),
            franchiseSeasons.notEnoughBestInDivision(season).withWarning(WARN_NOT_ENOUGH_BEST_IN_DIVISION),
            franchiseSeasons.notEnoughWorstInDivision(season).withWarning(WARN_NOT_ENOUGH_WORST_IN_DIVISION),
            franchiseSeasons.tooManyPostSeasonWins(season).withWarning(WARN_TOO_MANY_ROUNDS_WON.teamsWithExcessivePostseasonWins(season, franchiseSeasons)),
            franchiseSeasons.hasInvalidPostSeasonData.withWarning(WARN_SHOULD_NOT_HAVE_POSTSEASON_DATA.teamsWithInvalidPostSeasonData(franchiseSeasons))
        )

        val overallResult = RegularSeasonResult(
            "Overall",
            franchiseSeasons.filter { it.bestOverall }.map { it.teamName },
            franchiseSeasons.filter { it.worstOverall }.map { it.teamName }
        )

        val conferenceResults = franchiseSeasons.mapNotNull { it.conference }.distinct().sorted().map { conference ->
            RegularSeasonResult(
                conference,
                franchiseSeasons.filter {
                    it.conference == conference && it.bestInConference
                }.map { it.teamName },
                franchiseSeasons.filter {
                    it.conference == conference && it.worstInConference
                }.map { it.teamName }
            )
        }

        val divisionResults = franchiseSeasons.mapNotNull { it.division }.distinct().sorted().map { division ->
            RegularSeasonResult(
                division,
                franchiseSeasons.filter {
                    it.division == division && it.bestInDivision
                }.map { it.teamName },
                franchiseSeasons.filter {
                    it.division == division && it.worstInDivision
                }.map { it.teamName }
            )
        }

        return SeasonSummary(
            season.name,
            season.leagueId.toString(),
            season.totalMajorDivisions,
            season.totalMinorDivisions,
            franchiseSeasons.mapNotNull { it.conference }.distinct().sorted(),
            franchiseSeasons.mapNotNull { it.division }.distinct().sorted(),
            franchiseSeasons.filter { it.qualifiedForPostseason == true }.map { it.teamName }.sorted(),
            franchiseSeasons.filter { it.roundsWon != null && it.roundsWon > 0 }.map { it.teamName }.sorted(),
            franchiseSeasons.filter { it.appearedInChampionship == true }.map { it.teamName }.sorted(),
            franchiseSeasons.filter { it.wonChampionship == true }.map { it.teamName }.firstOrNull() ?: "",
            listOf(overallResult) + conferenceResults + divisionResults,
            warnings
        )
    }

    suspend fun getYearRange(): YearRange {
        return seasonDAO.all().let { seasons ->
            YearRange(seasons.minOf { it.startYear }, seasons.maxOf { it.endYear })
        }
    }

    private fun Boolean.withWarning(warning: String): Warning? = if (this) Warning(warning) else null

    private fun List<FranchiseSeason>.conferencesDoNotMatch(season: Season): Boolean =
        this.mapNotNull { it.conference }.distinct().size != season.totalMajorDivisions

    private fun List<FranchiseSeason>.divisionsDoNotMatch(season: Season): Boolean =
        this.mapNotNull { it.division }.distinct().size != season.totalMinorDivisions

    private fun List<FranchiseSeason>.tooManyChampions(season: Season): Boolean =
        season.postSeasonRounds != null && this.filter { it.wonChampionship == true }.size > 1

    private fun List<FranchiseSeason>.tooManyTeamsInChampionship(season: Season): Boolean =
        season.postSeasonRounds != null && this.filter { it.appearedInChampionship == true }.size > 2

    private fun List<FranchiseSeason>.notEnoughBestInConference(season: Season): Boolean =
        this.filter { it.bestInConference }.size < season.totalMajorDivisions

    private fun List<FranchiseSeason>.notEnoughWorstInConference(season: Season): Boolean =
        this.filter { it.worstInConference }.size < season.totalMajorDivisions

    private fun List<FranchiseSeason>.notEnoughBestInDivision(season: Season): Boolean =
        this.filter { it.bestInDivision }.size < season.totalMinorDivisions

    private fun List<FranchiseSeason>.notEnoughWorstInDivision(season: Season): Boolean =
        this.filter { it.worstInDivision }.size < season.totalMinorDivisions

    private fun List<FranchiseSeason>.tooManyPostSeasonWins(season: Season): Boolean =
        this.any { (it.roundsWon ?: 0) > (season.postSeasonRounds ?: 0) }

    private val List<FranchiseSeason>.hasInvalidPostSeasonData: Boolean
        get() = this.any { it.hasInvalidPostSeasonData }

    private fun String.teamsWithExcessivePostseasonWins(season: Season, franchiseSeasons: List<FranchiseSeason>): String =
        this.format(franchiseSeasons.filter { (it.roundsWon ?: 0) > (season.postSeasonRounds ?: 0) }
            .joinToString { it.teamName })

    private fun String.teamsWithInvalidPostSeasonData(franchiseSeasons: List<FranchiseSeason>): String =
        this.format(franchiseSeasons.filter { it.hasInvalidPostSeasonData }.joinToString { it.teamName })

    private val List<FranchiseSeason>.noBestInLeague: Boolean
        get() = this.none { listOf(Standing.FIRST, Standing.FIRST_TIED).contains(it.leaguePosition) }

    private val List<FranchiseSeason>.noWorstInLeague: Boolean
        get() = this.none { listOf(Standing.LAST, Standing.LAST_TIED).contains(it.leaguePosition) }

    private val FranchiseSeason.hasInvalidPostSeasonData: Boolean
        get() = this.qualifiedForPostseason == false && (this.roundsWon != null || this.wonChampionship == true || this.appearedInChampionship == true)

    private fun List<FranchiseSeason>.hasExcessive(leagueDivider: LeagueDivider, position: Standing) =
        this.groupBy { it.getUnit(leagueDivider) ?: "None" }.values.any { it.unitHasExcessive(leagueDivider, position) }

    private fun List<FranchiseSeason>.unitHasExcessive(leagueDivider: LeagueDivider, position: Standing) =
        this.filter { it.getResultBy(leagueDivider) == position }.size > 1 ||
                (this.filter { it.getResultBy(leagueDivider) == position }.size == 1 && this.any { it.getResultBy(leagueDivider) == position.tiedEquivalent })

    private fun List<FranchiseSeason>.getExcessive(leagueDivider: LeagueDivider, position: Standing) =
        this.groupBy { it.getUnit(leagueDivider) ?: "None" }.filterValues { it.unitHasExcessive(leagueDivider, position) }.keys.joinToString()

    enum class LeagueDivider {
        LEAGUE,
        CONFERENCE,
        DIVISION
    }

    private val Standing.tiedEquivalent get() = when(this) {
        Standing.FIRST -> Standing.FIRST_TIED
        Standing.LAST -> Standing.LAST_TIED
        else -> this
    }

    private fun FranchiseSeason.getUnit(divider: LeagueDivider) = when(divider) {
        LeagueDivider.CONFERENCE -> this.conference
        LeagueDivider.DIVISION -> this.division
        else -> this.leagueId.toString()
    }

    private fun FranchiseSeason.getResultBy(leagueDivider: LeagueDivider) =
        when(leagueDivider) {
            LeagueDivider.CONFERENCE -> this.conferencePosition
            LeagueDivider.DIVISION -> this.divisionPosition
            LeagueDivider.LEAGUE -> this.leaguePosition
        }
}