package mabersold.plugins.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject

fun Route.seasonRoutes() {
    val seasonDataService by inject<SeasonDataService>()

    get("/season_report/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val report = seasonDataService.getSeasonReport(id)

        call.respond(report)
    }
}