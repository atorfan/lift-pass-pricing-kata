package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.application.CostCalculator
import dojo.liftpasspricing.domain.BasePriceRepository
import spark.Request
import spark.Response
import spark.Route
import java.time.LocalDate

fun getPriceRoute(): Route {
    return Route { req: Request, _: Response? ->
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

fun putBasePriceRoute(): Route {
    return Route { req: Request, _: Response? ->
        val liftPassCost = req.queryParams("cost").toInt()
        val liftPassType = req.queryParams("type")

        val basePriceRepository = dependency(BasePriceRepository::class)
        basePriceRepository.storeFor(liftPassType, liftPassCost)

        ""
    }
}
