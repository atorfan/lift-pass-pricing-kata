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

    override fun storeFor(liftPassType: String, liftPassCost: Int) {
        obtainDatabaseConnection().use {
            it.prepareStatement("INSERT INTO base_price (type, cost) VALUES (?, ?) ON DUPLICATE KEY UPDATE cost = ?")
            .use { stmt ->
                stmt.setString(1, liftPassType)
                stmt.setInt(2, liftPassCost)
                stmt.setInt(3, liftPassCost)
                stmt.execute()
            }
        }
    }
}
