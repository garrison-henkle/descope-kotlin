package com.descope.internal.others

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import com.descope.sdk.DescopeSdk
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronizedImpl

class ContextProvider : ContentProvider() {
    override fun attachInfo(context: Context, info: ProviderInfo) {
        // super.attachInfo calls onCreate. Fail as early as possible.
        checkContentProviderAuthority(info = info)
        super.attachInfo(context, info)
    }

    override fun onCreate(): Boolean {
        context?.applicationContext?.also { appContext ->
            appContextRef = WeakReference(appContext)
            (appContext as? Application)?.registerActivityLifecycleCallbacks(activityListener())
                ?: println("[${DescopeSdk.name}] Unable to register Activity lifecycle listener in ContextProvider!") 
        } ?: println("[${DescopeSdk.name}] Unable to retrieve Application Context from ContextProvider!")
        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?,
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
    
    private fun activityListener() = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(p0: Activity) {
            synchronized(activityLock) {
                currentActivityRef = WeakReference(p0)
            }
        }

        override fun onActivityPaused(p0: Activity) {
            synchronized(activityLock) {
                val currentActivity = currentActivityRef?.get()
                if (currentActivityRef != null && currentActivity != null && currentActivity == p0) {
                    currentActivityRef = null
                }
            }
        }

        override fun onActivityCreated(p0: Activity, p1: Bundle?) = Unit
        override fun onActivityStarted(p0: Activity) = Unit
        override fun onActivityStopped(p0: Activity) = Unit
        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) = Unit
        override fun onActivityDestroyed(p0: Activity) = Unit
    }

    companion object {
        private var appContextRef: WeakReference<Context>? = null
        private val activityLock = Any()
        private var currentActivityRef: WeakReference<Activity>? = null
        val appContext: Context? get() = appContextRef?.get()
        val activityContext: Context? get() = currentActivityRef?.get()
        
        internal fun overrideAppContext(context: Context) {
            appContextRef = WeakReference(context)
        }
        
        internal fun overrideActivityContext(activity: Activity) {
            currentActivityRef = WeakReference(activity)
        }

        private const val EMPTY_APPLICATION_ID_PROVIDER_AUTHORITY =
            "com.descope.contextprovider"

        private fun checkContentProviderAuthority(info: ProviderInfo) {
            if (info.authority == EMPTY_APPLICATION_ID_PROVIDER_AUTHORITY) {
                throw IllegalStateException(
                    "Incorrect provider authority in manifest. Most likely due to a missing " +
                        "applicationId variable in application's build.gradle"
                )
            }
        }
    }
}