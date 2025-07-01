package mabersold.validators

import mabersold.models.api.requests.CreateSeasonRequest
import mabersold.models.api.requests.Postseason
import mabersold.models.api.requests.ResultGroup

const val END_YEAR_TOO_LOW = "End year cannot be lower than start year"
const val END_YEAR_TOO_HIGH = "End year can be no more than one higher than start year"
const val NO_RESULTS_AT_THIS_LEVEL = "No results are expected at this level"
const val NO_SUBGROUPS_AT_THIS_LEVEL = "No subgroups are expected at this level"
const val DUPLICATE_TEAM_IN_STANDINGS = "At least one franchise appears more than once in the standings"
const val INVALID_POSTSEASON_TEAM = "At least one franchise appears in the postseason that is not in the regular season standings"
const val WINNING_TEAM_DID_NOT_ADVANCE = "A winning team in the postseason should have advanced, but did not"
const val LOSING_TEAM_ADVANCED = "A team that lost a postseason round appeared in a later round"
const val TEAM_PLAYED_ITSELF = "A team played itself in the postseason"
const val CHAMPIONSHIP_INVALID = "The championship must be the final round with a single matchup"
const val MISMATCHED_MAJOR_DIVISIONS = "The number of major divisions does not match the provided value"
const val MISMATCHED_MINOR_DIVISIONS = "The number of minor divisions does not match the provided value"

fun validateCreateSeasonRequest(req: CreateSeasonRequest): List<String> {
    val errors = mutableListOf<String>()

    if (req.endYear < req.startYear) {
        errors.add(END_YEAR_TOO_LOW)
    }

    if (req.endYear > req.startYear && req.endYear - req.startYear > 1) {
        errors.add(END_YEAR_TOO_HIGH)
    }

    val regularSeasonErrors = findRegularSeasonErrors(req)
    errors.addAll(regularSeasonErrors)

    val regularSeasonFranchiseIds = hashSetOf<Int>()
    getRegularSeasonFranchiseIds(req.standings, regularSeasonFranchiseIds)

    req.postseason?.let {
        val postseasonErrors = findPostseasonErrors(req.postseason, regularSeasonFranchiseIds)
        errors.addAll(postseasonErrors)
    }

    return errors
}

private fun getRegularSeasonFranchiseIds(standingsGroup: ResultGroup, franchiseIdSet: HashSet<Int>) {
    if (standingsGroup.subGroups.isNullOrEmpty()) {
        franchiseIdSet.addAll(standingsGroup.results?.map { sg -> sg.franchiseId }.orEmpty())
        return
    }

    for (group in standingsGroup.subGroups) {
        getRegularSeasonFranchiseIds(group, franchiseIdSet)
    }
}

private fun findRegularSeasonErrors(request: CreateSeasonRequest): Set<String> {
    val errors = hashSetOf<String>()

    val maxLevel = when {
        request.totalMinorDivisions == 0 && request.totalMajorDivisions == 0 -> 0
        request.totalMinorDivisions == 0 && request.totalMajorDivisions > 0 -> 1
        else -> 2
    }

    // level 0
    errors.addAll(validateStandingsLevel(listOf(request.standings), 0 == maxLevel))

    // Level 1
    if (maxLevel > 0) {
        val conferenceGroups = request.standings.subGroups ?: listOf()
        if (conferenceGroups.size != request.totalMajorDivisions) {
            errors.add(MISMATCHED_MAJOR_DIVISIONS)
        }

        errors.addAll(validateStandingsLevel(conferenceGroups, 1 == maxLevel))
    }

    // Level 2
    if (maxLevel == 2) {
        val divisionGroups = request.standings.subGroups?.flatMap { it.subGroups ?: listOf() } ?: listOf()
        if (divisionGroups.size != request.totalMinorDivisions) {
            errors.add(MISMATCHED_MINOR_DIVISIONS)
        }
        errors.addAll(validateStandingsLevel(divisionGroups, true))
    }

    return errors
}

private fun validateStandingsLevel(groups: List<ResultGroup>, isLastLevel: Boolean): Set<String> {
    val errors = hashSetOf<String>()

    if (!isLastLevel) {
        // No results are expected at this level
        if (groups.any { g -> g.results?.isNotEmpty() == true || g.subGroups.isNullOrEmpty() }) {
            errors.add(NO_RESULTS_AT_THIS_LEVEL)
        }
        return errors
    }

    // No subgroups expected at this level
    if (groups.any { g -> g.results.isNullOrEmpty() || g.subGroups?.isNotEmpty() == true }) {
        errors.add(NO_SUBGROUPS_AT_THIS_LEVEL)
    }

    val allResults = groups.flatMap { it.results ?: emptyList() }

    val franchiseIds = hashSetOf<Int>()
    for (r in allResults) {
        if (franchiseIds.contains(r.franchiseId)) {
            errors.add(DUPLICATE_TEAM_IN_STANDINGS)
        }

        franchiseIds.add(r.franchiseId)
    }

    return errors
}

private fun findPostseasonErrors(postseason: Postseason, allowedFranchiseIds: Set<Int>): Set<String> {
    val errors = hashSetOf<String>()

    val allParticipants = hashSetOf<Int>()
    val previousRoundWinners = hashSetOf<Int>()
    val previousRoundLosers = hashSetOf<Int>()
    val currentRoundWinners = hashSetOf<Int>()
    val currentRoundLosers = hashSetOf<Int>()

    postseason.rounds.forEachIndexed { i, round ->
        currentRoundWinners.clear()
        currentRoundLosers.clear()

        for (matchup in round.matchups) {
            currentRoundWinners.add(matchup.winner.franchiseId)
            currentRoundLosers.add(matchup.loser.franchiseId)

            if (matchup.winner.franchiseId == matchup.loser.franchiseId) {
                errors.add(TEAM_PLAYED_ITSELF)
            }
        }

        // Verify that previous round winners are in the current round
        if (!(currentRoundWinners + currentRoundLosers).containsAll(previousRoundWinners)) {
            errors.add(WINNING_TEAM_DID_NOT_ADVANCE)
        }

        // Verify that previous round losers are not in the current round
        if ((currentRoundWinners + currentRoundLosers).any { previousRoundLosers.contains(it) }) {
            errors.add(LOSING_TEAM_ADVANCED)
        }

        if (i == postseason.rounds.size - 1) {
            if (round.matchups.size != 1 || round.matchups.first().isChampionshipRound != true) {
                errors.add(CHAMPIONSHIP_INVALID)
            }
        } else {
            if (round.matchups.any { it.isChampionshipRound == true }) {
                errors.add(CHAMPIONSHIP_INVALID)
            }
        }

        // Set the data for the next iteration
        allParticipants.addAll(currentRoundWinners)
        allParticipants.addAll(currentRoundLosers)

        previousRoundWinners.clear()
        previousRoundWinners.addAll(currentRoundWinners)
        previousRoundLosers.clear()
        previousRoundLosers.addAll(currentRoundLosers)
    }

    // Verify that all participants are allowed
    if ((allParticipants - allowedFranchiseIds).isNotEmpty()) {
        errors.add(INVALID_POSTSEASON_TEAM)
    }

    return errors
}