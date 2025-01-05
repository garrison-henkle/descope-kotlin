package com.descope.session

import com.descope.internal.others.ContextProvider
import com.descope.sdk.DescopeLogger
import com.descope.sdk.DescopeLogger.Level.Debug
import com.descope.sdk.DescopeLogger.Level.Error

internal actual fun createdEncryptedStore(projectId: String, logger: DescopeLogger?): SessionStorage.Store =
    ContextProvider.appContext?.let { context ->
        try {
            val storage = EncryptedSharedPrefs(projectId, context)
            logger?.log(Debug, "Encrypted storage initialized successfully")
            return storage
        } catch (e: Exception) {
            try {
                logger?.log(Error, "Encrypted storage key unusable")
                context.deleteSharedPreferences(projectId)
                EncryptedSharedPrefs(projectId, context)
            } catch (e: Exception) {
                logger?.log(Error, "Unable to initialize encrypted storage", e)
                SessionStorage.Store.none
            }
        } 
    } ?: run {
        logger?.log(Error, "Unable to initialize encrypted storage", "ContextProvider.context was null")
        SessionStorage.Store.none
    }