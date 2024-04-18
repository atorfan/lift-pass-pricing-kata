package dojo.liftpasspricing.application

import dojo.liftpasspricing.domain.*
import java.time.LocalDate

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

        return getForfait(forfaitType, basePrice, holidays)
            .costFor(age, priceDateRequested)
    }
}
