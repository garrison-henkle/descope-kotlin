package com.descope.internal

import com.descope.sdk.DescopeAuth
import com.descope.sdk.DescopeConfig
import com.descope.sdk.DescopeEnchantedLink
import com.descope.sdk.DescopeFlow
import com.descope.sdk.DescopeMagicLink
import com.descope.sdk.DescopeOAuth
import com.descope.sdk.DescopeOtp
import com.descope.sdk.DescopePasskey
import com.descope.sdk.DescopePassword
import com.descope.sdk.DescopeSdk
import com.descope.sdk.DescopeSso
import com.descope.sdk.DescopeTotp
import com.descope.session.DescopeSessionManager

internal class DescopeInternal {
    fun setup(
        projectId: String,
        configure: DescopeConfig.() -> Unit,
    ) {
        sdk = DescopeSdk(projectId, configure)
    }

    var sessionManager: DescopeSessionManager
        get() = sdk.sessionManager
        set(value) {
            sdk.sessionManager = value
        }

    val flow: DescopeFlow
        get() = sdk.flow

    val auth: DescopeAuth
        get() = sdk.auth

    val otp: DescopeOtp
        get() = sdk.otp

    val totp: DescopeTotp
        get() = sdk.totp

    val magicLink: DescopeMagicLink
        get() = sdk.magicLink

    val enchantedLink: DescopeEnchantedLink
        get() = sdk.enchantedLink

    val oauth: DescopeOAuth
        get() = sdk.oauth

    val sso: DescopeSso
        get() = sdk.sso

    val passkey: DescopePasskey
        get() = sdk.passkey

    val password: DescopePassword
        get() = sdk.password

    lateinit var sdk: DescopeSdk
}