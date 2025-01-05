package com.descope.internal.http

import com.descope.internal.others.urlDecode
import com.descope.internal.others.urlEncode
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.Foundation.pathComponents

internal actual fun parseUri(uriString: String): Uri = UriImpl(uriString = uriString)

actual abstract class Uri {
    actual abstract override fun toString(): String
    actual abstract fun isHierarchical(): Boolean
    internal abstract fun isOpaqueInternal(): Boolean
    actual fun isOpaque(): Boolean = isOpaqueInternal()
    actual abstract fun isRelative(): Boolean
    internal abstract fun isAbsoluteInternal(): Boolean
    actual fun isAbsolute(): Boolean = isAbsoluteInternal()
    actual abstract fun getScheme(): String?
    actual abstract fun getSchemeSpecificPart(): String
    actual abstract fun getEncodedSchemeSpecificPart(): String
    actual abstract fun getAuthority(): String?
    actual abstract fun getEncodedAuthority(): String?
    actual abstract fun getUserInfo(): String?
    actual abstract fun getEncodedUserInfo(): String?
    actual abstract fun getHost(): String?
    actual abstract fun getPort(): Int
    actual abstract fun getPath(): String?
    actual abstract fun getEncodedPath(): String?
    actual abstract fun getQuery(): String?
    actual abstract fun getEncodedQuery(): String?
    actual abstract fun getFragment(): String?
    actual abstract fun getEncodedFragment(): String?
    actual abstract fun getPathSegments(): List<String>
    actual abstract fun getLastPathSegment(): String?
    actual abstract fun buildUpon(): UriBuilder
    internal abstract fun getQueryParameterNamesInternal(): Set<String> 
    actual fun getQueryParameterNames(): Set<String> = getQueryParameterNamesInternal()
    internal abstract fun getQueryParametersInternal(key: String): List<String>
    actual fun getQueryParameters(key: String): List<String> = getQueryParametersInternal(key)
    internal abstract fun getQueryParameterInternal(key: String): String?
    actual fun getQueryParameter(key: String): String? = getQueryParameterInternal(key)
    internal abstract fun getBooleanQueryParameterInternal(key: String, defaultValue: Boolean): Boolean
    actual fun getBooleanQueryParameter(key: String, defaultValue: Boolean): Boolean = 
        getBooleanQueryParameterInternal(key, defaultValue)
    internal abstract fun normalizeSchemeInternal(): Uri
    actual fun normalizeScheme(): Uri = normalizeSchemeInternal()
}

internal class UriImpl(private val nsUrl: NSURL): Uri() {
    constructor(uriString: String) : this(nsUrl = NSURL(string = uriString))
    private val urlComponents = NSURLComponents(uRL = nsUrl, resolvingAgainstBaseURL = false)
    @Suppress("UNCHECKED_CAST")
    private val queryParameters: Map<String, List<String>> = (urlComponents.queryItems as? List<NSURLQueryItem>)
        ?.let { parameters ->
            val map = mutableMapOf<String, MutableList<String>>()
            for (parameter in parameters) {
                map.getOrPut(key = parameter.name) { mutableListOf() } += parameter.value ?: ""
            }
            map.mapValues { (_, value) -> value.toList() }
        } ?: emptyMap()

    override fun isHierarchical(): Boolean = isRelative() || getSchemeSpecificPart().firstOrNull() == '/'
    override fun isOpaqueInternal(): Boolean = isAbsoluteInternal() && getSchemeSpecificPart().firstOrNull() != '/'
    override fun isRelative(): Boolean = getScheme() == null
    override fun isAbsoluteInternal(): Boolean = getScheme() != null

    override fun getScheme(): String? = nsUrl.scheme

    override fun getSchemeSpecificPart(): String = nsUrl.relativeString.urlDecode()

    override fun getEncodedSchemeSpecificPart(): String = nsUrl.relativeString

    override fun getAuthority(): String? = getEncodedAuthority()?.urlDecode()

    override fun getEncodedAuthority(): String? = getSchemeSpecificPart()
        .trimStart('/')
        .split('#', '/', limit = 2)[0]
        .takeIf { it.isNotEmpty() }

    override fun getUserInfo(): String? = getEncodedUserInfo()?.urlDecode()

    override fun getEncodedUserInfo(): String? = getSchemeSpecificPart()
        .takeIf { '@' in it }
        ?.substringBefore(delimiter = '@')

    override fun getHost(): String? = nsUrl.host

    override fun getPort(): Int = nsUrl.port?.intValue ?: -1
    
    // NSURL.path is already decoded, unlike the rest of NSURL's properties
    override fun getPath(): String? = nsUrl.path

    override fun getEncodedPath(): String? = getPath()?.urlEncode()

    override fun getQuery(): String? = getEncodedQuery()?.urlDecode()

    override fun getEncodedQuery(): String? = nsUrl.query

    override fun getFragment(): String? = getEncodedFragment()?.urlDecode()

    override fun getEncodedFragment(): String? = nsUrl.fragment

    @Suppress("UNCHECKED_CAST")
    override fun getPathSegments(): List<String> = (nsUrl.pathComponents as? List<String>) ?: emptyList()

