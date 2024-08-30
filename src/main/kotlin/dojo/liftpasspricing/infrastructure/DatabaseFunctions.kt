package dojo.liftpasspricing.infrastructure

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

// TODO use a connection pool solution

val databaseHost = EnvironmentVariables.lookup("DATABASE_HOST")
val databasePort = EnvironmentVariables.lookup("DATABASE_PORT")
val databaseUser = EnvironmentVariables.lookup("DATABASE_USER")
val databasePass = EnvironmentVariables.lookup("DATABASE_PASS")

fun obtainDatabaseConnection(): Connection =
    DriverManager.getConnection("jdbc:postgresql://$databaseHost:$databasePort/lift_pass", databaseUser, databasePass)
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

inline fun <R> queryFromDatabase(
    query: String,
    noinline prepare: ((PreparedStatement) -> Unit)? = null,
    mapper: (ResultSet) -> R,
): R {
    var result: R
    obtainDatabaseConnection().use { conn ->
        conn.prepareStatement(query).use { pstmt ->
            prepare?.let { it(pstmt) }
            pstmt.executeQuery().use { resultSet ->
                result = mapper(resultSet)
            }
        }
    }
    return result
}

inline fun upsertDatabase(
    query: String,
    prepare: (PreparedStatement) -> Unit
) {
    obtainDatabaseConnection().use { conn ->
        conn.prepareStatement(query).use { pstmt ->
            prepare(pstmt)
        }
    }
}
