package dojo.liftpasspricing.domain

class NightlyForfait(override val basePrice: Int) : Forfait {

    override fun loadBonusRules() =
        listOf(
            BonusRule(noAgeFilter(), 0, 100),
            BonusRule(ageFilterFrom(0..5), 0, 100),
            BonusRule(ageFilterFrom(6..64), 0, 0),
            BonusRule(ageFilterFrom(65..99), 0, 60),
        )
}
