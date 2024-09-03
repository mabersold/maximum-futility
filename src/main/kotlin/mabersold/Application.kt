package mabersold

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import mabersold.dao.DatabaseFactory
import mabersold.dao.FranchiseDAO
import mabersold.dao.FranchiseDAOImpl
import mabersold.dao.FranchiseSeasonDAO
import mabersold.dao.FranchiseSeasonDAOImpl
import mabersold.dao.LeagueDAO
import mabersold.dao.LeagueDAOImpl
import mabersold.dao.MetroDAO
import mabersold.dao.MetroDAOImpl
import mabersold.dao.SeasonDAO
import mabersold.dao.SeasonDAOImpl
import mabersold.plugins.configureRouting
import mabersold.plugins.configureTemplating
import mabersold.services.FranchiseDataService
import mabersold.services.LeagueDataService
import mabersold.services.MetroDataService
import mabersold.services.SeasonDataService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        main(args)
    }.start(wait = true)
}

fun Application.main(args: Array<String>) {
    install(ContentNegotiation) {
        json()
    }
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    install(CORS) {
        anyHost()
    }

    val createSchema = args.isNotEmpty() && args.contains("--createSchema")
    DatabaseFactory.init(createSchema)
    configureTemplating()
    configureRouting()
}

val appModule = module {
    singleOf(::FranchiseSeasonDAOImpl) { bind<FranchiseSeasonDAO>() }
    singleOf(::SeasonDAOImpl) { bind<SeasonDAO>() }
    singleOf(::LeagueDAOImpl) { bind<LeagueDAO>() }
    singleOf(::FranchiseDAOImpl) { bind<FranchiseDAO>() }
    singleOf(::MetroDAOImpl) { bind<MetroDAO>() }
    singleOf(::SeasonDataService)
    singleOf(::MetroDataService)
    singleOf(::LeagueDataService)
    singleOf(::FranchiseDataService)
}
