package com.descope.internal.http

import com.descope.internal.others.currentTimeSeconds

actual fun createHttpCookie(name: String, value: String): HttpCookie = HttpCookie(name = name, value = value)

actual class HttpCookie private constructor(
    private val name: String,
    private var value: String,
    private var comment: String?,
    private var commentUrl: String?,
    private var discard: Boolean,
    private var portlist: String?,
    private var domain: String?,
    private var maxAge: Long?,
    private var path: String?,
    private var secure: Boolean,
    private var version: Int?,
    private var httpOnly: Boolean,
) {
    constructor(name: String, value: String) : this(
        name = name,
        value = value,
        comment = null,
        commentUrl = null,
        discard = false,
        portlist = null,
        domain = null,
        maxAge = null,
        path = null,
        secure = false,
        version = null,
        httpOnly = false,
    )

    private val createdAtEpochSeconds = currentTimeSeconds()

    actual fun getComment(): String? = comment
    actual fun setComment(purpose: String?) { comment = purpose }
    actual fun getCommentURL(): String? = commentUrl
    actual fun setCommentURL(purpose: String?) { commentUrl = purpose }
    actual fun getDiscard(): Boolean = discard
    actual fun setDiscard(discard: Boolean) { this.discard = discard}
    actual fun getPortlist(): String? = portlist
    actual fun setPortlist(ports: String?) { portlist = ports}
    actual fun getDomain(): String? = domain
    actual fun setDomain(pattern: String?) { domain = pattern }
    actual fun getMaxAge(): Long = maxAge ?: Long.MAX_VALUE
    actual fun setMaxAge(expiry: Long) { maxAge = expiry }
    actual fun getPath(): String? = path
    actual fun setPath(uri: String?) { path = uri }
    actual fun getSecure(): Boolean = secure
    actual fun setSecure(flag: Boolean) { secure = flag }
    actual fun getName(): String = name
    actual fun getValue(): String = value
    actual fun setValue(newValue: String) { value = newValue }
    actual fun getVersion(): Int = version ?: 1
    actual fun setVersion(v: Int) { version = v }
    actual fun isHttpOnly(): Boolean = httpOnly
    actual fun setHttpOnly(httpOnly: Boolean) { this.httpOnly = httpOnly }

    actual fun hasExpired(): Boolean = if (maxAge != null) {
        currentTimeSeconds() > getMaxAge() + createdAtEpochSeconds
    } else {
        false
    }

    actual override fun toString(): String = StringBuilder().apply {
        append(name)
        append('=')
        append(value)
        comment?.also {
            append("; Comment=")
            append(it)
        }
        commentUrl?.also {
            append("; CommentURL=\"")
            append(it)
            append('"')
        }
        if (discard) {
            append("; Discard")
        }
        portlist?.also {
            append("; Port=\"")
            append(it)
            append('"')
        }
        domain?.also {
            append("; Domain=")
            append(it)
        }
        maxAge?.also {
            append("; Max-Age=")
            append(it)
        }
        path?.also {
            append("; Path=")
            append(it)
        }
        if (secure) {
            append("; Secure")
        }
        version?.also {
            append("; Version=")
            append(it)
        }
        if(httpOnly) {
            append("; HttpOnly")
        }
    }.toString()

    actual fun clone(): Any = HttpCookie(
        name = name,
        value = value,
        comment = comment,
        commentUrl = commentUrl,
        discard = discard,
        portlist = portlist,
        domain = domain,
        maxAge = maxAge,
        path = path,
        secure = secure,
        version = version,
        httpOnly = httpOnly,
    )

    override fun equals(other: Any?): Boolean = this.hashCode() == other?.hashCode()
    actual override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (commentUrl?.hashCode() ?: 0)
        result = 31 * result + discard.hashCode()
        result = 31 * result + (portlist?.hashCode() ?: 0)
        result = 31 * result + (domain?.hashCode() ?: 0)
        result = 31 * result + maxAge.hashCode()
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + secure.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + httpOnly.hashCode()
        return result
    }
}