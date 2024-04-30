package dojo.liftpasspricing.application

import dojo.liftpasspricing.domain.BasePriceRepository
import dojo.liftpasspricing.domain.LiftPassCalendar
import dojo.liftpasspricing.domain.LiftPassCalendarRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

private const val BASE_COST_FOR_1JOUR = 35
private const val BASE_COST_FOR_NIGHT = 19

class CostCalculatorShould {

    @Nested
    inner class OneJourTests {

        @Test
        fun `return base cost for 1jour lift pass`() {
            val cost = calculateFor(type = "1jour")
            cost shouldBe BASE_COST_FOR_1JOUR
        }

        @Test
        fun `apply discount for working monday`() {
            val cost = calculateFor(type = "1jour", priceDateRequested = "2019-03-11")
            cost shouldBe 23
        }

        @ParameterizedTest
        @ValueSource(strings = ["2019-03-05", "2020-03-11"])
        fun `not apply discount for non working mondays`(date: String) {
            val cost = calculateFor(type = "1jour", priceDateRequested = date)
            cost shouldBe BASE_COST_FOR_1JOUR
        }

        @ParameterizedTest
        @ValueSource(strings = ["2019-02-18", "2019-02-25", "2019-03-04"])
        fun `not apply discount for holidays`(date: String) {
            val cost = calculateFor(type = "1jour", priceDateRequested = date)
            cost shouldBe BASE_COST_FOR_1JOUR
        }

        @ParameterizedTest
        @ValueSource(ints = [3, 5])
        fun `return 0 cost for children under 6`(age: Int) {
            val cost = calculateFor(type = "1jour", ages = intArrayOf(age))
            cost shouldBe 0
        }

        @ParameterizedTest
        @ValueSource(ints = [6, 14])
        fun `apply discount for children between 6 and 14 years old`(age: Int) {
            val cost = calculateFor(type = "1jour", ages = intArrayOf(age))
            cost shouldBe 25
        }

        @ParameterizedTest
        @ValueSource(ints = [15, 30, 40, 50, 60, 64])
        fun `not apply discount for people between 15 and 64 years old`(age: Int) {
            val cost = calculateFor(type = "1jour", ages = intArrayOf(age))
            cost shouldBe BASE_COST_FOR_1JOUR
        }

        @ParameterizedTest
        @ValueSource(ints = [65, 75])
        fun `apply discount for people over 64 years old`(age: Int) {
            val cost = calculateFor(type = "1jour", ages = intArrayOf(age))
            cost shouldBe 27
        }

        @Test
        fun `apply multiple discount for working monday and age over 64 years old`() {
            val cost = calculateFor(type = "1jour", priceDateRequested = "2019-03-11", ages = intArrayOf(65))
            cost shouldBe 18
        }

        @ParameterizedTest
        @CsvSource(
            delimiter = ';', value = [
                "25;5,6",
                "60;6,30",
                "62;5,30,65"
            ]
        )
        fun `return cost for more than one person`(
            expectedCost: Int,
            ages: String
        ) {
            val cost = calculateFor(type = "1jour", ages = ages.toIntArraySplitBy(','))
            cost shouldBe expectedCost
        }
    }

    @Nested
    inner class OneNightTests {

        @Test
        fun `return base cost for 1 night lift pass`() {
            val cost = calculateFor(type = "night")
            cost shouldBe 0
        }

        @ParameterizedTest
        @ValueSource(ints = [3, 5])
        fun `return 0 cost for children under 6`(age: Int) {
            val cost = calculateFor(type = "night", ages = intArrayOf(age))
            cost shouldBe 0
        }

        @ParameterizedTest
        @ValueSource(ints = [6, 30, 40, 50, 60, 64])
        fun `not apply discount for people between 6 and 64`(age: Int) {
            val cost = calculateFor(type = "night", ages = intArrayOf(age))
            cost shouldBe BASE_COST_FOR_NIGHT
        }

        @ParameterizedTest
        @ValueSource(ints = [65, 75])
        fun `apply discount for people above 65`(age: Int) {
            val cost = calculateFor(type = "night", ages = intArrayOf(age))
            cost shouldBe 8
        }

        @ParameterizedTest
        @CsvSource(
            delimiter = ';', value = [
                "38;6,6",
                "19;5,30",
                "27;5,30,65"
            ]
        )
        fun `return cost for more than one person`(
            expectedCost: Int,
            ages: String
        ) {
            val cost = calculateFor(type = "night", ages = ages.toIntArraySplitBy(','))
            cost shouldBe expectedCost
        }
    }

    private fun calculateFor(
        type: String,
        priceDateRequested: String? = null,
        vararg ages: Int,
    ) =
        costCalculator.calculateFor(
            type,
            ages.toList(),
            if (priceDateRequested != null) LocalDate.parse(priceDateRequested) else null
        )

    @BeforeEach
    fun setup() {
        val basePriceRepository = mockBasePriceRepository()
        val holidaysRepository = mockHolidaysRepository()
        costCalculator = CostCalculator(basePriceRepository, holidaysRepository)
    }

    private fun mockHolidaysRepository() = mockk<LiftPassCalendarRepository>().also {
        every { it.retrieve() } returns
                LiftPassCalendar(
                    listOf(
                        LocalDate.of(2019, 2, 18),
                        LocalDate.of(2019, 2, 25),
                        LocalDate.of(2019, 3, 4),
                    )
                )
    }

    private fun mockBasePriceRepository() = mockk<BasePriceRepository>().also {
        every { it.retrieveFor("1jour") } returns BASE_COST_FOR_1JOUR
        every { it.retrieveFor("night") } returns BASE_COST_FOR_NIGHT
    }

    private lateinit var costCalculator: CostCalculator
}

fun String.toIntArraySplitBy(delimiter: Char) =
    this.split(delimiter).toTypedArray().map { it.toInt() }.toIntArray()
