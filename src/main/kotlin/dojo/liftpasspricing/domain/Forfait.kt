package dojo.liftpasspricing.domain

import java.time.LocalDate
import kotlin.math.ceil

private val NO_AGES_SPECIFIED = arrayOfNulls<Int>(1).toList()

interface Forfait {

    val basePrice: Int

    fun costFor(ages: List<Int>): Int {
        return ages
            .ifEmpty { NO_AGES_SPECIFIED }
            .fold(0) { sum, age -> sum + costFor(age) }
    }

    private fun costFor(age: Int?): Int {
        return loadBonusRules()
            .filter { it.match(age) }
            .map(BonusRule::toDiscounts)
            .first()
            .let(::calculatePrice)
    }

    private fun calculatePrice(bonusRule: Discounts) =
        ceil(basePrice * (1 - bonusRule.ageDiscount / 100.0) * (1 - bonusRule.holidayDiscount / 100.0)).toInt()

    fun loadBonusRules(): List<BonusRule>
}

fun getForfait(
    forfaitType: String,
    basePrice: Int,
    calendar: LiftPassCalendar,
    priceDateRequested: LocalDate?
) =
    if (forfaitType == "night") {
        NightlyForfait(basePrice)
    } else {
        OneJourForfait(basePrice, calendar, priceDateRequested)
    }
