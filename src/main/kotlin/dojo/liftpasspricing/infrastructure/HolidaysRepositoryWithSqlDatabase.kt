package dojo.liftpasspricing.infrastructure

import dojo.liftpasspricing.domain.Holidays
import dojo.liftpasspricing.domain.HolidaysRepository
import java.time.Instant
import java.time.ZoneId

class HolidaysRepositoryWithSqlDatabase : HolidaysRepository {

    override fun retrieve() =
        Holidays(
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
