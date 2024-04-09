package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.domain.HolidaysRepository
import java.sql.Connection
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HolidaysRepositoryWithSqlDatabase(private val connection: Connection) : HolidaysRepository {

    override fun retrieve(): List<LocalDate> {
        return buildList {
            connection
                .prepareStatement("SELECT * FROM holidays")
                .use { pStmt ->
                    pStmt.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val sqlDate = resultSet.getDate("holiday")
                            val localDate = Instant.ofEpochMilli(sqlDate.time)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            add(localDate)
                        }
                    }
                }
        }
    }
}