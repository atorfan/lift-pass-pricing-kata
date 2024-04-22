package dojo.liftpasspricing.domain

import java.time.LocalDate

class OneJourForfait(
    override val basePrice: Int,
    private val calendar: LiftPassCalendar,
    private val priceDateRequested: LocalDate?,
) : Forfait {

    override fun loadBonusRules(): List<BonusRule> {
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
