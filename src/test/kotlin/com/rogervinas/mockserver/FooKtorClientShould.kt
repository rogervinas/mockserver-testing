package com.rogervinas.mockserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.HttpTemplate.TemplateType.MUSTACHE
import org.mockserver.model.HttpTemplate.template

@TestInstance(PER_CLASS)
class FooKtorClientShould {

  private val name = "Joe"

  private lateinit var mockServerClient: MockServerClient
  private lateinit var mockServerUrl: String

  @BeforeAll
  fun beforeAll() {
    mockServerClient = ClientAndServer()
    mockServerUrl = "http://localhost:${mockServerClient.port}"
  }

  @BeforeEach
  fun beforeEach() {
    mockServerClient.reset()
  }

  @Test
  fun `call foo api`() {
    mockServerClient
      .`when`(request().withMethod("GET").withPath("/foo").withQueryStringParameter("name", ".+"))
      .respond(template(
        MUSTACHE,
        """
        {
          statusCode: 200,
          body: 'Hello {{ request.queryStringParameters.name.0 }} I am Foo!'
        }
        """.trimIndent()
      ))

    assertThat(FooKtorClient(mockServerUrl).call(name)).isEqualTo("Hello $name I am Foo!")
  }

  @Test
  fun `handle foo api server error`() {
    mockServerClient
      .`when`(request().withMethod("GET").withPath("/foo").withQueryStringParameter("name", ".+"))
      .respond(response().withStatusCode(503))

    assertThat(FooKtorClient(mockServerUrl).call(name)).startsWith("Foo api error: Server error")
  }
}
