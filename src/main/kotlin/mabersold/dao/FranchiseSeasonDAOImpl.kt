package mabersold.dao

import mabersold.dao.DatabaseFactory.dbQuery
import mabersold.models.db.FranchiseSeason
import mabersold.models.db.FranchiseSeasons
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

class FranchiseSeasonDAOImpl : FranchiseSeasonDAO {
    private fun resultRowToFranchiseSeason(row: ResultRow) = FranchiseSeason(
        id = row[FranchiseSeasons.id].value,
        seasonId = row[FranchiseSeasons.seasonId].value,
        franchiseId = row[FranchiseSeasons.franchiseId].value,
        metroId = row[FranchiseSeasons.metroId].value,
        teamName = row[FranchiseSeasons.teamName],
        leagueId = row[FranchiseSeasons.leagueId].value,
        conference = row[FranchiseSeasons.conference],
        division = row[FranchiseSeasons.division],
        leaguePosition = row[FranchiseSeasons.leaguePosition],
        conferencePosition = row[FranchiseSeasons.conferencePosition],
        divisionPosition = row[FranchiseSeasons.divisionPosition]
    )

    override suspend fun all(): List<FranchiseSeason> = dbQuery {
        FranchiseSeasons.selectAll().map(::resultRowToFranchiseSeason)
    }

    override suspend fun get(id: Int): FranchiseSeason? {
        return null
    }
}