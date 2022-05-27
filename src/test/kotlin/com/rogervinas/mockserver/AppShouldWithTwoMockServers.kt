package com.rogervinas.mockserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

@TestInstance(PER_CLASS)
class AppShouldWithTwoMockServers {

  private val name = "Leo"

  private val mockServerClientFoo = ClientAndServer()
  private val mockServerClientBar = ClientAndServer()

  @Test
  fun `call foo and bar`() {
    mockServerClientFoo
      .`when`(request().withMethod("GET").withPath("/foo").withQueryStringParameter("name", name))
      .respond(response().withStatusCode(200).withBody("Hello ${name} I am Foo!"))
    mockServerClientBar
      .`when`(request().withMethod("GET").withPath("/bar/${name}"))
      .respond(response().withStatusCode(200).withBody("Hello $name I am Bar!"))

    val mockServerFooUrl = "http://localhost:${mockServerClientFoo.port}"
    val mockServerBarUrl = "http://localhost:${mockServerClientBar.port}"
    val app = App(name, mockServerFooUrl, mockServerBarUrl)

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
