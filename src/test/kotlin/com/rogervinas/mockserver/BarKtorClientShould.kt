package com.rogervinas.mockserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.client.MockServerClient
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

@ExtendWith(MockServerExtension::class)
@TestInstance(PER_CLASS)
class BarKtorClientShould {

  private val name = "Sue"

  private lateinit var mockServerClient: MockServerClient
  private lateinit var mockServerUrl: String

  @BeforeAll
  fun beforeAll(mockServerClient: MockServerClient) {
    this.mockServerClient = mockServerClient
    this.mockServerUrl = "http://localhost:${mockServerClient.port}"
  }

  @BeforeEach
  fun beforeEach() {
    mockServerClient.reset()
  }

  @Test
  fun `call bar api`() {
    mockServerClient
      .`when`(request().withMethod("GET").withPath("/bar/${name}"))
      .respond(response().withStatusCode(200).withBody("Hello $name I am Bar!"))

    assertThat(BarKtorClient(mockServerUrl).call(name)).isEqualTo("Hello $name I am Bar!")
  }

  @Test
  fun `handle bar api server error`() {
    mockServerClient
      .`when`(request().withMethod("GET").withPath("/bar/.+"))
      .respond(response().withStatusCode(503))

    assertThat(BarKtorClient(mockServerUrl).call(name)).startsWith("Bar api error: Server error")
  }
}
