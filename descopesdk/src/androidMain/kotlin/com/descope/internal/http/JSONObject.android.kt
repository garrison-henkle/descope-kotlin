package com.descope.internal.http

import kotlin.jvm.Throws

@Throws(JSONException::class)
internal actual fun createJSONObject(): JSONObject = JSONObject()
@Throws(JSONException::class)
internal actual fun createJSONObject(json: String): JSONObject = JSONObject(json)
@Throws(JSONException::class)
internal actual fun createJSONArray(): JSONArray = JSONArray()
@Throws(JSONException::class)
internal actual fun createJSONArray(json: String): JSONArray = JSONArray(json)
actual typealias JSONException = org.json.JSONException
actual typealias JSONObject = org.json.JSONObject
actual typealias JSONArray = org.json.JSONArray
actual val JSON_NULL: Any = org.json.JSONObject.NULL