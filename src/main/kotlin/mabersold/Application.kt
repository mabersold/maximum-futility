package mabersold

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mabersold.dao.DatabaseFactory
import mabersold.plugins.configureRouting
import mabersold.plugins.configureTemplating

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module(args)
    }.start(wait = true)
}

fun Application.module(args: Array<String>) {
    val createSchema = args.isNotEmpty() && args.contains("--createSchema")
    DatabaseFactory.init(createSchema)
    configureTemplating()
    configureRouting()
}
