package com.descope.session

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.descope.internal.others.WeakReference

internal actual fun attachToPlatformLifecycle(ref: WeakReference<SessionLifecycle>) {
    ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            // application in foreground
            val lifecycle = ref.get() ?: return 
            if (lifecycle.session != null) lifecycle.startTimer(runImmediately = true)
        }

        override fun onStop(owner: LifecycleOwner) {
            // application in background
            val lifecycle = ref.get() ?: return
            lifecycle.stopTimer()
        }
    })
}