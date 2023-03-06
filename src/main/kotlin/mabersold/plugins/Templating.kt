package mabersold.plugins

import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mabersold.models.League
import mabersold.services.FranchiseDataService
import mabersold.services.FranchiseToCityMapper

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
                League.NBA to listOf("data/basketball/nba.json")
            )

            val franchises = FranchiseDataService().getFranchiseData(leagueMap)
            val cities = FranchiseToCityMapper().mapToCities(franchises).sortedBy { it.metroArea.displayName }

            call.respond(ThymeleafContent("index", mapOf("cities" to cities)))
        }
        get("/franchises") {
            call.respond(
                ThymeleafContent(
                    "franchises",
                    mapOf("franchises" to FranchiseDataService().getFranchiseData().map { it.withLeague(League.MLB) }.sortedBy { it.name })
                )
            )
        }
    }
}