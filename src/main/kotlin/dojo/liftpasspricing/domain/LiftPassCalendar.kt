package dojo.liftpasspricing.domain

import java.time.DayOfWeek
import java.time.LocalDate

data class LiftPassCalendar(private val holidays: List<LocalDate>) {

    fun isWorkingMonday(priceDateRequested: LocalDate?) =
        priceDateRequested != null
                && priceDateRequested.dayOfWeek == DayOfWeek.MONDAY
                && !isHoliday(priceDateRequested)

    private fun isHoliday(priceDateRequested: LocalDate) =
        holidays.any { match(priceDateRequested, it) }

    private fun match(priceDateRequested: LocalDate, it: LocalDate) =
        priceDateRequested.year == it.year
                && priceDateRequested.month == it.month
                && priceDateRequested.dayOfMonth == it.dayOfMonth
}
