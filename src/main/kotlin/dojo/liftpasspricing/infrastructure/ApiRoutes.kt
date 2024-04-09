package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.application.CostCalculator
import spark.Request
import spark.Response
import spark.Route
import java.time.LocalDate

fun getPrices(): Route {
    return Route { req: Request, res: Response? ->
        val priceType = req.queryParams("type")
        val age = req.queryParams("age")?.toInt()
        val requestedDate =
            if (req.queryParams("date") != null)
                LocalDate.parse(req.queryParams("date"))
            else
                null

        val costCalculator = dependency(CostCalculator::class)
        val cost = costCalculator.calculateFor(priceType, age, requestedDate)

        "{ \"cost\": $cost }"
    }
}
