package dojo.liftpasspricing.domain

import java.time.LocalDate

interface HolidaysRepository {

    fun retrieve(): List<LocalDate>
}
