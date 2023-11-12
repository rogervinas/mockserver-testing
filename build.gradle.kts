import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

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
  implementation("io.ktor:ktor-client-cio:1.6.7")

  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
  testImplementation("io.mockk:mockk:1.12.4")
  testImplementation("org.assertj:assertj-core:3.22.0")
  testImplementation("org.mock-server:mockserver-client-java-no-dependencies:5.15.0")
  testImplementation("org.mock-server:mockserver-junit-jupiter-no-dependencies:5.15.0")
  testImplementation("org.testcontainers:testcontainers:1.19.1")
  testImplementation("org.testcontainers:junit-jupiter:1.19.1")
}

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(21))
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
