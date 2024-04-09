package liftpasspricing

import dojo.liftpasspricing.Prices
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import spark.Spark
import java.sql.Connection
import java.sql.SQLException

class PricesTest {

    @Test
    fun doesSomething() {
        val response = RestAssured.
            given()
                .port(4567)
            .`when`()
                .param("type", "1jour")
                .get("/prices")

            .then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                .assertThat()
                    .contentType(ContentType.JSON)
                .extract().jsonPath()

        assertEquals(35, response.getInt("cost"))
    }

    companion object {
        private var connection: Connection? = null

        @JvmStatic
        @BeforeAll
        @Throws(SQLException::class)
        fun createPrices() {
            connection = Prices.createApp()
        }

        @JvmStatic
        @AfterAll
        @Throws(SQLException::class)
        fun stopApplication() {
            Spark.stop()
            connection!!.close()
        }
    }
}
