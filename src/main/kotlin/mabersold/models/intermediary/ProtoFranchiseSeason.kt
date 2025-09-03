package mabersold.models.intermediary

import mabersold.models.db.Standing

data class ProtoFranchiseSeason(
    val teamName: String,
    val metroId: Int,
    val seasonId: Int,
    val franchiseId: Int,
    val leagueId: Int,
    val leaguePosition: Standing?,
    val conferencePosition: Standing?,
    val divisionPosition: Standing?,
    val conferenceName: String?,
    val divisionName: String?,
    val qualifiedForPostseason: Boolean?,
    val roundsWon: Int?,
    val appearedInChampionship: Boolean?,
    val wonChampionship: Boolean?
)
