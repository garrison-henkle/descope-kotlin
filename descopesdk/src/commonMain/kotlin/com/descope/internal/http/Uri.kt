package com.descope.internal.http

internal expect fun parseUri(uriString: String): Uri

expect abstract class Uri {
    abstract fun isHierarchical(): Boolean
    fun isOpaque(): Boolean
    abstract fun isRelative(): Boolean
    fun isAbsolute(): Boolean
    abstract fun getScheme(): String?
    abstract fun getSchemeSpecificPart(): String
    abstract fun getEncodedSchemeSpecificPart(): String
    abstract fun getAuthority(): String?
    abstract fun getEncodedAuthority(): String?
    abstract fun getUserInfo(): String?
    abstract fun getEncodedUserInfo(): String?
    abstract fun getHost(): String?
    abstract fun getPort(): Int
    abstract fun getPath(): String?
    abstract fun getEncodedPath(): String?
    abstract fun getQuery(): String?
    abstract fun getEncodedQuery(): String?
    abstract fun getFragment(): String?
    abstract fun getEncodedFragment(): String?
    abstract fun getPathSegments(): List<String>
    abstract fun getLastPathSegment(): String?
    abstract fun buildUpon(): UriBuilder
    fun getQueryParameterNames(): Set<String>
    fun getQueryParameters(key: String): List<String>
    fun getQueryParameter(key: String): String?
    fun getBooleanQueryParameter(key: String, defaultValue: Boolean): Boolean
    fun normalizeScheme(): Uri
    abstract override fun toString(): String
}

expect class UriBuilder {
    fun scheme(scheme: String): UriBuilder
    fun opaquePart(opaquePart: String): UriBuilder
    fun encodedOpaquePart(opaquePart: String): UriBuilder
    fun authority(authority: String): UriBuilder
    fun encodedAuthority(authority: String): UriBuilder
    fun path(path: String): UriBuilder
    fun encodedPath(path: String): UriBuilder
    fun appendPath(newSegment: String): UriBuilder
    fun appendEncodedPath(newSegment: String): UriBuilder
    fun query(query: String): UriBuilder
    fun encodedQuery(query: String): UriBuilder
    fun fragment(fragment: String): UriBuilder
    fun encodedFragment(fragment: String): UriBuilder
    fun appendQueryParameter(key: String, value: String): UriBuilder
    fun clearQuery(): UriBuilder
    fun build(): Uri
    override fun toString(): String
}