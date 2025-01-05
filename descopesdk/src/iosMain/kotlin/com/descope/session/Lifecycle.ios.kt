package com.descope.session

import com.descope.internal.others.WeakReference
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.darwin.NSObjectProtocol
import kotlin.experimental.ExperimentalNativeApi

private var willEnterForegroundObserver: NSObjectProtocol? = null
private var didEnterBackgroundObserver: NSObjectProtocol? = null

@Suppress("UnnecessaryOptInAnnotation") // compilation fails without the opt-in
@OptIn(ExperimentalNativeApi::class)
internal actual fun attachToPlatformLifecycle(ref: WeakReference<SessionLifecycle>) {
    willEnterForegroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
        name = UIApplicationWillEnterForegroundNotification,
        `object` = UIApplication.sharedApplication,
        queue = NSOperationQueue.mainQueue,
        usingBlock = block@{
            val lifecycle = ref.get() ?: return@block
            if (lifecycle.session != null) lifecycle.startTimer(runImmediately = true)
        },
    )
    didEnterBackgroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
        name = UIApplicationDidEnterBackgroundNotification,
        `object` = UIApplication.sharedApplication,
        queue = NSOperationQueue.mainQueue,
        usingBlock = block@{
            val lifecycle = ref.get() ?: return@block
            lifecycle.stopTimer()
        },
    )
}