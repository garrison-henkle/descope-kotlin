package com.descope

import com.descope.internal.DescopeInternal
import com.descope.sdk.DescopeAuth
import com.descope.sdk.DescopeConfig
import com.descope.sdk.DescopeEnchantedLink
import com.descope.sdk.DescopeFlow
import com.descope.sdk.DescopeMagicLink
import com.descope.sdk.DescopeOAuth
import com.descope.sdk.DescopeOtp
import com.descope.sdk.DescopePasskey
import com.descope.sdk.DescopePassword
import com.descope.sdk.DescopeSso
import com.descope.sdk.DescopeTotp
import com.descope.session.DescopeSessionManager

actual object Descope {
    private val delegate = DescopeInternal()

    /**
     * The setup of the `Descope` singleton.
     *
     * Call this function when initializing you application.
     * **This function must be called before the [Descope] object can be used**
     *
     * For example:
     *
     *     Descope.setup(projectId = "DESCOPE_PROJECT_ID") {
     *         baseUrl = "https://my.app.com"
     *         if (BuildConfig.DEBUG) {
     *             logger = DescopeLogger()
     *         }
     *     }
     *
     * @param projectId The Descope project ID
     * @param configure An optional closure that allows to finely configure the Descope SDK
     */
    actual fun setup(projectId: String, configure: DescopeConfig.() -> Unit) = delegate.setup(projectId, configure)

    /**
     *  Manages the storage and lifetime of a [DescopeSession].
     *
     *  You can use this `DescopeSessionManager` object as a shared instance to manage
     *  authenticated sessions in your application.
     *
     *      val authResponse = Descope.otp.verify(DeliveryMethod.Email, "andy@example.com", "123456")
     *      val session = DescopeSession(authResponse)
     *      Descope.sessionManager.manageSession(session)
     *
     *  See the documentation for [DescopeSessionManager] for more details.
     */
    actual var sessionManager: DescopeSessionManager
        get() = delegate.sessionManager
        set(newManager) { delegate.sessionManager = newManager }

    /** Authenticate using an authentication flow */
    actual val flow: DescopeFlow
        get() = delegate.flow

    /** General functions. */
    actual val auth: DescopeAuth
        get() = delegate.auth

    /** Authentication with OTP codes via email or phone. */
    actual val otp: DescopeOtp
        get() = delegate.otp

    /** Authentication with TOTP codes. */
    actual val totp: DescopeTotp
        get() = delegate.totp

    /** Authentication with magic links. */
    actual val magicLink: DescopeMagicLink
        get() = delegate.magicLink

    /** Authentication with enchanted links. */
    actual val enchantedLink: DescopeEnchantedLink
        get() = delegate.enchantedLink

    /** Authentication with OAuth. */
    actual val oauth: DescopeOAuth
        get() = delegate.oauth

    /** Authentication with SSO. */
    actual val sso: DescopeSso
        get() = delegate.sso

    /** Authentication with passkeys. */
    actual val passkey: DescopePasskey
        get() = delegate.passkey

    /** Authentication with passwords. */
    actual val password: DescopePassword
        get() = delegate.password
}