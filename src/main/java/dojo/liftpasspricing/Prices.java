package dojo.liftpasspricing;

import dojo.liftpasspricing.infrastructure.DatabaseConnectionFactoryKt;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static dojo.liftpasspricing.infrastructure.ApiRoutesKt.getPrices;
import static spark.Spark.*;

public class Prices {

    public static void createApp() {

        port(4567);

        put("/prices", (req, res) -> {
            int liftPassCost = Integer.parseInt(req.queryParams("cost"));
            String liftPassType = req.queryParams("type");

            try (Connection connection = DatabaseConnectionFactoryKt.obtainDatabaseConnection();
                 PreparedStatement stmt = connection.prepareStatement(
                         "INSERT INTO base_price (type, cost) VALUES (?, ?) ON DUPLICATE KEY UPDATE cost = ?"
                 )
            ) {
                stmt.setString(1, liftPassType);
                stmt.setInt(2, liftPassCost);
                stmt.setInt(3, liftPassCost);
                stmt.execute();
            }

            return "";
        });

        get("/prices", getPrices());

        after((req, res) -> res.type("application/json"));
    }
}
