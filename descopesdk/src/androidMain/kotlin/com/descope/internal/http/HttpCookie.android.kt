package com.descope.internal.http

actual fun createHttpCookie(name: String, value: String): HttpCookie = java.net.HttpCookie(name, value)

actual typealias HttpCookie = java.net.HttpCookie