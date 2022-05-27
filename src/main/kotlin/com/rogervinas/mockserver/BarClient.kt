package com.rogervinas.mockserver

interface BarClient {

  fun call(name: String): String
}
