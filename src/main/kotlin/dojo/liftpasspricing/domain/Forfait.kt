package dojo.liftpasspricing.domain

import java.time.LocalDate
import kotlin.math.ceil

interface Forfait {

    val basePrice: Int

    fun costFor(age: Int?, priceDateRequested: LocalDate?): Int {
        val bonusRule = loadBonusRules(priceDateRequested)
            .filter { it.match(age) }
            .map(BonusRule::toDiscounts)
            .first()

        return ceil(basePrice * (1 - bonusRule.ageDiscount / 100.0) * (1 - bonusRule.holidayDiscount / 100.0)).toInt()
    }

    fun loadBonusRules(priceDateRequested: LocalDate?): List<BonusRule>
}

fun getForfait(calendar: LiftPassCalendar, forfaitType: String, basePrice: Int) =
    if (forfaitType == "night") {
        NightlyForfait(basePrice)
    } else {
        OneJourForfait(calendar, basePrice)
    }
