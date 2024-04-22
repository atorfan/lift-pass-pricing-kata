package dojo.liftpasspricing.application

import dojo.liftpasspricing.domain.BasePriceRepository
import dojo.liftpasspricing.domain.LiftPassCalendar
import dojo.liftpasspricing.domain.LiftPassCalendarRepository
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream

class CostCalculatorShould {

    @ParameterizedTest
    @MethodSource("test data")
    fun `return cost`(expectedCost: Int, type: String, ages: List<Int>?, date: String?) {
        val priceDateRequested = if (date != null) LocalDate.parse(date) else null

        val cost = costCalculator.calculateFor(type, ages ?: emptyList(), priceDateRequested)

        cost shouldBe expectedCost
    }

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

    companion object {

        private const val BASE_COST_FOR_1JOUR = 35
        private const val BASE_COST_FOR_NIGHT = 19

        @JvmStatic
        fun `test data`(): Stream<Arguments> =
            Stream.of(
                Arguments.of(35, "1jour", null, null),
                Arguments.of(35, "1jour", null, "2019-03-04"),
                Arguments.of(23, "1jour", null, "2019-03-11"),
                Arguments.of(35, "1jour", null, "2020-03-11"),
                Arguments.of(0, "1jour", listOf(3), null),
                Arguments.of(0, "1jour", listOf(5), null),
                Arguments.of(25, "1jour", listOf(6), null),
                Arguments.of(25, "1jour", listOf(14), null),
                Arguments.of(25, "1jour", listOf(14), "2019-03-11"),
                Arguments.of(35, "1jour", listOf(15), null),
                Arguments.of(35, "1jour", listOf(64), null),
                Arguments.of(70, "1jour", listOf(40, 64), null),
                Arguments.of(35, "1jour", listOf(64), "2019-03-04"),
                Arguments.of(23, "1jour", listOf(64), "2019-03-11"),
                Arguments.of(46, "1jour", listOf(40, 64), "2019-03-11"),
                Arguments.of(27, "1jour", listOf(65), null),
                Arguments.of(27, "1jour", listOf(65), "2019-03-04"),
                Arguments.of(18, "1jour", listOf(65), "2019-03-11"),
                Arguments.of(62, "1jour", listOf(40, 65), "2019-03-04"),

                Arguments.of(0, "night", null, null),
                Arguments.of(0, "night", listOf(5), null),
                Arguments.of(19, "night", listOf(6), null),
                Arguments.of(19, "night", listOf(7), null),
                Arguments.of(19, "night", listOf(37), null),
                Arguments.of(19, "night", listOf(64), null),
                Arguments.of(8, "night", listOf(65), null),
                Arguments.of(38, "night", listOf(6, 6), null),
                Arguments.of(19, "night", listOf(5, 30), null),
                Arguments.of(27, "night", listOf(5, 30, 65), null),
            )
    }
}
