package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.domain.Holidays
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.sql.Date
import java.time.LocalDate

@Testcontainers
class HolidaysRepositoryWithSqlDatabaseShould {

    @Test
    fun `return holidays from database`() {
        val expected = listOf(
            LocalDate.of(2019, 2, 18),
            LocalDate.of(2019, 2, 25),
            LocalDate.of(2019, 3, 4),
            LocalDate.of(2019, 4, 3),
            LocalDate.of(2019, 12, 25),
        )
        upsert(expected)

        val saved = HolidaysRepositoryWithSqlDatabase().retrieve()

        saved shouldBe Holidays(expected)
    }

    private fun upsert(expected: List<LocalDate>) =
        upsertDatabase("INSERT IGNORE INTO lift_pass.holidays (holiday, description) VALUES (?, 'extra holiday')")
        { pStmt ->
            expected.forEach { date ->
                pStmt.setDate(1, Date.valueOf(date))
                pStmt.executeUpdate()
            }
        }

    companion object {

        @Container
        @SuppressWarnings("unused")
        val environment = ComposeContainer(File("./docker/docker-compose.yml"))
            .waitingFor("db-1", Wait.forHealthcheck())
            .withLocalCompose(true)
    }
}
