package mabersold.plugins.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import mabersold.models.api.requests.SaveFranchiseRequest
import mabersold.services.FranchiseDataService
import org.koin.ktor.ext.inject

fun Route.franchiseRoutes() {
    val franchiseDataService by inject<FranchiseDataService>()

    route("/franchises") {
        get {
            val leagueId = call.request.queryParameters["leagueId"]?.toIntOrNull()
            if (leagueId == null) {
                call.respond(HttpStatusCode.UnprocessableEntity, "League ID is required")
                return@get
            }

            val franchises = franchiseDataService.getFranchises(leagueId)

            call.respond(franchises)
        }
        post {
            val request = call.receive<SaveFranchiseRequest>()
            if (!request.canCreate()) {
                call.respond(HttpStatusCode.UnprocessableEntity, "Invalid request: name, is_defunct, and league_id must be provided")
                return@post
            }

            val created = franchiseDataService.createFranchise(request.name!!, request.isDefunct!!, request.leagueId!!)
            created?.let {
                call.respond(it)
            }
        }
        patch("/{franchiseId}") {
            val franchiseId = call.parameters["franchiseId"]?.toIntOrNull()
            if (franchiseId == null) {
                call.respond(HttpStatusCode.UnprocessableEntity, "Invalid or missing franchiseId")
                return@patch
            }

            val request = call.receive<SaveFranchiseRequest>()

            franchiseDataService.updateFranchise(franchiseId, request.name, request.isDefunct, request.leagueId)?.let { updated ->
                call.respond(updated)
            }
        }
        delete("/{franchiseId}") {
            val franchiseId = call.parameters["franchiseId"]?.toIntOrNull()
            if (franchiseId == null) {
                call.respond(HttpStatusCode.UnprocessableEntity, "Invalid or missing franchiseId")
                return@delete
            }

            val isDeleted = franchiseDataService.deleteFranchise(franchiseId)

            if (isDeleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}