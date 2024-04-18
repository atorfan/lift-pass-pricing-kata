package dojo.liftpasspricing.domain

data class BonusRule(val ageFilter: AgeFilter, val holidayBonus: Int, val ageBonus: Int) {

    fun toDiscounts() = Discounts(ageBonus, holidayBonus)

    fun match(age: Int?) = ageFilter.match(age)
}

data class AgeFilter(private val ageRange: IntRange?) {

    fun match(age: Int?): Boolean {
        if (ageRange == null && age == null) {
            return true
        }
        if (ageRange != null) {
            return ageRange.contains(age)
        }
        return false
    }
}

data class Discounts(val ageDiscount: Int, val holidayDiscount: Int)

fun ageFilterFrom(ageRange: IntRange) = AgeFilter(ageRange)

fun noAgeFilter() = AgeFilter(null)
