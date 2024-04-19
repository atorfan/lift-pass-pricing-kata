package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.application.CostCalculator
import dojo.liftpasspricing.domain.BasePriceRepository
import dojo.liftpasspricing.domain.LiftPassCalendarRepository
import kotlin.reflect.KClass

private val dependencies = buildMap {
    val basePriceRepository = BasePriceRepositoryWithSqlDatabase()
    put(BasePriceRepository::class, basePriceRepository)

    val holidaysRepository = LiftPassCalendarRepositoryWithSqlDatabase()
    put(LiftPassCalendarRepository::class, holidaysRepository)

    put(CostCalculator::class, CostCalculator(basePriceRepository, holidaysRepository))
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> dependency(clazz: KClass<T>): T {
    return dependencies[clazz] as T
}
