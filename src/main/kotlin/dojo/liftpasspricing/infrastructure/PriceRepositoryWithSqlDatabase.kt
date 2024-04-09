package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.domain.BasePriceRepository
import java.sql.Connection

class BasePriceRepositoryWithSqlDatabase(private val connection: Connection) : BasePriceRepository {

    override fun retrieveFor(priceType: String): Int {
        var basePrice: Int
        connection
            .prepareStatement("SELECT cost FROM base_price WHERE type = ?")
            .use { costStmt ->
                costStmt.setString(1, priceType)
                costStmt.executeQuery().use { result ->
                    result.next()
                    basePrice = result.getInt("cost")
                }
            }
        return basePrice
    }
}
