package mabersold.plugins.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mabersold.models.api.MetricType
import mabersold.models.api.MetroData
import mabersold.models.api.MetroOptions
import mabersold.models.api.MetroReport
import mabersold.services.LeagueDataService
import mabersold.services.MetroDataService
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject

fun Route.metroRoutes() {
    val seasonDataService by inject<SeasonDataService>()
    val leagueDataService by inject<LeagueDataService>()
    val metroDataService by inject<MetroDataService>()

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
}