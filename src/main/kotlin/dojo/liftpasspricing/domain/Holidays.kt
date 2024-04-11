package dojo.liftpasspricing.domain

import java.time.LocalDate

data class Holidays(val value: List<LocalDate>) {

    fun isHoliday(
        holidays: Holidays,
        priceDateRequested: LocalDate?
    ): Boolean {
        var isHoliday = false

        for (holiday in holidays.value) {
            if (priceDateRequested != null) {
                if (priceDateRequested.year == holiday.year
                    && priceDateRequested.month == holiday.month
                    && priceDateRequested.dayOfMonth == holiday.dayOfMonth
                ) {
                    isHoliday = true
                }
            }
        }
        return isHoliday
    }
}
