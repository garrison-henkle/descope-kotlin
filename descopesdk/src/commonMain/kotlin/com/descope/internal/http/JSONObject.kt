package com.descope.internal.http

import kotlin.jvm.JvmStatic

internal expect fun createJSONObject(): JSONObject
internal expect fun createJSONObject(json: String): JSONObject
internal expect fun createJSONArray(): JSONArray
internal expect fun createJSONArray(json: String): JSONArray

expect class JSONException : Exception

expect val JSON_NULL: Any 

expect class JSONObject {
    fun length(): Int
    fun put(name: String?, value: Boolean): JSONObject
    fun put(name: String?, value: Double): JSONObject
    fun put(name: String?, value: Int): JSONObject
    fun put(name: String?, value: Long): JSONObject
    fun put(name: String?, value: Any?): JSONObject
    fun putOpt(name: String?, value: Any?): JSONObject
    fun accumulate(name: String?, value: Any): JSONObject
    fun append(name: String?, value: Any): JSONObject
    fun remove(name: String?): Any?
    fun isNull(name: String): Boolean
    fun has(name: String): Boolean
    fun get(name: String): Any
    fun opt(name: String): Any?
    fun getBoolean(name: String): Boolean
    fun optBoolean(name: String, fallback: Boolean): Boolean
    fun getDouble(name: String): Double
    fun optDouble(name: String, fallback: Double): Double
    fun getInt(name: String): Int
    fun optInt(name: String, fallback: Int): Int
    fun getLong(name: String): Long
    fun optLong(name: String, fallback: Long): Long
    fun getString(name: String): String
    fun optString(name: String, fallback: String): String
    fun getJSONArray(name: String): JSONArray
    fun optJSONArray(name: String): JSONArray?
    fun getJSONObject(name: String): JSONObject
    fun optJSONObject(name: String): JSONObject?
    fun toJSONArray(names: JSONArray): JSONArray
    fun keys(): Iterator<String>
    fun names(): JSONArray
    override fun toString(): String
}

expect class JSONArray {
    fun length(): Int
    fun put(value: Boolean): JSONArray
    fun put(value: Double): JSONArray
    fun put(value: Int): JSONArray
    fun put(value: Long): JSONArray
    fun put(value: Any): JSONArray
    fun put(index: Int, value: Boolean): JSONArray
    fun put(index: Int, value: Double): JSONArray
    fun put(index: Int, value: Int): JSONArray
    fun put(index: Int, value: Long): JSONArray
    fun put(index: Int, value: Any): JSONArray
    fun isNull(index: Int): Boolean
    fun get(index: Int): Any
    fun opt(index: Int): Any?
    fun remove(index: Int): Any?
    fun getBoolean(index: Int): Boolean
    fun optBoolean(index: Int, fallback: Boolean): Boolean
    fun getDouble(index: Int): Double
    fun optDouble(index: Int, fallback: Double): Double
    fun getInt(index: Int): Int
    fun optInt(index: Int, fallback: Int): Int
    fun getLong(index: Int): Long
    fun optLong(index: Int, fallback: Long): Long
    fun getString(index: Int): String
    fun optString(index: Int, fallback: String): String
    fun getJSONArray(index: Int): JSONArray
    fun optJSONArray(index: Int): JSONArray?
    fun getJSONObject(index: Int): JSONObject
    fun optJSONObject(index: Int): JSONObject?
    override fun toString(): String
    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
}
