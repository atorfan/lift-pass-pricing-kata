package dojo.liftpasspricing.domain

import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.ceil

class CostCalculator(private val basePrice: Int, private val holidays: List<LocalDate>) {

    fun calculateFor(
        forfaitType: String,
        age: Int?,
        priceDateRequested: LocalDate?
    ): Int {
        val calculatedCost: Int
        var reduction: Int

        if (age != null && age < 6) {
            calculatedCost = 0
        } else {
            reduction = 0

            if (forfaitType != "night") {
                var isHoliday = false

                for (holiday in holidays) {
                    if (priceDateRequested != null) {
                        if (priceDateRequested.year == holiday.year
                            && priceDateRequested.month == holiday.month
                            && priceDateRequested.dayOfMonth == holiday.dayOfMonth
                        ) {
                            isHoliday = true
                        }
                    }
                }

                if (priceDateRequested != null) {
                    if (!isHoliday && priceDateRequested.dayOfWeek == DayOfWeek.MONDAY) {
                        reduction = 35
                    }
                }

                // TODO apply reduction for others
                if (age != null && age < 15) {
                    calculatedCost = ceil(basePrice * .7).toInt()
                } else {
                    if (age == null) {
                        val cost = basePrice * (1 - reduction / 100.0)
                        calculatedCost = ceil(cost).toInt()
                    } else {
                        if (age > 64) {
                            val cost = basePrice * .75 * (1 - reduction / 100.0)
                            calculatedCost = ceil(cost).toInt()
                        } else {
                            val cost = basePrice * (1 - reduction / 100.0)
                            calculatedCost = ceil(cost).toInt()
                        }
                    }
                }
            } else {
                calculatedCost = if (age != null && age >= 6) {
                    if (age > 64) {
                        ceil(basePrice * .4).toInt()
                    } else {
                        basePrice
                    }
                } else {
                    0
                }
            }
        }
        return calculatedCost
    }
}
