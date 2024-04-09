package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.domain.BasePriceRepository

class BasePriceRepositoryWithSqlDatabase : BasePriceRepository {

    override fun retrieveFor(priceType: String) =
        queryFromDatabase(
            "SELECT cost FROM base_price WHERE type = ?",
            { pStmt -> pStmt.setString(1, priceType) },
            { resultSet -> resultSet.next(); resultSet.getInt(1) }
        )

    override fun storeFor(liftPassType: String, liftPassCost: Int) {
        upsertDatabase("INSERT INTO base_price (type, cost) VALUES (?, ?) ON DUPLICATE KEY UPDATE cost = ?")
        { stmt ->
            stmt.setString(1, liftPassType)
            stmt.setInt(2, liftPassCost)
            stmt.setInt(3, liftPassCost)
            stmt.execute()
        }
    }
}
