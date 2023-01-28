package mabersold.plugins

import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mabersold.services.FranchiseDataService

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
            call.respond(ThymeleafContent("index", mapOf()))
        }
        get("/franchises") {
            call.respond(ThymeleafContent("franchises", mapOf("franchises" to FranchiseDataService().getFranchiseData())))
        }
    }
}