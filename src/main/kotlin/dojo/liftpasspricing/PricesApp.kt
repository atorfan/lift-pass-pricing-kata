package dojo.liftpasspricing

import dojo.liftpasspricing.infrastructure.apiRoutesConfiguration
import io.javalin.Javalin

object PricesApp {

    private lateinit var app: Javalin

    fun start() {
        app = Javalin.create { config ->
            config.http.defaultContentType = "application/json"
            config.router.apiBuilder(::apiRoutesConfiguration)
        }
            .start(4567)
    }

    fun shutdown() {
        app.stop()
    }
}
