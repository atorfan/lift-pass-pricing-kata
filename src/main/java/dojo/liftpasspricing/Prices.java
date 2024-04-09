package dojo.liftpasspricing;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Prices {

    private static final DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
            final Date priceDateRequested = (priceDateRequestedString != null) ?
                    ISO_DATE_FORMAT.parse(priceDateRequestedString) :
                    null;

            final int basePrice = retrieveBasePrice(connection, forfaitType);
            final List<Date> holidays = retrieveHolidays(connection);

            int calculatedCost;
            int reduction;

            if (age != null && age < 6) {
                calculatedCost = 0;
            } else {
                reduction = 0;

                if (!forfaitType.equals("night")) {
                    boolean isHoliday = false;

                    for (Date holiday : holidays) {
                        if (priceDateRequested != null) {
                            if (priceDateRequested.getYear() == holiday.getYear() && //
                                    priceDateRequested.getMonth() == holiday.getMonth() && //
                                    priceDateRequested.getDate() == holiday.getDate()) {
                                isHoliday = true;
                            }
                        }
                    }

                    if (priceDateRequested != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(priceDateRequested);
                        if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                            reduction = 35;
                        }
                    }

                    // TODO apply reduction for others
                    if (age != null && age < 15) {
                        calculatedCost =  (int) Math.ceil(basePrice * .7);
                    } else {
                        if (age == null) {
                            double cost = basePrice * (1 - reduction / 100.0);
                            calculatedCost = (int) Math.ceil(cost);
                        } else {
                            if (age > 64) {
                                double cost = basePrice * .75 * (1 - reduction / 100.0);
                                calculatedCost = (int) Math.ceil(cost);
                            } else {
                                double cost = basePrice * (1 - reduction / 100.0);
                                calculatedCost = (int) Math.ceil(cost);
                            }
                        }
                    }
                } else {
                    if (age != null && age >= 6) {
                        if (age > 64) {
                            calculatedCost = (int) Math.ceil(basePrice * .4);
                        } else {
                            calculatedCost = basePrice;
                        }
                    } else {
                        calculatedCost = 0;
                    }
                }
            }
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

    private static List<Date> retrieveHolidays(final Connection connection) throws SQLException {
        List<Date> holidays = new ArrayList<>();
        try (PreparedStatement holidayStmt = connection.prepareStatement("SELECT * FROM holidays")) {
            try (ResultSet holidaysResultSet = holidayStmt.executeQuery()) {

                while (holidaysResultSet.next()) {
                    Date holiday = holidaysResultSet.getDate("holiday");
                    holidays.add(holiday);
                }
            }
        }
        return holidays;
    }
}
