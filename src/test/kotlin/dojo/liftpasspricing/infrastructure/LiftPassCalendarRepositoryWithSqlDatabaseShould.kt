package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.domain.LiftPassCalendar
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
class LiftPassCalendarRepositoryWithSqlDatabaseShould {

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

        val saved = LiftPassCalendarRepositoryWithSqlDatabase().retrieve()

        saved shouldBe LiftPassCalendar(expected)
    }

    private fun upsert(expected: List<LocalDate>) =
        upsertDatabase("INSERT INTO holidays (holiday, description) VALUES (?, 'extra holiday') ON CONFLICT DO NOTHING")
        { pStmt ->
            expected.forEach { date ->
                pStmt.setDate(1, Date.valueOf(date))
                pStmt.executeUpdate()
            }
        }

    companion object {

        @Container
        @SuppressWarnings("unused")
        val environment = ComposeContainer(File("./docker/infra-docker-compose.yml"))
            .waitingFor("postgres-database-1", Wait.forHealthcheck())
            .withLocalCompose(true)
    }
}
