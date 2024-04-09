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
import java.util.Calendar;
import java.util.Date;

public class Prices {

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
            final String priceDateRequested = req.queryParams("date");

            final int basePrice = retrieveBasePrice(connection, forfaitType);

            int calculatedCost;

            int reduction;
            boolean isHoliday = false;

            if (age != null && age < 6) {
                calculatedCost = 0;
            } else {
                reduction = 0;

                if (!forfaitType.equals("night")) {
                    DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try (PreparedStatement holidayStmt = connection.prepareStatement("SELECT * FROM holidays")) {
                        try (ResultSet holidays = holidayStmt.executeQuery()) {

                            while (holidays.next()) {
                                Date holiday = holidays.getDate("holiday");
                                if (priceDateRequested != null) {
                                    Date d = isoFormat.parse(priceDateRequested);
                                    if (d.getYear() == holiday.getYear() && //
                                            d.getMonth() == holiday.getMonth() && //
                                            d.getDate() == holiday.getDate()) {
                                        isHoliday = true;
                                    }
                                }
                            }

                        }
                    }

                    if (priceDateRequested != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(isoFormat.parse(priceDateRequested));
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

}
