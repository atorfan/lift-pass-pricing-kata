package dojo.liftpasspricing.domain

import java.time.LocalDate

class OneJourForfait(private val calendar: LiftPassCalendar, override val basePrice: Int) : Forfait {

    override fun loadBonusRules(priceDateRequested: LocalDate?): List<BonusRule> {
        val holidayBonus = if (calendar.isWorkingMonday(priceDateRequested)) 35 else 0
        return listOf(
            BonusRule(noAgeFilter(), holidayBonus, 0),
            BonusRule(ageFilterFrom(0..5), 0, 100),
            BonusRule(ageFilterFrom(6..14), 0, 30),
            BonusRule(ageFilterFrom(15..64), holidayBonus, 0),
            BonusRule(ageFilterFrom(65..99), holidayBonus, 25),
        )
    }
}
