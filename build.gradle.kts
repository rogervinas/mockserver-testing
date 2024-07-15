import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED

plugins {
  id("org.jetbrains.kotlin.jvm") version "2.0.0"
  application
}

repositories {
  mavenCentral()
}

val mockServerVersion = "5.15.0"
val ktorClientVersion = "2.3.12"

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("io.ktor:ktor-client-core:$ktorClientVersion")
  implementation("io.ktor:ktor-client-cio:$ktorClientVersion")

  testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
  testImplementation("io.mockk:mockk:1.13.12")
  testImplementation("org.assertj:assertj-core:3.26.3")
  testImplementation("org.mock-server:mockserver-client-java-no-dependencies:$mockServerVersion")
  testImplementation("org.mock-server:mockserver-junit-jupiter-no-dependencies:$mockServerVersion")
  testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.8"))
  testImplementation("org.testcontainers:testcontainers")
  testImplementation("org.testcontainers:junit-jupiter")
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  testLogging {
    events(PASSED, SKIPPED, FAILED)
    exceptionFormat = FULL
    showExceptions = true
    showCauses = true
    showStackTraces = true
  }
}

application {
  mainClass.set("com.rogervinas.mockserver.AppKt")
}
