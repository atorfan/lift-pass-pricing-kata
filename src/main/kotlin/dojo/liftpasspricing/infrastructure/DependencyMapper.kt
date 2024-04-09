package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.application.CostCalculator
import dojo.liftpasspricing.domain.BasePriceRepository
import dojo.liftpasspricing.domain.HolidaysRepository
import java.sql.DriverManager
import kotlin.reflect.KClass

private val dependencies = buildMap {
    // TODO use a connection pool solution
    // TODO user & password from environment variables
    val databaseConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql")

    val basePriceRepository = BasePriceRepositoryWithSqlDatabase(databaseConnection)
    put(BasePriceRepository::class, basePriceRepository)

    val holidaysRepository = HolidaysRepositoryWithSqlDatabase(databaseConnection)
    put(HolidaysRepository::class, holidaysRepository)

    put(CostCalculator::class, CostCalculator(basePriceRepository, holidaysRepository))
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> dependency(clazz: KClass<T>): T {
    return dependencies[clazz] as T
}
