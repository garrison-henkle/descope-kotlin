package com.descope.session

import com.descope.internal.others.WeakReference
import com.descope.internal.others.currentTimeMillis
import com.descope.sdk.DescopeAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val SECOND = 1000L

/**
 * This interface can be used to customize how a [DescopeSessionManager] object
 * manages its [DescopeSession] while the application is running.
 */
interface DescopeSessionLifecycle {
    /** Set by the session manager whenever the current active session changes. */
    var session: DescopeSession?

    /** Called the session manager to conditionally refresh the active session. */
    suspend fun refreshSessionIfNeeded()
}

/**
 * The default implementation of the `DescopeSessionLifecycle` interface.
 *
 * The `SessionLifecycle` class periodically checks if the session needs to be
 * refreshed (every 30 seconds by default). The [refreshSessionIfNeeded] function
 * will refresh the session if it's about to expire (within 60 seconds by default)
 * or if it's already expired.
 *
 * @property auth used to refresh the session when needed
 */
class SessionLifecycle(private val auth: DescopeAuth) : DescopeSessionLifecycle {

    var stalenessAllowedInterval: Long = 60L /* seconds */ * SECOND
    var stalenessCheckFrequency: Long = 30L /* seconds */ * SECOND

    init { attachToPlatformLifecycle(ref = WeakReference(referred = this@SessionLifecycle)) }

    override var session: DescopeSession? = null
        set(value) {
            if (field == value) return
            field = value
            if (value == null) {
                stopTimer()
            } else {
                startTimer()
            }
        }

    override suspend fun refreshSessionIfNeeded() {
        session?.run {
            if (shouldRefresh(this)) {
                val response = auth.refreshSession(refreshJwt) // TODO check for refresh failure to not try again and again after expiry
                updateTokens(response)
            }
        }
    }

    // Internal

    private fun shouldRefresh(session: DescopeSession): Boolean {
        return session.sessionToken.expiresAt - currentTimeMillis() <= stalenessAllowedInterval
    }

    // Timer

    private var timer: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    internal fun startTimer(runImmediately: Boolean = false) {
        val weakRef = WeakReference(this)
        val delay = if (runImmediately) 0L else stalenessCheckFrequency
        timer?.cancel()
        timer = GlobalScope.launch(Dispatchers.IO) timerTask@{
            delay(timeMillis = delay)
            while(true) {
                val ref = weakRef.get()
                if (ref == null) {
                    stopTimer()
                    return@timerTask
                }
                withContext(Dispatchers.Main) {
                    try {
                        ref.refreshSessionIfNeeded()
                    } catch (ignored: Exception) {
                    }
                }
                delay(timeMillis = stalenessCheckFrequency)
            }
        }
    }

    internal fun stopTimer() {
        timer?.cancel()
        timer = null
    }

}

internal expect fun attachToPlatformLifecycle(ref: WeakReference<SessionLifecycle>)