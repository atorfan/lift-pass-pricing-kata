package dojo.liftpasspricing

import dojo.liftpasspricing.infrastructure.getPriceRoute
import dojo.liftpasspricing.infrastructure.putBasePriceRoute
import spark.Filter
import spark.Request
import spark.Response
import spark.Spark

object PricesApp {

    @JvmStatic
    fun create() {
        Spark.port(4567)

        Spark.get("/prices", getPriceRoute())
        Spark.put("/prices", putBasePriceRoute())

        Spark.after(Filter { _: Request?, res: Response -> res.type("application/json") })
    }
}
