package mabersold.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import mabersold.models.api.MetricType
import mabersold.models.api.MetroOptions
import mabersold.services.LeagueDataService
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Application.configureTemplating() {
    val seasonDataService by inject<SeasonDataService>()
    val leagueDataService by inject<LeagueDataService>()

    routing {
        staticResources("/", "static")

        get("/") {
            val options = MetroOptions(
                yearRange = seasonDataService.getYearRange(),
                leagues = leagueDataService.all(),
                metrics = MetricType.entries.map { mapOf("name" to it.name, "display_name" to it.displayName) }
            )

            call.respond(MustacheContent("index.hbs", mapOf("name" to "Mark Abersold", "options" to options)))
        }
    }
}