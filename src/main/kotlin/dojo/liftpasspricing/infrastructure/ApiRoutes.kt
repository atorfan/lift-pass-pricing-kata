package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.application.CostCalculator
import dojo.liftpasspricing.domain.BasePriceRepository
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.Context
import java.time.LocalDate

fun apiRoutesConfiguration() {
    path("/prices") {
        get(::getPriceHandler)
        put(::putBasePriceHandler)
    }
}

fun getPriceHandler(context: Context) {
    val priceType = context.queryParam("type")!!
    val ages = context.queryParams("ages").map(String::toInt)
    val requestedDate = context.queryParam("date")?.let { LocalDate.parse(it) }

    val costCalculator = dependency(CostCalculator::class)
    val cost = costCalculator.calculateFor(priceType, ages, requestedDate)

    context.result("{ \"cost\": $cost }")
}

fun putBasePriceHandler(context: Context) {
    val liftPassCost = context.queryParam("cost")!!.toInt()
    val liftPassType = context.queryParam("type")!!

    val basePriceRepository = dependency(BasePriceRepository::class)
    basePriceRepository.storeFor(liftPassType, liftPassCost)
}
