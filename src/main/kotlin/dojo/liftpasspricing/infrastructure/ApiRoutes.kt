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
        val ages = req.queryParamsValues("ages")?.map(String::toInt) ?: emptyList()
        val requestedDate = req.queryParams("date")?.let {LocalDate.parse(it)}

        val costCalculator = dependency(CostCalculator::class)
        val cost = costCalculator.calculateFor(priceType, ages, requestedDate)

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
