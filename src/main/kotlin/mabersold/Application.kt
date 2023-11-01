package mabersold

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mabersold.dao.DatabaseFactory
import mabersold.dao.FranchiseSeasonDAO
import mabersold.dao.FranchiseSeasonDAOImpl
import mabersold.dao.SeasonDAO
import mabersold.dao.SeasonDAOImpl
import mabersold.plugins.configureRouting
import mabersold.plugins.configureTemplating
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
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    val createSchema = args.isNotEmpty() && args.contains("--createSchema")
    DatabaseFactory.init(createSchema)
    configureTemplating()
    configureRouting()
}

val appModule = module {
    singleOf(::FranchiseSeasonDAOImpl) { bind<FranchiseSeasonDAO>() }
    singleOf(::SeasonDAOImpl) { bind<SeasonDAO>() }
    singleOf(::SeasonDataService)
    singleOf(::MetroDataService)
}
