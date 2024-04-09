package dojo.liftpasspricing.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class CostCalculator {

    private final int basePrice;
    private final List<LocalDate> holidays;

    public CostCalculator(int basePrice, List<LocalDate> holidays) {
        this.basePrice = basePrice;
        this.holidays = holidays;
    }

    public int calculateFor(
            String forfaitType,
            Integer age,
            LocalDate priceDateRequested
    ) {
        int calculatedCost;
        int reduction;

        if (age != null && age < 6) {
            calculatedCost = 0;
        } else {
            reduction = 0;

            if (!forfaitType.equals("night")) {
                boolean isHoliday = false;

                for (LocalDate holiday : holidays) {
                    if (priceDateRequested != null) {
                        if (priceDateRequested.getYear() == holiday.getYear() && //
                                priceDateRequested.getMonth() == holiday.getMonth() && //
                                priceDateRequested.getDayOfMonth() == holiday.getDayOfMonth()) {
                            isHoliday = true;
                        }
                    }
                }

                if (priceDateRequested != null) {
                    if (!isHoliday && priceDateRequested.getDayOfWeek() == DayOfWeek.MONDAY) {
                        reduction = 35;
                    }
                }

                // TODO apply reduction for others
                if (age != null && age < 15) {
                    calculatedCost = (int) Math.ceil(basePrice * .7);
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
        return calculatedCost;
    }
}
