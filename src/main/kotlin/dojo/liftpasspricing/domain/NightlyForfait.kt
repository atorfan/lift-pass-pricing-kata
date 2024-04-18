package dojo.liftpasspricing.domain

import java.time.LocalDate
import kotlin.math.ceil

class NightlyForfait(private val basePrice: Int) : Forfait {

    override fun costFor(age: Int?, priceDateRequested: LocalDate?): Int {
        val ageBonus =
            if (age == null || age < 6) {
                .0
            } else if (age > 64) {
                .4
            } else {
                1.0
            }
        return ceil(basePrice * ageBonus).toInt()
    }
}
