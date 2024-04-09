package dojo.liftpasspricing;

import dojo.liftpasspricing.domain.CostCalculator;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

            final int basePrice = retrieveBasePrice(connection, forfaitType);
            final List<LocalDate> holidays = retrieveHolidays(connection);
            CostCalculator costCalculator = new CostCalculator(basePrice, holidays);
            int calculatedCost = costCalculator.calculateFor(forfaitType, age, priceDateRequested);

            return "{ \"cost\": " + calculatedCost + "}";
        });

        after((req, res) -> {
            res.type("application/json");
        });

        return connection;
    }

    private static int retrieveBasePrice(final Connection connection, final String forfaitType) throws SQLException {
        int cost;
        try (PreparedStatement costStmt = connection.prepareStatement("SELECT cost FROM base_price WHERE type = ?")) {
            costStmt.setString(1, forfaitType);
            try (ResultSet result = costStmt.executeQuery()) {
                result.next();
                cost = result.getInt("cost");
            }
        }
        return cost;
    }

    private static List<LocalDate> retrieveHolidays(final Connection connection) throws SQLException {
        List<LocalDate> holidays = new ArrayList<>();
        try (PreparedStatement holidayStmt = connection.prepareStatement("SELECT * FROM holidays")) {
            try (ResultSet holidaysResultSet = holidayStmt.executeQuery()) {

                while (holidaysResultSet.next()) {
                    Date holiday = holidaysResultSet.getDate("holiday");
                    holidays.add(
                            Instant.ofEpochMilli(holiday.getTime())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                    );
                }
            }
        }
        return holidays;
    }
}
