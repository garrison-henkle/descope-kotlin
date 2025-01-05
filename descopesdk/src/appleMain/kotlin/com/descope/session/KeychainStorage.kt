package com.descope.session

import com.descope.sdk.DescopeLogger
import com.descope.sdk.DescopeLogger.Level.Error

internal class KeychainStorage(
    private val client: KeychainClient,
    private val logger: DescopeLogger?,
) : SessionStorage.Store {
    override fun loadItem(key: String): String? = try {
        client.getString(item = KeychainItem(name = key))
    } catch(ignored: Exception) {
        logger?.log(Error, "Unable to load key '$key' from the Keychain!")
        null
    }

    override fun saveItem(key: String, data: String) {
        try {
            client.setString(item = KeychainItem(name = key), value = data)
        } catch(ignored: Exception) {
            logger?.log(Error, "Unable to save key '$key' to the Keychain!")
            logger
        }
    }

    override fun removeItem(key: String) {
        try {
            client.removeItem(item = KeychainItem(name = key))
        } catch(ignored: Exception) {
            logger?.log(Error, "Unable to remove key '$key' from the Keychain!")
        }
    }
}