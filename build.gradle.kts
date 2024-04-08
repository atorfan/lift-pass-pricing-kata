plugins {
    kotlin("jvm") version "1.9.+"
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
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.+")

    // Rest-Assured
    testImplementation("io.rest-assured:rest-assured:3.3.+")
    // dependencies needed by Rest-Assured
    testImplementation("org.slf4j:jcl-over-slf4j:1.7.+")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.9.10.+")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin { // Extension for easy setup
    jvmToolchain(17) // Target version of generated JVM bytecode. See 7️⃣
}
