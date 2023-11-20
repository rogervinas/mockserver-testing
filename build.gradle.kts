import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.9.20"
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("io.ktor:ktor-client-core:2.3.6")
  implementation("io.ktor:ktor-client-cio:2.3.6")

  testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
  testImplementation("io.mockk:mockk:1.13.8")
  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation("org.mock-server:mockserver-client-java-no-dependencies:5.15.0")
  testImplementation("org.mock-server:mockserver-junit-jupiter-no-dependencies:5.15.0")
  testImplementation("org.testcontainers:testcontainers:1.19.2")
  testImplementation("org.testcontainers:junit-jupiter:1.19.2")
}

kotlin {
  jvmToolchain {
    this.languageVersion.set(JavaLanguageVersion.of(21))
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
