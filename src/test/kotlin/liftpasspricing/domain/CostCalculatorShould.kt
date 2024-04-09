package liftpasspricing.domain

import dojo.liftpasspricing.domain.CostCalculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream

class CostCalculatorShould {

    @ParameterizedTest
    @MethodSource("test data")
    fun `return cost`(basePrice: Int, expectedCost: Int, type: String, age: Int?, date: String?) {
        val priceDateRequested = if (date != null) LocalDate.parse(date) else null

        val costCalculator = CostCalculator(basePrice, holidays)
        val cost = costCalculator.calculateFor(type, age, priceDateRequested)

        assertEquals(expectedCost, cost)
    }

    @BeforeEach
    fun setup() {
        holidays =
            listOf(
                LocalDate.of(2019, 2, 18),
                LocalDate.of(2019, 2, 25),
                LocalDate.of(2019, 3, 4),
            )
    }

    private lateinit var holidays: List<LocalDate>

    companion object {
        private const val BASE_COST_FOR_1JOUR = 35
        private const val BASE_COST_FOR_NIGHT = 19

        @JvmStatic
        fun `test data`(): Stream<Arguments> =
            Stream.of(
                Arguments.of(BASE_COST_FOR_1JOUR, 35, "1jour", null, null),
                Arguments.of(BASE_COST_FOR_1JOUR, 35, "1jour", null, "2019-03-04"),
                Arguments.of(BASE_COST_FOR_1JOUR, 23, "1jour", null, "2019-03-11"),
                Arguments.of(BASE_COST_FOR_1JOUR, 35, "1jour", null, "2020-03-11"),
                Arguments.of(BASE_COST_FOR_1JOUR, 0, "1jour", 3, null),
                Arguments.of(BASE_COST_FOR_1JOUR, 0, "1jour", 5, null),
                Arguments.of(BASE_COST_FOR_1JOUR, 25, "1jour", 6, null),
                Arguments.of(BASE_COST_FOR_1JOUR, 25, "1jour", 14, null),
                Arguments.of(BASE_COST_FOR_1JOUR, 35, "1jour", 15, null),
                Arguments.of(BASE_COST_FOR_1JOUR, 35, "1jour", 64, null),
                Arguments.of(BASE_COST_FOR_1JOUR, 35, "1jour", 64, "2019-03-04"),
                Arguments.of(BASE_COST_FOR_1JOUR, 23, "1jour", 64, "2019-03-11"),
                Arguments.of(BASE_COST_FOR_1JOUR, 27, "1jour", 65, null),
                Arguments.of(BASE_COST_FOR_1JOUR, 27, "1jour", 65, "2019-03-04"),
                Arguments.of(BASE_COST_FOR_1JOUR, 18, "1jour", 65, "2019-03-11"),

                Arguments.of(BASE_COST_FOR_NIGHT, 0, "night", null, null),
                Arguments.of(BASE_COST_FOR_NIGHT, 0, "night", 5, null),
                Arguments.of(BASE_COST_FOR_NIGHT, 19, "night", 6, null),
                Arguments.of(BASE_COST_FOR_NIGHT, 19, "night", 7, null),
                Arguments.of(BASE_COST_FOR_NIGHT, 19, "night", 37, null),
                Arguments.of(BASE_COST_FOR_NIGHT, 19, "night", 64, null),
                Arguments.of(BASE_COST_FOR_NIGHT, 8, "night", 65, null),
            )
    }
}
