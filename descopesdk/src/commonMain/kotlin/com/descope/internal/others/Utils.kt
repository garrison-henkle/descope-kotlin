package com.descope.internal.others

import com.descope.internal.http.JSONArray
import com.descope.internal.http.JSONException
import com.descope.internal.http.JSONObject
import com.descope.internal.http.createJSONArray
import com.descope.internal.http.createJSONObject

// Url Encoding

internal expect fun String.urlEncode(): String

internal expect fun String.urlDecode(): String

// Base64

internal expect fun String.decodeBase64(): ByteArray

internal expect fun ByteArray.toBase64(): String

// JSON

internal expect fun JSONObject.toMap(): Map<String, Any>

internal fun JSONObject.stringOrEmptyAsNull(key: String): String? = try {
    getString(key).ifEmpty { null }
} catch (ignored: JSONException) {
    null
}

internal fun JSONArray.toList(): List<Any> {
    val list = mutableListOf<Any>()
    for (i in 0 until length()) {
        list.add(when(val obj = get(i)) {
            is JSONObject -> obj.toMap()
            is JSONArray -> obj.toList()
            else -> obj
        })
    }
    return list
}

internal fun JSONObject.optionalMap(key: String): Map<String, Any> = try {
    val obj = getJSONObject(key)
    obj.toMap()
} catch (ignored: JSONException) {
    emptyMap()
}

internal fun JSONArray.toStringList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until length()) {
        list.add(getString(i))
    }
    return list
}

internal fun List<*>.toJsonArray(): JSONArray = createJSONArray().apply {
    this@toJsonArray.forEach {
        when {
            it is Map<*, *> -> put(it.toJsonObject())
            it is List<*> -> put(it.toJsonArray())
            it != null -> put(it)
        }
    }
}

internal fun Map<*, *>.toJsonObject(): JSONObject = createJSONObject().apply {
    forEach {
        val key = it.key as String
        val value = it.value
        when {
            value is Map<*, *> -> put(key, value.toJsonObject())
            value is List<*> -> put(key, value.toJsonArray())
            value != null -> put(key, value)
        }
    }
}

// SHA256

internal expect fun sha256(bytes: ByteArray): ByteArray

// General

internal fun Long.secToMs() = this * 1000L

inline fun <reified T> tryOrNull(block: () -> T): T? = try {
    block()
} catch (e: Exception) {
    null
}

internal expect fun currentTimeMillis(): Long

internal fun currentTimeSeconds(): Long = currentTimeMillis() / 1_000

internal expect fun createDateStringFromEpochMs(format: String, epochMs: Long): String
internal fun createDateStringFromEpochSec(format: String, epochSec: Long): String =
    createDateStringFromEpochMs(format = format, epochMs = epochSec * 1_000L)

internal expect fun isMainThread(): Boolean