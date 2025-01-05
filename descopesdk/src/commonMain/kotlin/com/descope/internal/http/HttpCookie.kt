package com.descope.internal.http

expect fun createHttpCookie(name: String, value: String): HttpCookie

@Suppress("EqualsOrHashCode")
expect class HttpCookie {
    fun hasExpired(): Boolean
    fun getComment(): String?
    fun setComment(purpose: String?)
    fun getCommentURL(): String?
    fun setCommentURL(purpose: String?)
    fun getDiscard(): Boolean
    fun setDiscard(discard: Boolean)
    fun getPortlist(): String?
    fun setPortlist(ports: String?)
    fun getDomain(): String?
    fun setDomain(pattern: String?)
    fun getMaxAge(): Long
    fun setMaxAge(expiry: Long)
    fun getPath(): String?
    fun setPath(uri: String?)
    fun getSecure(): Boolean
    fun setSecure(flag: Boolean)
    fun getName(): String
    fun getValue(): String
    fun setValue(newValue: String)
    fun getVersion(): Int
    fun setVersion(v: Int)
    fun isHttpOnly(): Boolean
    fun setHttpOnly(httpOnly: Boolean)
    override fun toString(): String
    override fun hashCode(): Int
    fun clone(): Any
}