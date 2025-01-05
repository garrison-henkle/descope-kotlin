package com.descope.internal.others

import com.descope.internal.http.JSONObject

// JSON

internal actual fun JSONObject.toMap(): Map<String, Any> = toMap()

internal expect fun String.toJsonMap(): Map<String, Any>

internal expect fun String.toJsonArray(): List<Any>