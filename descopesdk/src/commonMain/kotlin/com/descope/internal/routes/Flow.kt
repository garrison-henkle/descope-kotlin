package com.descope.internal.routes

import com.descope.internal.http.DescopeClient
import com.descope.internal.http.Uri
import com.descope.internal.http.parseUri
import com.descope.internal.others.sha256
import com.descope.internal.others.toBase64
import com.descope.internal.others.with
import com.descope.sdk.DescopeFlow
import com.descope.sdk.DescopeFlowPresentation
import com.descope.sdk.DescopeFlowRunner
import com.descope.sdk.DescopeLogger.Level.Info
import com.descope.types.AuthenticationResponse
import com.descope.types.DescopeException
import com.descope.types.Result
import kotlin.random.Random

internal class Flow(
    override val client: DescopeClient
) : Route, DescopeFlow {

    override var currentRunner: DescopeFlowRunner? = null
        private set

    override fun create(flowUrl: String, deepLinkUrl: String, backupCustomScheme: String?): DescopeFlowRunner {
        val runner = FlowRunner(flowUrl, deepLinkUrl, backupCustomScheme)
        currentRunner = runner
        return runner
    }

    inner class FlowRunner(
        private val flowUrl: String,
        private val deepLinkUrl: String,
        private val backupCustomScheme: String?,
    ) : DescopeFlowRunner() {

        private lateinit var codeVerifier: String

        override var flowPresentation: DescopeFlowPresentation? = null
        override var flowAuthentication: DescopeFlow.Authentication? = null

        override suspend fun start() {
            log(Info, "Starting flow", flowUrl)
            val codeChallenge = initVerifierAndChallenge()
            flowAuthentication?.run { client.flowPrime(codeChallenge, flowId, refreshJwt) }
            startFlowViaBrowser(codeChallenge)
        }

        override fun start(callback: (Result<Unit>) -> Unit) = wrapCoroutine(callback) {
            start()
        }

        override fun resume(incomingUriString: String) {
            // create the redirect flow URL by copying all url parameters received from the incoming URI
            val incomingUri = parseUri(incomingUriString)
            val uriBuilder = parseUri(flowUrl).buildUpon()
            incomingUri.getQueryParameterNames()
                .forEach { uriBuilder.appendQueryParameter(it, incomingUri.getQueryParameter(it)!!) }
            val uri = uriBuilder.build()

            // launch via chrome custom tabs
            launchUri(uri)
        }

        override suspend fun exchange(incomingUri: Uri): AuthenticationResponse {
            // make sure start has been called
            if (!this::codeVerifier.isInitialized) throw DescopeException.flowFailed.with(desc = "`start(context)` must be called before exchange")

            // get the `code` url param from the incoming uri and exchange it
            val authorizationCode = incomingUri.getQueryParameter("code") ?: throw DescopeException.flowFailed.with(desc = "No code parameter on incoming URI")
            log(Info, "Exchanging flow authorization code for session", authorizationCode)
            if (currentRunner === this) currentRunner = null
            return client.flowExchange(authorizationCode, codeVerifier).convert()
        }

        override fun exchange(incomingUri: Uri, callback: (Result<AuthenticationResponse>) -> Unit) = wrapCoroutine(callback) {
            exchange(incomingUri)
        }

        // Internal

        private fun initVerifierAndChallenge(): String {
            // create some random bytes
            val randomBytes = ByteArray(32)
            Random.nextBytes(randomBytes)

            // codeVerifier == base64(randomBytes)
            codeVerifier = randomBytes.toBase64()

            // hash bytes using sha256
            val hashed = sha256(randomBytes)

            // codeChallenge == base64(sha256(randomBytes))
            return hashed.toBase64()
        }

        private fun startFlowViaBrowser(codeChallenge: String) {
            val uriBuilder = parseUri(flowUrl).buildUpon()
                .appendQueryParameter("ra-callback", deepLinkUrl)
                .appendQueryParameter("ra-challenge", codeChallenge)
                .appendQueryParameter("ra-initiator", "android")
            backupCustomScheme?.let {
                uriBuilder.appendQueryParameter("ra-backup-callback", it)
            }
            val uri = uriBuilder.build()

            // launch via embedded browser
            launchUri(uri)
        }

        private fun launchUri(uri: Uri) {
            val customTabsIntent = flowPresentation?.createCustomTabsIntent(context) ?: defaultCustomTabIntent()
            customTabsIntent.launchUrl(context, uri)
        }

    }

}
