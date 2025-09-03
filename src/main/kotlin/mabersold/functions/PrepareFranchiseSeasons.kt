package mabersold.functions

import io.ktor.server.plugins.BadRequestException
import mabersold.models.api.requests.CreateSeasonRequest
import mabersold.models.api.requests.Postseason
import mabersold.models.api.requests.Result
import mabersold.models.api.requests.ResultGroup
import mabersold.models.db.Chapter
import mabersold.models.db.Standing
import mabersold.models.intermediary.ProtoFranchiseSeason
import java.util.Calendar

data class MinMaxInfo(val max: Double, val min: Double, val maxCount: Int, val minCount: Int)

fun prepareFranchiseSeasons(seasonId: Int, request: CreateSeasonRequest, chapters: List<Chapter>): List<ProtoFranchiseSeason> {
    if (chapters.isEmpty()) {
        throw BadRequestException("No chapters available")
    }

    val returnList = mutableListOf<ProtoFranchiseSeason>()

    // Quick lookup of conferences and divisions so we can find which one each team belongs in
    val resultGroups = hashMapOf(
        "Conference" to hashMapOf<String, List<Result>>(),
        "Division" to hashMapOf<String, List<Result>>(),
    )
    val overallList = getAllResultGroups(request.standings, resultGroups)

    // Min-Max Info stores best and worst results of each group, and how many of them are
    // This is used later in determining whether a team is best/worst in a group
    val minMaxInfo = hashMapOf<String, MinMaxInfo>()
    minMaxInfo["Overall"] = createMinMaxInfo(overallList)

    for (label in listOf("Conference", "Division")) {
        resultGroups[label]?.entries?.forEach { (name, results) ->
            minMaxInfo[name] = createMinMaxInfo(results)
        }
    }

    for (result in overallList) {
        val chapter = chapters.getChapter(result.franchiseId, request.startYear)!!

        val overallPosition = getPosition(result, minMaxInfo["Overall"])

        // Determine team's conference name and position
        val confEntry = resultGroups["Conference"]?.entries?.firstOrNull { (_, conferenceResults) ->
            conferenceResults.any { it.franchiseId == result.franchiseId }
        }
        val confName = confEntry?.key
        val confPosition = if (confName != null) getPosition(result, minMaxInfo[confName]) else null

        // Determine team's division name and position
        val divEntry = resultGroups["Division"]?.entries?.firstOrNull { (_, divisionResults ) ->
            divisionResults.any { it.franchiseId == result.franchiseId }
        }
        val divName = divEntry?.key
        val divPosition = if (divName != null) getPosition(result, minMaxInfo[divName]) else null

        returnList.add(
            ProtoFranchiseSeason(
                chapter.teamName,
                chapter.metroId,
                seasonId,
                result.franchiseId,
                chapter.leagueId,
                overallPosition,
                confPosition,
                divPosition,
                confName,
                divName,
                request.postseason?.isInPostseason(result.franchiseId),
                request.postseason?.roundsWon(result.franchiseId),
                request.postseason?.reachedChampionship(result.franchiseId),
                request.postseason?.wonChampionship(result.franchiseId)
            )
        )
    }

    return returnList
}

private fun getAllResultGroups(standings: ResultGroup, resultingGroups: HashMap<String, HashMap<String, List<Result>>>, depth: Int = 0): List<Result> {
    val type = when(depth) {
        0 -> "Overall"
        1 -> "Conference"
        2 -> "Division"
        else -> "Unknown"
    }
    val name = standings.name ?: "Overall"

    // Build the results list - it will either contain what's in standings, or the combined standings of all subgroups
    val resultsList = if (!standings.results.isNullOrEmpty()) {
        standings.results
    } else {
        standings.subGroups?.flatMap { getAllResultGroups(it, resultingGroups, depth + 1) } ?: listOf()
    }

    // Store conference or division groups in resultingGroups - we don't need to store overall, because the eventual return
    // value will contain the overall standings, so we skip this for depth == 0
    if (depth > 0) {
        resultingGroups[type]?.let { it[name] = resultsList }
    }

    return resultsList
}

private fun getPosition(result: Result, minMaxInfo: MinMaxInfo?): Standing? =
    when {
        result.winningRate == minMaxInfo?.max && minMaxInfo.maxCount == 1 -> Standing.FIRST
        result.winningRate == minMaxInfo?.max && minMaxInfo.maxCount > 1 -> Standing.FIRST_TIED
        result.winningRate == minMaxInfo?.min && minMaxInfo.minCount == 1 -> Standing.LAST
        result.winningRate == minMaxInfo?.min && minMaxInfo.minCount > 1 -> Standing.LAST_TIED
        else -> null
    }

private fun createMinMaxInfo(results: List<Result>): MinMaxInfo {
    val max = results.maxOf { it.winningRate }
    val min = results.minOf { it.winningRate }

    return MinMaxInfo(
        max,
        min,
        results.count { it.winningRate == max },
        results.count { it.winningRate == min }
    )
}

private fun List<Chapter>.getChapter(franchiseId: Int, year: Int): Chapter? =
    this.filter { it.franchiseId == franchiseId }
        .filter { it.startYear <= year }
        .firstOrNull { year <= (it.endYear ?: Calendar.getInstance().get(Calendar.YEAR)) }

private fun Postseason.isInPostseason(franchiseId: Int) =
    this.rounds.any { r -> r.matchups.any { m -> m.winner.franchiseId == franchiseId || m.loser.franchiseId == franchiseId } }

private fun Postseason.roundsWon(franchiseId: Int) =
    if (this.isInPostseason(franchiseId)) this.rounds.filter { r -> r.matchups.any { m -> m.winner.franchiseId == franchiseId } }.size else null

private fun Postseason.reachedChampionship(franchiseId: Int) =
    if (this.isInPostseason(franchiseId)) {
        this.rounds.first { r -> r.matchups.size == 1 && r.matchups.first().isChampionshipRound == true }
            .matchups.all { it.winner.franchiseId == franchiseId || it.loser.franchiseId == franchiseId }
    } else null

private fun Postseason.wonChampionship(franchiseId: Int) =
    if (this.isInPostseason(franchiseId)) {
        this.rounds.first { r -> r.matchups.size == 1 && r.matchups.first().isChampionshipRound == true }
            .matchups.all { it.winner.franchiseId == franchiseId }
    } else null