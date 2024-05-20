package mabersold.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import mabersold.models.api.MetricType
import mabersold.models.api.MetroData
import mabersold.models.api.MetroOptions
import mabersold.models.api.MetroReport
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
                .filter { minLastActiveYear == null || it.lastActiveYear > minLastActiveYear}
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
        get("/leagues") {
            val leagues = leagueDataService.all()
            call.respond(leagues)
        }
        get("/leagues/{leagueId}/franchises") {
            val leagueId = call.parameters["leagueId"]?.toInt() ?: throw IllegalArgumentException("Invalid League ID")
            val franchises = franchiseDataService.getFranchises(leagueId)

            call.respond(franchises)
        }
        get("/leagues/{leagueId}/seasons") {
            val leagueId = call.parameters["leagueId"]?.toInt() ?: throw IllegalArgumentException("Invalid League ID")
            val seasons = seasonDataService.getSeasons(leagueId).sortedBy { it.startYear }

            call.respond(seasons)
        }
    }
}
