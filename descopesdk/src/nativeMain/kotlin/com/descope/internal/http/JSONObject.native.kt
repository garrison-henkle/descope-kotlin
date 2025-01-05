package com.descope.internal.http

import com.descope.internal.others.toJsonArray
import com.descope.internal.others.toJsonMap

internal actual fun createJSONObject(): JSONObject = JSONObject()
internal actual fun createJSONObject(json: String): JSONObject = JSONObject(json)
internal actual fun createJSONArray(): JSONArray = JSONArray()
internal actual fun createJSONArray(json: String): JSONArray = JSONArray(json)

actual class JSONException : Exception()

actual val JSON_NULL: Any = Any() 

actual class JSONObject {
    private val data: MutableMap<String, Any>

    constructor() {
        data = mutableMapOf()
    }

    constructor(json: String) {
        data = json.toJsonMap().toMutableMap()
    }

    actual fun length(): Int = data.size
    actual fun put(name: String?, value: Boolean): JSONObject = apply {
        data[name ?: throw NullPointerException()] = value
    }

    actual fun put(name: String?, value: Double): JSONObject = apply {
        if (!value.isFinite()) throw JSONException()
        data[name ?: throw NullPointerException()] = value
    }

    actual fun put(name: String?, value: Int): JSONObject = apply {
        data[name ?: throw NullPointerException()] = value
    }

    actual fun put(name: String?, value: Long): JSONObject = apply {
        data[name ?: throw NullPointerException()] = value
    }

    actual fun put(name: String?, value: Any?): JSONObject = apply {
        val nonNullName = name ?: throw NullPointerException()
        if (value == null) {
            remove(name = nonNullName)
        } else {
            data[nonNullName] = value
        }
    }

    actual fun putOpt(name: String?, value: Any?): JSONObject = apply {
        if (name != null && value != null) {
            data[name] = value
        }
    }

    actual fun accumulate(name: String?, value: Any): JSONObject = apply {
        val nonNullName = name ?: throw NullPointerException()
        val obj = opt(name = nonNullName)
        val jsonArray = obj as? JSONArray
        when {
            obj == null -> put(name = name, value = JSONArray().apply { put(value = value) })
            jsonArray != null -> jsonArray.put(value = value)
            else -> {
                put(
                    name = name,
                    value = JSONArray().apply {
                        put(value = obj)
                        put(value = value)
                    },
                )
            }
        }
    }

    actual fun append(name: String?, value: Any): JSONObject = apply {
        val nonNullName = name ?: throw NullPointerException()
        val obj = opt(name = nonNullName)
        val jsonArray = obj as? JSONArray
        when {
            obj == null -> put(name = name, value = JSONArray().apply { put(value = value) })
            jsonArray != null -> jsonArray.put(value = value)
            else -> throw JSONException()
        }
    }

    actual fun remove(name: String?): Any? {
        val nonNullName = name ?: throw NullPointerException()
        return data.remove(key = nonNullName)
    }

    actual fun isNull(name: String): Boolean = opt(name = name) == null
    actual fun has(name: String): Boolean = name in data
    actual fun get(name: String): Any = data[name] ?: throw JSONException()
    actual fun opt(name: String): Any? = data[name]
    actual fun getBoolean(name: String): Boolean = (get(name = name) as? Boolean) ?: throw JSONException()
    actual fun optBoolean(name: String, fallback: Boolean): Boolean = (get(name = name) as? Boolean) ?: fallback
    actual fun getDouble(name: String): Double = (get(name = name) as? Double) ?: throw JSONException()
    actual fun optDouble(name: String, fallback: Double): Double = (get(name = name) as? Double) ?: fallback
    actual fun getInt(name: String): Int = (get(name = name) as? Int) ?: throw JSONException()
    actual fun optInt(name: String, fallback: Int): Int = (get(name = name) as? Int) ?: fallback
    actual fun getLong(name: String): Long = (get(name = name) as? Long) ?: throw JSONException()
    actual fun optLong(name: String, fallback: Long): Long = (get(name = name) as? Long) ?: fallback
    actual fun getString(name: String): String = (get(name = name) as? String) ?: throw JSONException()
    actual fun optString(name: String, fallback: String): String = (get(name = name) as? String) ?: fallback
    actual fun getJSONArray(name: String): JSONArray = (get(name = name) as? JSONArray) ?: throw JSONException()
    actual fun optJSONArray(name: String): JSONArray? = get(name = name) as? JSONArray
    actual fun getJSONObject(name: String): JSONObject = (get(name = name) as? JSONObject) ?: throw JSONException()
    actual fun optJSONObject(name: String): JSONObject? = get(name = name) as? JSONObject
    actual fun toJSONArray(names: JSONArray): JSONArray =
        JSONArray(array = Array(size = names.length()) { i -> data[names.getString(index = i)]!! })

    actual fun keys(): Iterator<String> = data.keys.iterator()
    actual fun names(): JSONArray = JSONArray(array = data.keys.toList().toTypedArray())

    internal fun toMap(): Map<String, Any> = data
    
    actual override fun toString(): String = StringBuilder().apply {
        var i = 0
        val lastIndex = data.size - 1
        append('{')
        for ((key, value) in data) {
            append('"')
            append(key)
            append("\":")
            when (value) {
                JSON_NULL -> append("null")
                is Long -> append(value)
                is Int -> append(value)
                is Double -> append(value)
                is Boolean -> append(value)
                is String -> {
                    append('"')
                    append(value)
                    append('"')
                }

                is JSONObject -> append(value.toString())
                is JSONArray -> append(value.toString())
            }
            if (i++ != lastIndex) {
                append(',')
            }
        }
        append('}')
    }.toString()
}

