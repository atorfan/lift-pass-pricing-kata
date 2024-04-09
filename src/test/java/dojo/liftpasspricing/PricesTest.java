package dojo.liftpasspricing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import spark.Spark;

@Disabled
public class PricesTest {

    @BeforeAll
    public static void createPrices() {
        Prices.createApp();
    }

    @AfterAll
    public static void stopApplication() {
        Spark.stop();
    }

    @Test
    public void doesSomething() {
        JsonPath response = RestAssured.
            given().
                port(4567).
            when().
                param("type", "1jour").
                get("/prices").

            then().
                assertThat().
                    statusCode(200).
                assertThat().
                    contentType("application/json").
            extract().jsonPath();

        assertEquals(35, response.getInt("cost"));
    }
}
