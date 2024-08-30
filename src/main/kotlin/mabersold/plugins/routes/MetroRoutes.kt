package mabersold.plugins.routes

import com.opencsv.CSVWriter
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.server.application.call
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.io.OutputStreamWriter
import java.security.MessageDigest
import java.time.Instant
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

    data class ReportResults(
        val metricType: MetricType,
        val startYear: Int,
        val endYear: Int,
        val leagues: Set<Int>,
        val results: List<MetroData>
    )

    suspend fun getReportResults(queryParameters: Parameters): ReportResults {
        val metricType = queryParameters["metricType"]?.let { metricType -> MetricType.valueOf(metricType) }
            ?: MetricType.TOTAL_CHAMPIONSHIPS

        val startYear = queryParameters["startYear"]?.toInt() ?: seasonDataService.getYearRange().startYear
        val endYear = queryParameters["endYear"]?.toInt() ?: seasonDataService.getYearRange().endYear
        val leagues = queryParameters.getAll("leagueId")?.map { it.toInt() }?.toSet() ?: emptySet()
        val minLastActiveYear = queryParameters["minLastActiveYear"]?.toInt()

        return ReportResults(
            metricType,
            startYear,
            endYear,
            leagues,
            metroDataService.getMetroDataByMetric(metricType, startYear, endYear, leagues)
                .filter { minLastActiveYear == null || it.lastActiveYear >= minLastActiveYear}
                .sortedWith(compareBy<MetroData>{ it.rate }.thenByDescending { it.opportunities })
        )
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
        val (metricType, startYear, endYear, leagues, data) = getReportResults(call.request.queryParameters)

        call.respond(
            MetroReport(
                startYear,
                endYear,
                metricType,
                leagueDataService.all().filter { leagues.isEmpty() || leagues.contains(it.id) },
                data
            )
        )
    }
    get("metro_report_csv") {
        fun generateShortHashFromTimestamp(): String {
            val timestamp = Instant.now().toString()
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(timestamp.toByteArray())
            return hashBytes.joinToString("") { "%02x".format(it) }.substring(0, 8)
        }

        val (metricType, startYear, endYear, _, data) = getReportResults(call.request.queryParameters)

        val hash = generateShortHashFromTimestamp()
        val filename = "metro_report-$metricType-$startYear-$endYear-$hash.csv"

        call.response.header(
            "Content-Disposition",
            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, filename).toString()
        )

        call.respondOutputStream(ContentType.Text.CSV) {
            val writer = OutputStreamWriter(this)
            val csvWriter = CSVWriter(writer)

            csvWriter.writeNext(arrayOf("Metro Area", "Total", "Opportunities", "Rate"))

            data.forEach { metroData ->
                csvWriter.writeNext(arrayOf(
                    metroData.name,
                    metroData.total.toString(),
                    metroData.opportunities.toString(),
                    String.format("%.2f%%", metroData.rate?.times(100) ?: 0)
                ))
            }
            csvWriter.close()
        }
    }
}