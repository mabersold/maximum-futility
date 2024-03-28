package mabersold.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import mabersold.services.LeagueDataService
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val seasonDataService by inject<SeasonDataService>()
    val leagueDataService by inject<LeagueDataService>()

    routing {
        get("/validate") {
            call.respondText("{}")
        }
        get("/year_range") {
            val range = seasonDataService.getYearRange()
            call.respond(range)
        }
        get("/leagues") {
            val leagues = leagueDataService.all()
            call.respond(leagues)
        }
    }
}
