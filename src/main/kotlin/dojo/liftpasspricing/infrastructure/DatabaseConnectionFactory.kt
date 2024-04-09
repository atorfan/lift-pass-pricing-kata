package dojo.liftpasspricing.infrastructure

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

// TODO use a connection pool solution
// TODO user & password from environment variables

fun obtainDatabaseConnection(): Connection =
    DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql")
        .also {
            closeOnShutdown(it)
        }

private fun closeOnShutdown(connection: Connection) {
    Runtime.getRuntime().addShutdownHook(Thread {
        try {
            connection.close()
        } catch (e: SQLException) {
            LoggerFactory.getLogger("DatabaseConnectionFactory").error("Connection close", e)
        }
    })
}
