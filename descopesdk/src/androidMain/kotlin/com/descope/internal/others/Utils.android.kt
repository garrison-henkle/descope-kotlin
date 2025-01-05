package com.descope.internal.others

import android.os.Looper
import android.text.format.DateFormat
import android.util.Base64
import com.descope.internal.http.JSONArray
import com.descope.internal.http.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest

// Url Encoding

internal actual fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8.name())

internal actual fun String.urlDecode(): String = URLDecoder.decode(this, Charsets.UTF_8.name())

// Base64

internal actual fun String.decodeBase64(): ByteArray {
    return Base64.decode(this, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}

internal actual fun ByteArray.toBase64(): String {
    return Base64.encodeToString(this, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}

// JSON

internal actual fun JSONObject.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    keys().forEach { key ->
        map[key] = when(val obj = get(key)) {
            is JSONObject -> obj.toMap()
            is JSONArray -> obj.toList()
            else -> obj
        }
    }
    return map.toMap()
}

// SHA256

internal actual fun sha256(bytes: ByteArray): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(bytes)
}

// General

internal actual fun currentTimeMillis(): Long = System.currentTimeMillis()

internal actual fun createDateStringFromEpochMs(format: String, epochMs: Long): String =
    DateFormat.format(format, epochMs).toString()

internal actual fun isMainThread(): Boolean = Looper.getMainLooper().thread == Thread.currentThread()