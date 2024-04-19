package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.domain.LiftPassCalendar
import dojo.liftpasspricing.domain.LiftPassCalendarRepository
import java.time.Instant
import java.time.ZoneId

class LiftPassCalendarRepositoryWithSqlDatabase : LiftPassCalendarRepository {

    override fun retrieve() =
        LiftPassCalendar(
            buildList {
                queryFromDatabase("SELECT * FROM holidays")
                { resultSet ->
                    while (resultSet.next()) {
                        val sqlDate = resultSet.getDate("holiday")
                        val localDate = Instant.ofEpochMilli(sqlDate.time)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        add(localDate)
                    }
                }
            }
        )
}
