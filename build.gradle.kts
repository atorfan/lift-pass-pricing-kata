plugins {
    application
    kotlin("jvm") version "2.0.+"
    id("org.jetbrains.kotlinx.kover") version "0.8.+"
}

repositories {
    mavenCentral()
}

dependencies {
    // Javalin - simple web framework
    implementation("io.javalin:javalin:6.2.+")

    // Logger
    implementation("org.slf4j:slf4j-simple:2.0.+")

    // MySQL connector
    runtimeOnly("com.mysql:mysql-connector-j:9.0.+")

    // Junit 5
    val junitVersion = "5.+"
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")

    // Kotest
    val kotestVersion = "5.9.+"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")

    // mockk
    testImplementation("io.mockk:mockk:1.13.+")

    // Rest-Assured
    testImplementation("io.rest-assured:rest-assured:5.5.+")
    // dependencies needed by Rest-Assured
    testImplementation("org.slf4j:jcl-over-slf4j:2.0.+")
    val jacksonVersion = "2.17.+"
    testImplementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Testcontainers
    val testContainersVersion = "1.20.+"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.apache.commons:commons-compress:1.27.+")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    // Define la clase principal aquí
    mainClass.set("dojo.liftpasspricing.MainKt")
}

kotlin { // Extension for easy setup
    jvmToolchain(17) // Target version of generated JVM bytecode. See 7️⃣
}
