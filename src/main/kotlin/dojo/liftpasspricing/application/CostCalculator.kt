package dojo.liftpasspricing.application

import dojo.liftpasspricing.domain.BasePriceRepository
import dojo.liftpasspricing.domain.Holidays
import dojo.liftpasspricing.domain.HolidaysRepository
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.ceil

class CostCalculator(
    private val basePriceRepository: BasePriceRepository,
    private val holidaysRepository: HolidaysRepository
) {
    fun calculateFor(
        forfaitType: String,
        age: Int?,
        priceDateRequested: LocalDate?
    ): Int {
        val basePrice = basePriceRepository.retrieveFor(forfaitType)
        val holidays = holidaysRepository.retrieve()

        if (age != null && age < 6) {
            return 0
        }

        val calculatedCost: Int

        if (forfaitType != "night") {

            // TODO apply reduction for others
            if (age != null && age < 15) {
                calculatedCost = ceil(basePrice * .7).toInt()
            } else {
                val reduction = if (shouldApplyReduction(holidays, priceDateRequested)) 35 else 0

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
            calculatedCost =
                if (age != null && age >= 6) {
                    if (age > 64) {
                        ceil(basePrice * .4).toInt()
                    } else {
                        basePrice
                    }
                } else {
                    0
                }
        }
        return calculatedCost
    }

    private fun shouldApplyReduction(holidays: Holidays, priceDateRequested: LocalDate?) =
        priceDateRequested != null
                && !holidays.isHoliday(holidays, priceDateRequested)
                && priceDateRequested.dayOfWeek == DayOfWeek.MONDAY
}
