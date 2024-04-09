package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.domain.BasePriceRepository

class BasePriceRepositoryWithSqlDatabase : BasePriceRepository {

    override fun retrieveFor(priceType: String): Int {
        var basePrice: Int
        obtainDatabaseConnection().use {
            it.prepareStatement("SELECT cost FROM base_price WHERE type = ?")
            .use { costStmt ->
                costStmt.setString(1, priceType)
                costStmt.executeQuery().use { result ->
                    result.next()
                    basePrice = result.getInt("cost")
                }
            }
        }
        return basePrice
    }
}
