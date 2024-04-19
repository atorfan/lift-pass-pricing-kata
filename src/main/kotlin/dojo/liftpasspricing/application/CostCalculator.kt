package dojo.liftpasspricing.application

import dojo.liftpasspricing.domain.*
import java.time.LocalDate

class CostCalculator(
    private val basePriceRepository: BasePriceRepository,
    private val liftPassCalendarRepository: LiftPassCalendarRepository
) {
    fun calculateFor(
        forfaitType: String,
        age: Int?,
        priceDateRequested: LocalDate?
    ): Int {
        val basePrice = basePriceRepository.retrieveFor(forfaitType)
        val calendar = liftPassCalendarRepository.retrieve()

        return getForfait(calendar, forfaitType, basePrice)
            .costFor(age, priceDateRequested)
    }
}
