package dojo.liftpasspricing.domain

import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.ceil

class OneJourForfait(private val basePrice: Int, private val holidays: Holidays) : Forfait {

    override fun costFor(age: Int?, priceDateRequested: LocalDate?): Int {
        val bonusRule = loadBonusRules(priceDateRequested)
            .filter { it.match(age) }
            .map(BonusRule::toDiscounts)
            .first()

        return ceil(basePrice * (1 - bonusRule.ageDiscount / 100.0) * (1 - bonusRule.holidayDiscount / 100.0)).toInt()
    }

    private fun loadBonusRules(priceDateRequested: LocalDate?): List<BonusRule> {
        val holidayBonus = holidayBonus(priceDateRequested)
        return listOf(
            BonusRule(noAgeFilter(), holidayBonus, 0),
            BonusRule(ageFilterFrom(0..5), 0, 100),
            BonusRule(ageFilterFrom(6..14), 0, 30),
            BonusRule(ageFilterFrom(15..64), holidayBonus, 0),
            BonusRule(ageFilterFrom(65..99), holidayBonus, 25),
        )
    }

    private fun holidayBonus(priceDateRequested: LocalDate?) =
        if (priceDateRequested != null && isWorkingMonday(priceDateRequested)) 35 else 0

    private fun isWorkingMonday(priceDateRequested: LocalDate) =
        !holidays.isHoliday(holidays, priceDateRequested) && priceDateRequested.dayOfWeek == DayOfWeek.MONDAY
}
