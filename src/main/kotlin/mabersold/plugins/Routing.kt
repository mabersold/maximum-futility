package mabersold.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import mabersold.models.api.MetricType
import mabersold.models.api.MetroData
import mabersold.models.api.MetroOptions
import mabersold.models.api.MetroReport
import mabersold.models.api.requests.SaveFranchiseRequest
import mabersold.models.api.requests.SaveLeagueRequest
import mabersold.services.FranchiseDataService
import mabersold.services.LeagueDataService
import mabersold.services.MetroDataService
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val seasonDataService by inject<SeasonDataService>()
    val leagueDataService by inject<LeagueDataService>()
    val metroDataService by inject<MetroDataService>()
    val franchiseDataService by inject<FranchiseDataService>()

    routing {
        get("/validate") {
            call.respondText("{}")
        }
        get("/metro_options") {
            val options = MetroOptions(
                yearRange = seasonDataService.getYearRange(),
                leagues = leagueDataService.all(),
                metrics = MetricType.values().map { mapOf("name" to it.name, "display_name" to it.displayName) }
            )
            call.respond(options)
        }
        get("metro_report") {
            val metricType =
                call.request.queryParameters["metricType"]?.let { metricType -> MetricType.valueOf(metricType) }
                    ?: MetricType.TOTAL_CHAMPIONSHIPS

            val minLastActiveYear = call.request.queryParameters["minLastActiveYear"]?.toInt()

            val startYear = call.request.queryParameters["startYear"]?.toInt() ?: seasonDataService.getYearRange().startYear
            val endYear = call.request.queryParameters["endYear"]?.toInt() ?: seasonDataService.getYearRange().endYear

            val leagues = call.request.queryParameters.getAll("leagueId")?.map { it.toInt() }?.toSet() ?: emptySet()

            val leaguesIncluded = leagueDataService.all().filter { leagues.isEmpty() || leagues.contains(it.id) }

            val data = metroDataService.getMetroDataByMetric(metricType, startYear, endYear, leagues)
                .filter { minLastActiveYear == null || it.lastActiveYear >= minLastActiveYear}
                .sortedWith(compareBy<MetroData>{ it.rate }.thenByDescending { it.opportunities })

            val metroReport = MetroReport(
                startYear,
                endYear,
                metricType,
                leaguesIncluded,
                data
            )
            call.respond(metroReport)
        }
        get("/season_report/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val report = seasonDataService.getSeasonReport(id)

            call.respond(report)
        }
        route("/leagues") {
            get {
                val leagues = leagueDataService.all()
                call.respond(leagues)
            }
            get("/{leagueId}/seasons") {
                val leagueId = call.parameters["leagueId"]?.toIntOrNull()
                if (leagueId == null) {
                    call.respond(HttpStatusCode.UnprocessableEntity, "League ID is required")
                    return@get
                }

                val seasons = seasonDataService.getSeasons(leagueId).sortedBy { it.startYear }

                call.respond(seasons)
            }
            post {
                val request = call.receive<SaveLeagueRequest>()
                if (!request.canCreate()) {
                    call.respond(HttpStatusCode.UnprocessableEntity, "Invalid request: league name and sport are required")
                }

                val created = leagueDataService.create(request.name!!, request.sport!!)
                created?.let {
                    call.respond(it)
                }
            }
            patch("/{leagueId}") {
                val leagueId = call.parameters["leagueId"]?.toIntOrNull()
                if (leagueId == null) {
                    call.respond(HttpStatusCode.UnprocessableEntity, "League ID is required")
                    return@patch
                }

                val request = call.receive<SaveLeagueRequest>()
                val updated = leagueDataService.update(leagueId, request.name, request.sport)
                updated?.let {
                    call.respond(it)
                }
            }
            delete("/{leagueId}") {
                val leagueId = call.parameters["leagueId"]?.toIntOrNull()
                if (leagueId == null) {
                    call.respond(HttpStatusCode.UnprocessableEntity, "League ID is required")
                    return@delete
                }

                val isDeleted = leagueDataService.delete(leagueId)
                if (isDeleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
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
}
