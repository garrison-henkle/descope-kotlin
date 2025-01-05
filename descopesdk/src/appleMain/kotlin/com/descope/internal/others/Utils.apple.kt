@file:OptIn(ExperimentalForeignApi::class)

package com.descope.internal.others

import com.descope.internal.http.JSONArray
import com.descope.internal.http.JSONObject
import com.descope.internal.http.JSON_NULL
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_LONG
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSError
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSKeyedArchiver
import platform.Foundation.NSLocale
import platform.Foundation.NSMutableData
import platform.Foundation.NSNull
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.URLHostAllowedCharacterSet
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.currentLocale
import platform.Foundation.dataUsingEncoding
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.Foundation.stringByRemovingPercentEncoding
import platform.Foundation.timeIntervalSince1970
import platform.posix.memcpy
import kotlin.math.roundToLong

// Url Encoding

internal actual fun String.urlEncode(): String = asNSString()
    .stringByAddingPercentEncodingWithAllowedCharacters(allowedCharacters = NSCharacterSet.URLHostAllowedCharacterSet)
    ?: this

internal actual fun String.urlDecode(): String = asNSString().stringByRemovingPercentEncoding() ?: this

// Base64

// https://stackoverflow.com/a/22432808 hack that uses NSURL's built-in base64 decoder. K/N does not expose
// the NSData.initWithBase64EncodedString function to create a base64-encoded NSData the normal way.
internal actual fun String.decodeBase64(): ByteArray {
    val nsUrl = NSURL(string = "data:application/octet-stream;base64,$this")
    return NSData.dataWithContentsOfURL(url = nsUrl)?.toByteArray() ?: byteArrayOf()
}

internal actual fun ByteArray.toBase64(): String = toNSData().base64EncodedStringWithOptions(options = 0uL)

// JSON

@Suppress("UNCHECKED_CAST")
internal actual fun String.toJsonMap(): Map<String, Any> = asNSString()
    .dataUsingEncoding(encoding = NSUTF8StringEncoding)
    ?.let { nsData ->
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            NSJSONSerialization.JSONObjectWithData(
                data = nsData,
                options = 0uL,
                error = error.ptr,
            ) as? Map<String, Any>
        }?.convertNSNulls()
    } ?: mutableMapOf()

@Suppress("UNCHECKED_CAST")
internal actual fun String.toJsonArray(): List<Any> = asNSString()
    .dataUsingEncoding(encoding = NSUTF8StringEncoding)
    ?.let { nsData ->
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            NSJSONSerialization.JSONObjectWithData(
                data = nsData,
                options = 0uL,
                error = error.ptr,
            ) as? List<Any>
        }?.convertNSNulls()
    } ?: mutableListOf()


private fun Map<String, Any>.convertNSNulls(): Map<String, Any> =
    mapValues { (_, value) -> convertNSNulls(value = value) }

private fun List<Any>.convertNSNulls(): List<Any> = map(transform = ::convertNSNulls)

@Suppress("UNCHECKED_CAST")
private inline fun convertNSNulls(value: Any): Any = when (value) {
    is NSNull -> JSON_NULL
    is Map<*, *> -> (value as Map<String, Any>).convertNSNulls()
    is List<*> -> (value as List<Any>).convertNSNulls()
    else -> value
}

// SHA256

internal actual fun sha256(bytes: ByteArray): ByteArray {
    val digest = UByteArray(size = CC_SHA256_DIGEST_LENGTH)
    bytes.usePinned { pinnedBytes ->
        digest.usePinned { pinnedDigest ->
            CC_SHA256(
                data = pinnedBytes.addressOf(index = 0),
                len = bytes.size.convert(),
                md = pinnedDigest.addressOf(index = 0),
            )
        }
    }
    return digest.toByteArray()
}

// General

internal fun NSData.toByteArray(): ByteArray = ByteArray(size = length.toInt()).apply {
    usePinned {
        memcpy(__dst = it.addressOf(index = 0), __src = this@toByteArray.bytes, __n = this@toByteArray.length)
    }
}

internal fun ByteArray.toNSData(): NSData = memScoped {
    NSData.dataWithBytes(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.toULong())
}

internal actual fun currentTimeMillis(): Long = NSDate().timeIntervalSince1970.roundToLong()

internal actual fun createDateStringFromEpochMs(format: String, epochMs: Long): String =
    NSDateFormatter().apply {
        locale = NSLocale.currentLocale
        dateFormat = format
    }.stringFromDate(date = NSDate.dateWithTimeIntervalSince1970(secs = epochMs / 1_000.0))

internal actual fun isMainThread(): Boolean = NSThread.isMainThread

internal fun <K: CFStringRef?, V: Any?, T> Map<K, V>.useCFDictionary(block: (dictionary: CFDictionaryRef?) -> T): T {
    val map = this@useCFDictionary
    val retainedItems = mutableListOf<CFTypeRef?>()
    val dictionary = CFDictionaryCreateMutable(
        allocator = kCFAllocatorDefault,
        capacity = size.toLong(),
        keyCallBacks = null,
        valueCallBacks = null,
    )
    return try {
        fun Any?.addToDictAndRegisterForCleanup(key: CFStringRef?) {
            val retained = CFBridgingRetain(X = this)
            retainedItems += retained
            dictionary[key] = retained
        }

        for ((key, value) in map) {
            when (value) {
                is CFTypeRef -> dictionary[key] = value
                is String -> value
                    .asNSString()
                    .asNSData()
                    .addToDictAndRegisterForCleanup(key = key)
                is NSString -> value
                    .asNSData()
                    .addToDictAndRegisterForCleanup(key = key)
                is Boolean -> {
                    val cfBool = if (value) kCFBooleanTrue else kCFBooleanFalse
                    dictionary[key] = cfBool
                }
                is Int -> NSNumber(integer = value.toLong()).asNSData().addToDictAndRegisterForCleanup(key = key)
                is Long -> NSNumber(long = value).asNSData().addToDictAndRegisterForCleanup(key = key)
                null -> dictionary[key] = null
                else -> value.addToDictAndRegisterForCleanup(key = key)
            }
        }

        block(dictionary)
    } finally {
        retainedItems.forEach { CFBridgingRelease(X = it) }
        CFBridgingRelease(X = dictionary)
    }
}

internal operator fun CFMutableDictionaryRef?.set(key: CFStringRef?, value: CFTypeRef?) {
    CFDictionaryAddValue(theDict = this@set, key = key, value = value)
}

internal fun NSString.asNSData(): NSData? = dataUsingEncoding(encoding = NSUTF8StringEncoding)

internal fun NSNumber.asNSData(): NSData? =
    NSKeyedArchiver.archivedDataWithRootObject(
        `object` = this,
        requiringSecureCoding = true,
        error = null,
    )

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(BetaInteropApi::class)
fun NSData.asUTF8(): String? =
    NSString.create(data = this, encoding = NSUTF8StringEncoding)?.asKString()

@Suppress("CAST_NEVER_SUCCEEDS")
fun NSString.asKString(): String = this as String

@Suppress("CAST_NEVER_SUCCEEDS")
fun String.asNSString(): NSString = this as NSString