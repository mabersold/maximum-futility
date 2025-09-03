package mabersold.plugins.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import mabersold.models.api.requests.CreateSeasonRequest
import mabersold.services.CreateSeasonDataService
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject

fun Route.seasonRoutes() {
    val seasonDataService by inject<SeasonDataService>()
    val createSeasonDataService by inject<CreateSeasonDataService>()

    get("/season_report/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val report = seasonDataService.getSeasonReport(id)

        call.respond(report)
    }

    post("/seasons") {
        val request = call.receive<CreateSeasonRequest>()
        createSeasonDataService.addSeason(request)
        call.respond("")
    }

    get("/seasons") {
        val leagueId = call.request.queryParameters["league_id"]?.toIntOrNull()
        if (leagueId == null) {
            call.respond(HttpStatusCode.UnprocessableEntity, "League ID is required")
            return@get
        }

        val seasons = seasonDataService.getSeasons(leagueId)
        call.respond(seasons)
    }
}