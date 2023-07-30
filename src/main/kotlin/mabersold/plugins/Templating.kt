package mabersold.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import mabersold.models.League
import mabersold.models.MetricType
import mabersold.services.FranchiseDataService
import mabersold.services.FranchiseToCityMapper
import mabersold.services.MetroDataService
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureTemplating() {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }

    routing {
        get("/") {
            val leagueMap = mapOf(
                League.MLB to listOf("data/baseball/franchises.json"),
                League.NFL to listOf("data/football/pre-super-bowl-nfl.json", "data/football/super-bowl-era-nfl.json"),
                League.NBA to listOf("data/basketball/nba.json"),
                League.NHL to listOf("data/hockey/nhl.json")
            )

            val startYear = call.request.queryParameters["startYear"]?.toInt() ?: League.MLB.firstSeason
            val endYear = call.request.queryParameters["endYear"]?.toInt() ?: League.MLB.mostRecentFinishedSeason

            val franchises = FranchiseDataService().getFranchiseData(leagueMap, startYear, endYear)
            val cities = FranchiseToCityMapper().mapToCities(franchises).sortedBy { it.metroArea.displayName }

            call.respond(ThymeleafContent("index", mapOf("cities" to cities)))
        }
        get("/franchises") {
            call.respond(
                ThymeleafContent(
                    "franchises",
                    mapOf("franchises" to FranchiseDataService().getFranchiseData().map { it.withLeague(League.MLB) }
                        .sortedBy { it.name })
                )
            )
        }
        get("/metros") {
            val metricType = call.request.queryParameters["metricType"]?.let { metricType -> MetricType.valueOf(metricType) } ?: MetricType.BEST_OVERALL
            val metroDataService = MetroDataService()
            val metroData = metroDataService.getMetroData(metricType)
            val allMetricTypes = MetricType.values().toList()

            call.respond(ThymeleafContent("metros", mapOf("metros" to metroData, "type" to metricType.displayName, "metricTypes" to allMetricTypes)))
        }
    }
}