package dojo.liftpasspricing

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.sql.SQLException

@Testcontainers
class PricesAppTest {

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "35;{'type': '1jour', 'ages': [64], 'date': '2019-03-04'}",
            "23;{'type': '1jour', 'ages': [64], 'date': '2019-03-11'}",
            "70;{'type': '1jour', 'ages': [40, 64]}",
            " 0;{'type': 'night'}",
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

        val cost = response.getInt("cost")

        cost shouldBe expectedCost
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

        response.shouldBeEmpty()
    }

    private fun mapFrom(params: String): Map<String, Any> {
        val objectMapper = jacksonObjectMapper()
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        return objectMapper.readValue<Map<String, Any>>(params)
    }

    companion object {
        @Container
        @SuppressWarnings("unused")
        val environment = ComposeContainer(File("./docker/infra-docker-compose.yml"))
            .waitingFor("postgres-database-1", Wait.forHealthcheck())
            .withLocalCompose(true)

        @JvmStatic
        @BeforeAll
        @Throws(SQLException::class)
        fun createPrices() {
            PricesApp.start()
        }

        @JvmStatic
        @AfterAll
        @Throws(SQLException::class)
        fun stopApplication() {
            PricesApp.shutdown()
        }
    }
}
