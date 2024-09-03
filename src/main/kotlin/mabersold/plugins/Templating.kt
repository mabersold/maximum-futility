package mabersold.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import mabersold.dao.FranchiseSeasonDAOImpl
import mabersold.models.api.MetricType
import mabersold.services.MetroDataService
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureTemplating() {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }

    val seasonDataService by inject<SeasonDataService>()
    val metroDataService by inject<MetroDataService>()

    routing {
        get("/metros") {
            val metricType =
                call.request.queryParameters["metricType"]?.let { metricType -> MetricType.valueOf(metricType) }
                    ?: MetricType.TOTAL_CHAMPIONSHIPS
            val from = call.request.queryParameters["from"]?.toInt()
            val until = call.request.queryParameters["until"]?.toInt()

            val dao = FranchiseSeasonDAOImpl()
            val metroData = metroDataService.getMetroDataByMetric(metricType, from, until)
            val activeMetros = dao.activeMetros()

            val metroDataWithActiveMetros = metroData.filter { activeMetros.contains(it.name) }
            val allMetricTypes = MetricType.entries

            val excludedMetros = metroData.filter { !activeMetros.contains(it.name) }.map { it.name }
            val yearRange = seasonDataService.getYearRange()

            call.respond(
                ThymeleafContent(
                    "metros",
                    mapOf(
                        "metros" to metroDataWithActiveMetros,
                        "type" to metricType.displayName,
                        "from" to (from ?: yearRange.startYear),
                        "until" to (until ?: yearRange.endYear),
                        "yearRange" to yearRange,
                        "metricTypes" to allMetricTypes,
                        "excludedMetros" to excludedMetros.joinToString { it }
                    )
                )
            )
        }
        get("/season") {
            val seasonId = call.request.queryParameters["id"]?.toInt() ?: 1

            val seasonSummary = seasonDataService.getSeasonSummary(seasonId)

            call.respond(
                ThymeleafContent("season", mapOf("season" to seasonSummary, "nextSeason" to seasonId + 1))
            )
        }
    }
}