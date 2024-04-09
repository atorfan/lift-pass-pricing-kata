plugins {
    kotlin("jvm") version "1.9.+"
    id("org.jetbrains.kotlinx.kover") version "0.7.+"
}

repositories {
    mavenCentral()
}

dependencies {
    // Spark web framework
    implementation("com.sparkjava:spark-core:2.7.+")

    // Logger
    implementation("org.slf4j:slf4j-simple:1.7.+")

    // MySQL connector
    runtimeOnly("mysql:mysql-connector-java:8.0.+")

    // Junit 5
    val junitVersion = "5.+"
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")

    // Rest-Assured
    testImplementation("io.rest-assured:rest-assured:3.3.+")
    // dependencies needed by Rest-Assured
    testImplementation("org.slf4j:jcl-over-slf4j:1.7.+")
    val jacksonVersion = "2.11.+"
    testImplementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Testcontainers
    val testContainersVersion = "1.19.+"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin { // Extension for easy setup
    jvmToolchain(17) // Target version of generated JVM bytecode. See 7️⃣
}
