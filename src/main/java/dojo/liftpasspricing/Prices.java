package dojo.liftpasspricing;

import dojo.liftpasspricing.application.CostCalculator;
import dojo.liftpasspricing.domain.BasePriceRepository;
import dojo.liftpasspricing.domain.HolidaysRepository;
import dojo.liftpasspricing.infrastructure.BasePriceRepositoryWithSqlDatabase;
import dojo.liftpasspricing.infrastructure.HolidaysRepositoryWithSqlDatabase;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Prices {

    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    public static Connection createApp() throws SQLException {

        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");

        port(4567);

        put("/prices", (req, res) -> {
            int liftPassCost = Integer.parseInt(req.queryParams("cost"));
            String liftPassType = req.queryParams("type");

            try (PreparedStatement stmt = connection.prepareStatement( //
                    "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
                            "ON DUPLICATE KEY UPDATE cost = ?")) {
                stmt.setString(1, liftPassType);
                stmt.setInt(2, liftPassCost);
                stmt.setInt(3, liftPassCost);
                stmt.execute();
            }

            return "";
        });

        get("/prices", (req, res) -> {
            final Integer age = req.queryParams("age") != null ? Integer.valueOf(req.queryParams("age")) : null;
            final String forfaitType = req.queryParams("type");
            final String priceDateRequestedString = req.queryParams("date");
            final LocalDate priceDateRequested = (priceDateRequestedString != null) ?
                    LocalDate.parse(priceDateRequestedString, ISO_DATE_FORMATTER) :
                    null;

            final BasePriceRepository priceRepository = new BasePriceRepositoryWithSqlDatabase(connection);
            final HolidaysRepository holidaysRepository = new HolidaysRepositoryWithSqlDatabase(connection);
            final CostCalculator costCalculator = new CostCalculator(priceRepository, holidaysRepository);

            int calculatedCost = costCalculator.calculateFor(forfaitType, age, priceDateRequested);

            return "{ \"cost\": " + calculatedCost + "}";
        });

        after((req, res) -> {
            res.type("application/json");
        });

        return connection;
    }
}