actual class JSONArray {
    private var data: MutableList<Any>

    constructor() {
        data = mutableListOf()
    }

    constructor(array: Array<Any>) {
        data = array.toMutableList()
    }

    constructor(json: String) {
        data = json.toJsonArray().toMutableList()
    }

    actual fun length(): Int = data.size

    actual fun put(value: Boolean): JSONArray = apply {
        data += value
    }

    actual fun put(value: Double): JSONArray = apply {
        data += value
    }

    actual fun put(value: Int): JSONArray = apply {
        data += value
    }

    actual fun put(value: Long): JSONArray = apply {
        data += value
    }

    actual fun put(value: Any): JSONArray = apply {
        data += value
    }

    actual fun put(index: Int, value: Boolean): JSONArray = apply {
        data.add(index = index, element = value)
    }

    actual fun put(index: Int, value: Double): JSONArray = apply {
        if (!value.isFinite()) throw JSONException()
        data.add(index = index, element = value)
    }

    actual fun put(index: Int, value: Int): JSONArray = apply {
        data.add(index = index, element = value)
    }

    actual fun put(index: Int, value: Long): JSONArray = apply {
        data.add(index = index, element = value)
    }

    actual fun put(index: Int, value: Any): JSONArray = apply {
        data.add(index = index, element = value)
    }

    actual fun isNull(index: Int): Boolean = data.getOrNull(index = index) == null
    actual fun get(index: Int): Any = data.getOrNull(index = index) ?: throw JSONException()
    actual fun opt(index: Int): Any? = data.getOrNull(index = index)
    actual fun remove(index: Int): Any? = if (index in data.indices) data.removeAt(index = index) else null
    actual fun getBoolean(index: Int): Boolean = (data.getOrNull(index = index) as? Boolean) ?: throw JSONException()
    actual fun optBoolean(index: Int, fallback: Boolean): Boolean = (data.getOrNull(index = index) as? Boolean) ?: fallback
    actual fun getDouble(index: Int): Double = (data.getOrNull(index = index) as? Double) ?: throw JSONException()
    actual fun optDouble(index: Int, fallback: Double): Double = (data.getOrNull(index = index) as? Double) ?: fallback
    actual fun getInt(index: Int): Int = (data.getOrNull(index = index) as? Int) ?: throw JSONException()
    actual fun optInt(index: Int, fallback: Int): Int = (data.getOrNull(index = index) as? Int) ?: fallback
    actual fun getLong(index: Int): Long = (data.getOrNull(index = index) as? Long) ?: throw JSONException()
    actual fun optLong(index: Int, fallback: Long): Long = (data.getOrNull(index = index) as? Long) ?: fallback
    actual fun getString(index: Int): String = (data.getOrNull(index = index) as? String) ?: throw JSONException()
    actual fun optString(index: Int, fallback: String): String = (data.getOrNull(index = index) as? String) ?: fallback
    actual fun getJSONArray(index: Int): JSONArray = (data.getOrNull(index = index) as? JSONArray) ?: throw JSONException()
    actual fun optJSONArray(index: Int): JSONArray? = data.getOrNull(index = index) as? JSONArray
    actual fun getJSONObject(index: Int): JSONObject  = (data.getOrNull(index = index) as? JSONObject) ?: throw JSONException()
    actual fun optJSONObject(index: Int): JSONObject? = data.getOrNull(index = index) as? JSONObject

    actual override fun toString(): String = StringBuilder().apply {
        val lastIndex = data.size - 1
        append('[')
        for ((i, value) in data.withIndex()) {
            when (value) {
                JSON_NULL -> append("null")
                is Long -> append(value)
                is Int -> append(value)
                is Double -> append(value)
                is Boolean -> append(value)
                is String -> {
                    append('"')
                    append(value)
                    append('"')
                }

                is JSONObject -> append(value.toString())
                is JSONArray -> append(value.toString())
            }
            if (i != lastIndex) {
                append(',')
            }
        }
        append(']')
    }.toString()
}