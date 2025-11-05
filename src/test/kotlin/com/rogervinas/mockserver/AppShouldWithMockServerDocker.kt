package com.rogervinas.mockserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait.forHttp
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File

@Testcontainers
@TestInstance(PER_CLASS)
class AppShouldWithMockServerDocker {

  companion object {
    private const val name = "Ivy"

    private const val fooServiceName = "foo-api"
    private const val fooServicePort = 8080
    private const val barServiceName = "bar-api"
    private const val barServicePort = 8080

    private lateinit var fooApiHost: String
    private var fooApiPort: Int = 0
    private lateinit var barApiHost: String
    private var barApiPort: Int = 0

    val waitForMockServerLiveness = forHttp("/mockserver/status")
      .withMethod("PUT")
      .forStatusCode(200)

    @Container
    @JvmStatic
    val container = ComposeContainer(File("docker-compose.yml"))
      .withExposedService(fooServiceName, fooServicePort, waitForMockServerLiveness)
      .withExposedService(barServiceName, barServicePort, waitForMockServerLiveness)

    @BeforeAll
    @JvmStatic
    fun beforeAll() {
      fooApiHost = container.getServiceHost(fooServiceName, fooServicePort)
      fooApiPort = container.getServicePort(fooServiceName, fooServicePort)
      barApiHost = container.getServiceHost(barServiceName, barServicePort)
      barApiPort = container.getServicePort(barServiceName, barServicePort)
    }
  }

  @Test
  fun `call foo and bar`() {
    val fooApiUrl = "http://${fooApiHost}:${fooApiPort}"
    val barApiUrl = "http://${barApiHost}:${barApiPort}"

    val app = App(name, fooApiUrl, barApiUrl)

    assertThat(app.execute()).isEqualTo(
      """
        Hi! I am $name
        I called Foo and its response is Hello $name I am Foo!
        I called Bar and its response is Hello $name I am Bar!
        Bye!
      """.trimIndent()
    )
  }

  @Test
  fun `call foo an bar with dynamic stubs`() {
    val fooApiUrl = "http://${fooApiHost}:${fooApiPort}/dynamic"
    val barApiUrl = "http://${barApiHost}:${barApiPort}/dynamic"

    MockServerClient(fooApiHost, fooApiPort)
      .`when`(
        request()
          .withMethod("GET")
          .withPath("/dynamic/foo")
          .withQueryStringParameter("name", name)
      )
      .respond(
        response()
          .withStatusCode(200)
          .withBody("Hi $name I am Foo, how are you?")
      )
    MockServerClient(barApiHost, barApiPort)
      .`when`(
        request()
          .withMethod("GET")
          .withPath("/dynamic/bar/$name")
      ).respond(
        response()
          .withStatusCode(200)
          .withBody("Hi $name I am Bar, nice to meet you!")
      )

    val app = App(name, fooApiUrl, barApiUrl)

    assertThat(app.execute()).isEqualTo(
      """
        Hi! I am $name
        I called Foo and its response is Hi $name I am Foo, how are you?
        I called Bar and its response is Hi $name I am Bar, nice to meet you!
        Bye!
      """.trimIndent()
    )
  }
}