    override fun getLastPathSegment(): String? = getPathSegments().firstOrNull()

    override fun buildUpon(): UriBuilder = UriBuilder(nsUrl = nsUrl)

    override fun getQueryParameterNamesInternal(): Set<String> = queryParameters.keys

    override fun getQueryParametersInternal(key: String): List<String> = queryParameters[key] ?: emptyList()

    override fun getQueryParameterInternal(key: String): String? = queryParameters[key]?.firstOrNull()

    override fun getBooleanQueryParameterInternal(key: String, defaultValue: Boolean): Boolean =
        getQueryParameterInternal(key = key)
            ?.lowercase()
            ?.toBooleanStrictOrNull()
            ?: defaultValue

    override fun normalizeSchemeInternal(): Uri = UriImpl(
        uriString = StringBuilder().apply {
            getScheme()?.lowercase()?.also { scheme ->
                append(scheme)
                append(':')
            }
            append(getSchemeSpecificPart())
        }.toString()
    )

    override fun toString(): String = nsUrl.absoluteString ?: ""

    fun asNSURL(): NSURL = nsUrl
} 

actual class UriBuilder(nsUrl: NSURL) {
    constructor(urlString: String) : this(nsUrl = NSURL(string = urlString))
    
    private var components = NSURLComponents(uRL = nsUrl, resolvingAgainstBaseURL = false)

    actual fun scheme(scheme: String): UriBuilder = apply {
        components.scheme = scheme
    }

    actual fun opaquePart(opaquePart: String): UriBuilder = apply {
        val absoluteUrl = components.URL?.absoluteString ?: ""
        val scheme = absoluteUrl
            .substringBefore(delimiter = ':', missingDelimiterValue = "")
            .takeIf { it.isNotEmpty() }
        val schemeSpecificPart = absoluteUrl.substringAfter(delimiter = ':')
        val fragment = schemeSpecificPart
            .substringAfter(delimiter = '#', missingDelimiterValue = "")
            .takeIf { it.isNotEmpty() }
        val newUrl = StringBuilder().apply {
            if (scheme != null) {
                append(scheme)
                append(':')
            }
            append(opaquePart)
            if (fragment != null) {
                append('#')
                append(fragment)
            }
        }.toString()
        components = NSURLComponents(uRL = NSURL(string = newUrl), resolvingAgainstBaseURL = false)
    }

    actual fun encodedOpaquePart(opaquePart: String): UriBuilder = apply {
        opaquePart(opaquePart = opaquePart.urlDecode())
    }

    actual fun authority(authority: String): UriBuilder = apply {
        val absoluteUrl = components.URL?.absoluteString ?: ""
        val scheme = absoluteUrl
            .substringBefore(delimiter = ':', missingDelimiterValue = "")
            .takeIf { it.isNotEmpty() }
        val schemeSpecificPart = absoluteUrl.substringAfter(delimiter = ':')
        val pathQueryAndFragment = schemeSpecificPart
            .indexOfAny(chars = charArrayOf('/', '#'))
            .takeIf { it != -1 }
            ?.let { index -> schemeSpecificPart.substring(startIndex = index) }
        val newUrl = StringBuilder().apply {
            if (scheme != null) {
                append(scheme)
                append(':')
            }
            append(authority)
            if (pathQueryAndFragment != null) {
                append(pathQueryAndFragment)
            }
        }.toString()
        components = NSURLComponents(uRL = NSURL(string = newUrl), resolvingAgainstBaseURL = false)
    }

    actual fun encodedAuthority(authority: String): UriBuilder = apply {
        authority(authority = authority.urlDecode())
    }

    actual fun path(path: String): UriBuilder = apply {
        components.path = path
    }

    actual fun encodedPath(path: String): UriBuilder = apply {
        components.percentEncodedPath = path
    }

    actual fun appendPath(newSegment: String): UriBuilder = apply {
        components.path = components.path?.let { pathPrefix -> "$pathPrefix/$newSegment" }
    }

    actual fun appendEncodedPath(newSegment: String): UriBuilder = apply {
        components.percentEncodedPath = components.percentEncodedPath?.let { pathPrefix -> "$pathPrefix/$newSegment" }
    }

    actual fun query(query: String): UriBuilder = apply {
        components.query = query
    }

    actual fun encodedQuery(query: String): UriBuilder = apply {
        components.percentEncodedQuery = query
    }

    actual fun fragment(fragment: String): UriBuilder = apply {
        components.fragment = fragment
    }

    actual fun encodedFragment(fragment: String): UriBuilder = apply {
        components.percentEncodedFragment = fragment
    }

    actual fun appendQueryParameter(key: String, value: String): UriBuilder = apply {
        components.query = StringBuilder().apply {
            val currentQuery = components.query
            if (currentQuery != null) {
                append(currentQuery)
                if (currentQuery.isNotEmpty()) {
                    append('&')    
                }
            }
            append(key.urlEncode())
            append('=')
            append(value.urlEncode())
        }.toString()
    }

    actual fun clearQuery(): UriBuilder = apply {
        components.query = ""
    }

    actual fun build(): Uri = UriImpl(nsUrl = components.URL ?: NSURL(string = ""))
}