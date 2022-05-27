package com.rogervinas.mockserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.client.MockServerClient
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

@ExtendWith(MockServerExtension::class)
class AppShouldWithOneMockServer {

  private val name = "Ada"

  @Test
  fun `call foo and bar`(mockServerClient: MockServerClient) {
    mockServerClient
      .`when`(request().withMethod("GET").withPath("/foo").withQueryStringParameter("name", name))
      .respond(response().withStatusCode(200).withBody("Hello ${name} I am Foo!"))
    mockServerClient
      .`when`(request().withMethod("GET").withPath("/bar/${name}"))
      .respond(response().withStatusCode(200).withBody("Hello $name I am Bar!"))

    val mockServerUrl = "http://localhost:${mockServerClient.port}"
    val app = App(name, mockServerUrl, mockServerUrl)

    assertThat(app.execute()).isEqualTo(
      """
        Hi! I am $name
        I called Foo and its response is Hello $name I am Foo!
        I called Bar and its response is Hello $name I am Bar!
        Bye!
      """.trimIndent()
    )
  }
}
