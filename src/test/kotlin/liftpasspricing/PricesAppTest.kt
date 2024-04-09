package liftpasspricing

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dojo.liftpasspricing.PricesApp
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import spark.Spark
import java.io.File
import java.sql.SQLException

@Testcontainers
class PricesAppTest {

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "35;{'type': '1jour'}",
            "35;{'type': '1jour', 'date': '2019-03-04'}",
            "23;{'type': '1jour', 'date': '2019-03-11'}",
            "35;{'type': '1jour', 'date': '2020-03-11'}",
            " 0;{'type': '1jour', 'age': 3}",
            " 0;{'type': '1jour', 'age': 5}",
            "25;{'type': '1jour', 'age': 6}",
            "25;{'type': '1jour', 'age': 14}",
            "35;{'type': '1jour', 'age': 15}",
            "35;{'type': '1jour', 'age': 64}",
            "35;{'type': '1jour', 'age': 64, 'date': '2019-03-04'}",
            "23;{'type': '1jour', 'age': 64, 'date': '2019-03-11'}",
            "27;{'type': '1jour', 'age': 65}",
            "27;{'type': '1jour', 'age': 65, 'date': '2019-03-04'}",
            "18;{'type': '1jour', 'age': 65, 'date': '2019-03-11'}",
            " 0;{'type': 'night'}",
            " 0;{'type': 'night', 'age': 5}",
            "19;{'type': 'night', 'age': 6}",
            "19;{'type': 'night', 'age': 7}",
            "19;{'type': 'night', 'age': 37}",
            "19;{'type': 'night', 'age': 64}",
            " 8;{'type': 'night', 'age': 65}",
        ]
    )
    fun `should return cost`(expectedCost: Int, jsonRequestParams: String) {
        val response = RestAssured.
            given()
                .port(4567)
            .`when`()
                .params(mapFrom(jsonRequestParams))
                .get("/prices")

            .then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                .assertThat()
                    .contentType(ContentType.JSON)
                .extract().jsonPath()

        assertEquals(expectedCost, response.getInt("cost"))
    }

    @Test
    fun `should put cost`() {
        val response = RestAssured.
            given()
                .port(4567)
            .`when`()
                .params("cost", "35")
                .params("type", "1jour")
                .put("/prices")

            .then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                .assertThat()
                    .contentType(ContentType.JSON)
                .extract().response()
                .asString()

        assertTrue(response.isBlank())
    }

    private fun mapFrom(params: String): Map<String, Any?> {
        val objectMapper = jacksonObjectMapper()
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        return objectMapper.readValue<Map<String, Any>>(params)
    }

    companion object {
        @Container
        @SuppressWarnings("unused")
        val environment = ComposeContainer(File("./docker/docker-compose.yml"))
            .waitingFor("db-1", Wait.forHealthcheck())
            .withLocalCompose(true)

        @JvmStatic
        @BeforeAll
        @Throws(SQLException::class)
        fun createPrices() {
            PricesApp.create()
        }

        @JvmStatic
        @AfterAll
        @Throws(SQLException::class)
        fun stopApplication() {
            Spark.stop()
        }
    }
}
