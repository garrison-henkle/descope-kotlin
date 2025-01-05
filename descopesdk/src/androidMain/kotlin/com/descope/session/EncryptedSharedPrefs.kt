package com.descope.session

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * A [SessionStorage.Store] implementation using [EncryptedSharedPreferences] as
 * the backing store.
 */
class EncryptedSharedPrefs(name: String, context: Context) : SessionStorage.Store {
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        name,
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun loadItem(key: String): String? = sharedPreferences.getString(key, null)

    override fun saveItem(key: String, data: String) = sharedPreferences.edit()
        .putString(key, data)
        .apply()

    override fun removeItem(key: String) = sharedPreferences.edit()
        .remove(key)
        .apply()
}