package mabersold.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import mabersold.plugins.routes.franchiseRoutes
import mabersold.plugins.routes.leagueRoutes
import mabersold.plugins.routes.metroRoutes
import mabersold.plugins.routes.seasonRoutes

fun Application.configureRouting() {
    routing {
        get("/validate") {
            call.respondText("{}")
        }
        route("/api/v1") {
            metroRoutes()
            seasonRoutes()
            leagueRoutes()
            franchiseRoutes()
        }
    }
}
