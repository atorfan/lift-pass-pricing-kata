package dojo.liftpasspricing.application

import dojo.liftpasspricing.domain.*
import java.time.LocalDate

class CostCalculator(
    private val basePriceRepository: BasePriceRepository,
    private val liftPassCalendarRepository: LiftPassCalendarRepository
) {
    fun calculateFor(
        forfaitType: String,
        ages: List<Int>,
        priceDateRequested: LocalDate?
    ): Int {
        val basePrice = basePriceRepository.retrieveFor(forfaitType)
        val calendar = liftPassCalendarRepository.retrieve()

        val forfait = getForfait(forfaitType, basePrice, calendar, priceDateRequested)
        return forfait.costFor(ages)
    }
}
