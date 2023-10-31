package mabersold.models

import mabersold.models.db.Standing

data class FranchiseSeasonInfo(
    val metro: Metro,
    val teamName: String,
    val conference: String?,
    val division: String?,
    val leaguePosition: Standing?,
    val conferencePosition: Standing?,
    val divisionPosition: Standing?,
    val qualifiedForPostseason: Boolean?,
    val postSeasonRounds: Int?,
    val roundsWon: Int?,
    val appearedInChampionship: Boolean?,
    val wonChampionship: Boolean?,
    val totalConferences: Int,
    val totalDivisions: Int
)
