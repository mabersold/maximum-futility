package mabersold.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import mabersold.models.api.MetricType
import mabersold.models.api.MetroData
import mabersold.models.api.MetroReport
import mabersold.services.LeagueDataService
import mabersold.services.MetroDataService
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val seasonDataService by inject<SeasonDataService>()
    val leagueDataService by inject<LeagueDataService>()
    val metroDataService by inject<MetroDataService>()

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
        get("/metrics") {
            val metrics = MetricType.values().map { mapOf("name" to it.name, "display_name" to it.displayName) }
            call.respond(metrics)
        }
        get("metro_report") {
            val metricType =
                call.request.queryParameters["metricType"]?.let { metricType -> MetricType.valueOf(metricType) }
                    ?: MetricType.TOTAL_CHAMPIONSHIPS

            val startYear = call.request.queryParameters["startYear"]?.toInt() ?: seasonDataService.getYearRange().startYear
            val endYear = call.request.queryParameters["endYear"]?.toInt() ?: seasonDataService.getYearRange().endYear

            val data = metroDataService.getMetroDataByMetric(metricType, startYear, endYear)
                .sortedWith(compareBy({ it.rate }, { it.opportunities}))

            val metroReport = MetroReport(
                startYear,
                endYear,
                metricType,
                data
            )
            call.respond(metroReport)
        }
    }
}
