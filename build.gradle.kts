plugins {
    kotlin("jvm") version "1.9.+"
    id("org.jetbrains.kotlinx.kover") version "0.8.+"
}

repositories {
    mavenCentral()
}

dependencies {
    // Spark web framework
    implementation("com.sparkjava:spark-core:2.9.+")
    val jettyVersion = "9.4.48.+"
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-webapp:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-http:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-util:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-xml:$jettyVersion")

    // Logger
    implementation("org.slf4j:slf4j-simple:2.0.+")

    // MySQL connector
    runtimeOnly("com.mysql:mysql-connector-j:8.4.+")

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
    testImplementation("io.rest-assured:rest-assured:5.4.+")
    // dependencies needed by Rest-Assured
    testImplementation("org.slf4j:jcl-over-slf4j:2.0.+")
    val jacksonVersion = "2.17.+"
    testImplementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Testcontainers
    val testContainersVersion = "1.19.+"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.apache.commons:commons-compress:1.26.+")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin { // Extension for easy setup
    jvmToolchain(17) // Target version of generated JVM bytecode. See 7️⃣
}
