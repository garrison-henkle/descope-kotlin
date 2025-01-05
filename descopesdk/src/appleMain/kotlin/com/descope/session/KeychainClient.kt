package com.descope.session

import com.descope.internal.others.asNSData
import com.descope.internal.others.asNSString
import com.descope.internal.others.asUTF8
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSString
import platform.darwin.OSStatus

interface KeychainClient {
    @Throws(Error::class)
    fun get(item: KeychainItem): List<QueryResult>

    fun valueExistsForItem(item: KeychainItem): Boolean

    @Throws(Error::class)
    fun setValueForItem(item: KeychainItem, value: KeychainItem.Value)

    @Throws(Error::class)
    fun removeItem(item: KeychainItem)

    @Throws(Error::class)
    fun getString(item: KeychainItem): String? = get(item = item).firstOrNull()?.data?.asUTF8()

    @Throws(Error::class)
    fun setString(item: KeychainItem, value: String) {
        value.asNSString().asNSData()?.also { nsData ->
            setValueForItem(
                item = item,
                value = KeychainItem.Value(
                    data = nsData,
                    account = null,
                    label = null,
                    generic = null,
                    accessPolicy = null,
                ),
            )
        }
    }

    data class QueryResult(
        val data: NSData,
        val createdAt: NSDate,
        val modifiedAt: NSDate,
        val label: String?,
        val account: String?,
        val generic: NSData?,
    )

    sealed class Error(message: String? = null) : Exception(message = message) {
        data object ResultMissingAccount : Error()
        data object ResultMissingDates : Error()
        data object ResultNotData: Error()
        data object UnableToCreateAccessControl : Error()
        data class UnhandledError(val status: OSStatus) : Error(message = "Unhandled error: $status")
    }
}