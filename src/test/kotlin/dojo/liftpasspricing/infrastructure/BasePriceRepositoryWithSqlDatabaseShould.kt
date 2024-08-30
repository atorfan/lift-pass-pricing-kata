package dojo.liftpasspricing.infrastructure

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File

@Testcontainers
class BasePriceRepositoryWithSqlDatabaseShould {

    @Test
    fun `return base price from database`() {
        val expected = mapOf(
            "1jour" to 35,
            "night" to 19,
            "extra" to 10
        )
        upsert(expected)

        val saved = basePriceRepositoryWithSqlDatabase.retrieveFor("extra")

        saved shouldBe 10
    }

    @Test
    fun `save base price in database`() {
        basePriceRepositoryWithSqlDatabase.storeFor("extra", 13)

        val saved = selectBasePriceFor("extra")

        saved shouldBe 13
    }

    private fun selectBasePriceFor(type: String) = queryFromDatabase(
        "SELECT cost FROM base_price WHERE type = ?",
        { pStmt -> pStmt.setString(1, type) },
        { resultSet -> resultSet.next(); resultSet.getInt(1) }
    )

    private fun upsert(expected: Map<String, Int>) =
        upsertDatabase("INSERT INTO base_price (type, cost) VALUES (?, ?) ON CONFLICT DO NOTHING")
        { pStmt ->
            expected.forEach { costToType ->
                pStmt.setString(1, costToType.key)
                pStmt.setInt(2, costToType.value)
                pStmt.executeUpdate()
            }
        }

    @BeforeEach
    fun setUp() {
        basePriceRepositoryWithSqlDatabase = BasePriceRepositoryWithSqlDatabase()
    }

    private lateinit var basePriceRepositoryWithSqlDatabase: BasePriceRepositoryWithSqlDatabase

    companion object {

        @Container
        @SuppressWarnings("unused")
        val environment = ComposeContainer(File("./docker/infra-docker-compose.yml"))
            .waitingFor("postgres-database-1", Wait.forHealthcheck())
            .withLocalCompose(true)
    }
}
