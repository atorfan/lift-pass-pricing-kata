package dojo.liftpasspricing.infrastructure

import java.io.File

object EnvironmentVariables {
    private val envVars: Map<String, String>

    private fun Map<String, String>.envOrLookup(key: String): String {
        return System.getenv(key) ?: this[key]!!
    }

    fun lookup(key: String) = envVars.envOrLookup(key)

    init {
        envVars = envFile().fold(
            onSuccess = { mapEnvFile(it) },
            onFailure = { emptyMap() }
        )
    }

    private fun mapEnvFile(envFile: File) =
        if (envFile.exists()) {
            envFile.readLines()
                .map { it.split("=") }
                .filter { it.size == 2 }
                .associate { it.first().trim() to it.last().trim() }
        } else {
            emptyMap()
        }

    private fun envFile() =
        try {
            Result.success(listOf(".env").map { File(it) }.first { it.exists() })
        } catch (ex: NoSuchElementException) {
            Result.failure(ex)
        }
}
