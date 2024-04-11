package dojo.liftpasspricing

import dojo.liftpasspricing.infrastructure.getPriceRoute
import dojo.liftpasspricing.infrastructure.putBasePriceRoute
import spark.Filter
import spark.Request
import spark.Response
import spark.Spark.*

object PricesApp {

    fun start() {
        port(4567)

        get("/prices", getPriceRoute())
        put("/prices", putBasePriceRoute())

        after(Filter { _: Request?, res: Response -> res.type("application/json") })
    }

    fun shutdown() {
        stop()
    }
}
