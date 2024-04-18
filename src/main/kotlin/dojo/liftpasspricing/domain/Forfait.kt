package dojo.liftpasspricing.domain

import java.time.LocalDate

interface Forfait {

    fun costFor(age: Int?, priceDateRequested: LocalDate?): Int
}

fun getForfait(forfaitType: String, basePrice: Int, holidays: Holidays) =
    if (forfaitType == "night") {
        NightlyForfait(basePrice)
    } else {
        OneJourForfait(basePrice, holidays)
    }
