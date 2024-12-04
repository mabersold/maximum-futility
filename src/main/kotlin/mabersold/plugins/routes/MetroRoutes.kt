package mabersold.plugins.routes

import com.opencsv.CSVWriter
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.io.OutputStreamWriter
import mabersold.models.api.requests.CreateMetroRequest
import mabersold.services.MetroDataService
import org.koin.ktor.ext.inject

fun Route.metroRoutes() {
    val metroDataService by inject<MetroDataService>()

    route("/metros") {
        get {
            val metros = metroDataService.getMetros()
            call.respond(metros)
        }
        post {
            val request = call.receive<CreateMetroRequest>()
            val created = metroDataService.create(request.name, request.label)
            created?.let {
                call.respond(it)
            }
        }
        get("/csv") {
            val metros = metroDataService.getMetros()

            call.response.header(
                "Content-Disposition",
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "metros.csv").toString()
            )

            call.respondOutputStream(ContentType.Text.CSV) {
                val writer = OutputStreamWriter(this)
                val csvWriter = CSVWriter(writer)
                csvWriter.writeNext(arrayOf("Name"))

                metros.forEach { metro ->
                    csvWriter.writeNext(arrayOf(
                        metro.name
                    ))
                }
                csvWriter.close()
            }
        }
    }
}