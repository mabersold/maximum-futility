package mabersold.plugins.routes

import com.opencsv.CSVWriter
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.io.OutputStreamWriter
import mabersold.models.api.requests.SaveLeagueRequest
import mabersold.services.LeagueDataService
import mabersold.services.SeasonDataService
import org.koin.ktor.ext.inject

fun Route.leagueRoutes() {
    val leagueDataService by inject<LeagueDataService>()
    val seasonDataService by inject<SeasonDataService>()

    route("/leagues") {
        get {
            val leagues = leagueDataService.all()
            call.respond(leagues)
        }
        get("/csv") {
            val leagues = leagueDataService.all()

            call.response.header(
                "Content-Disposition",
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "leagues.csv").toString()
            )

            call.respondOutputStream(ContentType.Text.CSV) {
                val writer = OutputStreamWriter(this)
                val csvWriter = CSVWriter(writer)
                csvWriter.writeNext(arrayOf("Name", "Sport"))

                leagues.forEach { league ->
                    csvWriter.writeNext(arrayOf(
                        league.name,
                        league.sport
                    ))
                }
                csvWriter.close()
            }
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

            val created = leagueDataService.create(request.name!!, request.sport!!, request.label!!)
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
}