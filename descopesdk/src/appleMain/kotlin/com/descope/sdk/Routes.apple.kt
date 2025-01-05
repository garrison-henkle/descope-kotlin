package com.descope.sdk

import androidx.annotation.RequiresApi
import com.descope.internal.http.Uri
import com.descope.sdk.DescopeFlow.Authentication
import com.descope.session.DescopeSession
import com.descope.types.AuthenticationResponse
import com.descope.types.OAuthProvider
import com.descope.types.Result
import com.descope.types.SignInOptions
import com.descope.types.SignUpDetails

/**
 * Authenticate a user using an OAuth provider.
 *
 * Use the Descope console to configure which authentication provider you'd like to support.
 *
 * The OAuth protocol is based on creating redirect chain. In order to redirect back to the
 * app it's required to set up deep links or app links. See more here:
 * https://developer.android.com/training/app-links
 *
 * See examples for more information on how to handle deep links.
 */
actual abstract class DescopeOAuth {

    /**
     * Authenticates a new user using an OAuth redirect chain.
     *
     * This function returns a URL to redirect to in order to
     * authenticate the user against the chosen [provider].
     *
     *     // use one of the built in constants for the OAuth provider
     *     val authUrl = Descope.oauth.signUp(OAuthProvider.Github, redirectUrl = "exampleauthschema://my-app.com/handle-oauth")
     *
     *     // or pass a string with the name of a custom provider
     *     val authUrl = Descope.oauth.signUp(OAuthProvider("myprovider"), redirectUrl = "exampleauthschema://my-app.com/handle-oauth")
     *
     * - **Important:** Make sure a default OAuth redirect URL is configured
     * in the Descope console, or provided by this call via [redirectUrl]. It should
     * redirect back to this app using a deep link. See examples for more information.
     *
     * @param provider which provider to authenticate against
     * @param redirectUrl optional redirect URL. If null, the default redirect URL in Descope console will be used.
     * @param options additional behaviors to perform during authentication.
     * @return a URL that starts the OAuth redirect chain
     */
    actual abstract suspend fun signUp(provider: OAuthProvider, redirectUrl: String?, options: List<SignInOptions>?): String

    /** @see signUp */
    actual abstract fun signUp(provider: OAuthProvider, redirectUrl: String?, options: List<SignInOptions>?, callback: (Result<String>) -> Unit)

    /**
     * Authenticates an existing user using an OAuth redirect chain.
     *
     * This function returns a URL to redirect to in order to
     * authenticate the user against the chosen [provider].
     *
     *     // use one of the built in constants for the OAuth provider
     *     val authUrl = Descope.oauth.signIn(OAuthProvider.Github, redirectUrl = "exampleauthschema://my-app.com/handle-oauth")
     *
     *     // or pass a string with the name of a custom provider
     *     val authUrl = Descope.oauth.signIn(OAuthProvider("myprovider"), redirectUrl = "exampleauthschema://my-app.com/handle-oauth")
     *
     * - **Important:** Make sure a default OAuth redirect URL is configured
     * in the Descope console, or provided by this call via [redirectUrl]. It should
     * redirect back to this app using a deep link. See examples for more information.
     *
     * @param provider which provider to authenticate against
     * @param redirectUrl optional redirect URL. If null, the default redirect URL in Descope console will be used.
     * @param options additional behaviors to perform during authentication.
     * @return a URL that starts the OAuth redirect chain
     */
    actual abstract suspend fun signIn(provider: OAuthProvider, redirectUrl: String?, options: List<SignInOptions>?): String

    /** @see signIn */
    actual abstract fun signIn(provider: OAuthProvider, redirectUrl: String?, options: List<SignInOptions>?, callback: (Result<String>) -> Unit)

    /**
     * Authenticate an existing user if one exists, or create a new user using an
     * OAuth redirect chain.
     *
     * This function returns a URL to redirect to in order to
     * authenticate the user against the chosen [provider].
     *
     *     // use one of the built in constants for the OAuth provider
     *     val authUrl = Descope.oauth.signUpOrIn(OAuthProvider.Github, redirectUrl = "exampleauthschema://my-app.com/handle-oauth")
     *
     *     // or pass a string with the name of a custom provider
     *     val authUrl = Descope.oauth.signUpOrIn(OAuthProvider("myprovider"), redirectUrl = "exampleauthschema://my-app.com/handle-oauth")
     *
     * - **Important:** Make sure a default OAuth redirect URL is configured
     * in the Descope console, or provided by this call via [redirectUrl]. It should
     * redirect back to this app using a deep link. See examples for more information.
     *
     * @param provider which provider to authenticate against
     * @param redirectUrl optional redirect URL. If null, the default redirect URL in Descope console will be used.
     * @param options additional behaviors to perform during authentication.
     * @return a URL that starts the OAuth redirect chain
     */
    actual abstract suspend fun signUpOrIn(provider: OAuthProvider, redirectUrl: String?, options: List<SignInOptions>?): String

    /** @see signUpOrIn */
    actual abstract fun signUpOrIn(provider: OAuthProvider, redirectUrl: String?, options: List<SignInOptions>?, callback: (Result<String>) -> Unit)

    /**
     * Starts an OAuth redirect chain to authenticate a user.
     *
     * This function returns a URL to redirect to in order to
     * authenticate the user against the chosen [provider].
     *
     *     // use one of the built in constants for the OAuth provider
     *     val authUrl = Descope.oauth.start(OAuthProvider.Github, redirectUrl = "exampleauthschema://my-app.com/handle-oauth")
     *
     *     // or pass a string with the name of a custom provider
     *     val authUrl = Descope.oauth.start(OAuthProvider("myprovider"), redirectUrl = "exampleauthschema://my-app.com/handle-oauth")
     *
     * - **Important:** Make sure a default OAuth redirect URL is configured
     * in the Descope console, or provided by this call via [redirectUrl]. It should
     * redirect back to this app using a deep link. See examples for more information.
     *
     * @param provider which provider to authenticate against
     * @param redirectUrl optional redirect URL. If null, the default redirect URL in Descope console will be used.
     * @param options additional behaviors to perform during authentication.
     * @return a URL that starts the OAuth redirect chain
     */
    @Deprecated(message = "Use signUpOrIn instead", replaceWith = ReplaceWith("signUpOrIn(provider, redirectUrl, options)"))
    actual abstract suspend fun start(provider: OAuthProvider, redirectUrl: String?, options: List<SignInOptions>?): String

    /** @see start */
    @Deprecated(message = "Use signUpOrIn instead", replaceWith = ReplaceWith("signUpOrIn(provider, redirectUrl, options, callback)"))
    actual abstract fun start(provider: OAuthProvider, redirectUrl: String?, options: List<SignInOptions>?, callback: (Result<String>) -> Unit)

    /**
     * Completes an OAuth redirect chain.
     *
     * This function exchanges the [code] received in the `code` URL
     * parameter for an [AuthenticationResponse].
     *
     * - **Important:** The redirect URL might not contain a code URL parameter
     *   but can contain an `err` URL parameter instead. This can occur when attempting to
     *   [signUp] with an existing user or trying to [signIn] with a non-existing
     *   user.
     *
     * @param code received in the final redirect as a url parameter named `code`
     * @return an [AuthenticationResponse] upon successful verification.
     */
    actual abstract suspend fun exchange(code: String): AuthenticationResponse

    /** @see exchange */
    actual abstract fun exchange(code: String, callback: (Result<AuthenticationResponse>) -> Unit)

    /**
     * Authenticates the user using the native Sign in with Google dialog.
     *
     * This API enables a more streamlined user experience than the equivalent browser
     * based OAuth authentication, when using the `Google` provider or a custom provider
     * that's configured for Google. The authentication presents a native dialog that lets
     * the user sign in with the Google account they're already using on their device.
     *
     * If you haven't already configured your app to support Sign in with Google you'll
     * probably need to set up your [Google APIs console project](https://developers.google.com/identity/one-tap/android/get-started#api-console)
     * for this. You should also configure an OAuth provider for Google in the in the [Descope console](https://app.descope.com/settings/authentication/social),
     * with its `Grant Type` set to `Implicit`. Also note that the `Client ID` and
     * `Client Secret` should be set to the values of your `Web application` OAuth client,
     * rather than those from the `Android` OAuth client.
     *
     * For more details about configuring your app see the [Credential Manager documentation](https://developer.android.com/training/sign-in/credential-manager).
     *
     * Note: This is an asynchronous operation that performs network requests before and
     * after displaying the modal authentication view. It is thus recommended to switch the
     * user interface to a loading state before calling this function, otherwise the user
     * might accidentally interact with the app when the authentication view is not
     * being displayed.
     *
     * @param provider the provider the user wishes to authenticate with, this will usually
     * either be `Google` or the name of a custom provider that's configured for Google.
     * @param options additional behaviors to perform during authentication.
     * @return an [AuthenticationResponse] upon successful authentication.
     */
    actual abstract suspend fun native(provider: OAuthProvider, options: List<SignInOptions>?): AuthenticationResponse

    /** @see native */
    actual abstract fun native(provider: OAuthProvider, options: List<SignInOptions>?, callback: (Result<AuthenticationResponse>) -> Unit)
}

/**
 * Authenticate users using passkeys.
 *
 * The authentication operations in this interface are all suspending functions that
 * perform network requests before and after displaying the modal authentication view.
 * It is thus recommended to switch the user interface to a loading state before calling
 * this function, otherwise the user might accidentally interact with the app when the
 * authentication view is not being displayed.
 *
 * - **Important**: Before authentication via passkeys is possible, some set up is required.
 * Please follow the [Add support for Digital Asset Links](https://developer.android.com/training/sign-in/passkeys#add-support-dal)
 * setup, as described in the official Google docs.
 */
actual abstract class DescopePasskey {
    /**
     * Authenticates a new user by creating a new passkey.
     *
     * @param loginId What identifies the user when logging in.
     * @param details Optional details about the user signing up.
     * @return An [AuthenticationResponse] value upon successful authentication.
     */
    @RequiresApi(value = 28)
    actual abstract suspend fun signUp(loginId: String, details: SignUpDetails?): AuthenticationResponse

    /**
     * Authenticates an existing user by prompting for an existing passkey.
     *
     * @param loginId What identifies the user when logging in.
     * @param options Additional behaviors to perform during authentication.
     * @return An [AuthenticationResponse] value upon successful authentication.
     */
    @RequiresApi(value = 28)
    actual abstract suspend fun signIn(loginId: String, options: List<SignInOptions>?): AuthenticationResponse

    /**
     * Authenticates an existing user if one exists or creates a new one.
     *
     * A new passkey will be created if the user doesn't already exist, otherwise a passkey
     * must be available on their device to authenticate with.
     *
     * @param loginId What identifies the user when logging in
     * @param options Additional behaviors to perform during authentication.
     * @return An [AuthenticationResponse] value upon successful authentication.
     */
    @RequiresApi(value = 28)
    actual abstract suspend fun signUpOrIn(loginId: String, options: List<SignInOptions>?): AuthenticationResponse

    /**
     * Updates an existing user by adding a new passkey as an authentication method.
     *
     * @param loginId What identifies the user when logging in
     * @param refreshJwt The `refreshJwt` from an active [DescopeSession].
     */
    @RequiresApi(value = 28)
    actual abstract suspend fun add(loginId: String, refreshJwt: String)

}

/**
 * A helper interface that encapsulates a single flow run.
 *
 * First create a new `Runner` using the [DescopeFlow.create] method.
 * Then [start] the flow where needed.
 *
 * In case the flow uses `Magic Link Authentication` call [resume] when on the captured
 * incoming URI. For your convenience, the [DescopeFlow.currentRunner] is available to access the current
 * flow runner.
 */
actual abstract class DescopeFlowRunner {
    /** Optional authentication info to allow running flows for authenticated users */
    actual abstract var flowAuthentication: Authentication?

    /** Optional overrides and customizations to the flow's presentation */
    actual abstract var flowPresentation: DescopeFlowPresentation?

    /**
     * Start a user authentication flow.
     *
     * If the user has an **active session** and this [DescopeFlowRunner] was provided with [Authentication],
     * this flow will run with the user logged in.
     *
     * Note: This is an asynchronous operation that might perform network requests before
     * opening the browser. It is thus recommended to switch the
     * user interface to a loading state before calling this function, otherwise the user
     * might accidentally interact with the app when the authentication view is not
     * being displayed.
     *
     */
    actual abstract suspend fun start()

    /** @see start */
    actual abstract fun start(callback: (Result<Unit>) -> Unit)

    /**
     * Resumes an ongoing flow after a redirect back to the app.
     * This is required for *Magic Link only* at this stage.
     *
     * - **Note:** This requires additional setup on the application side.
     *  See the examples for more details.
     *
     * @param incomingUriString the URI received when redirecting back to the app.
     */
    actual abstract fun resume(incomingUriString: String)

    /**
     * Handles the final flow redirect response and exchanges it for an [AuthenticationResponse].
     *
     * Provide this function the [incomingUri] from the handling activity, e.g.
     *
     *     val incomingUri: Uri = intent?.data ?: return
     *
     * @param incomingUri the URI passed to the deep link handling Activity.
     * @return an [AuthenticationResponse] upon successful verification.
     */
    actual abstract suspend fun exchange(incomingUri: Uri): AuthenticationResponse

    /** @see exchange */
    actual abstract fun exchange(incomingUri: Uri, callback: (Result<AuthenticationResponse>) -> Unit)
}

/**
 * Customize the flow's presentation by implementing the [DescopeFlowPresentation] interface.
 */
actual abstract class DescopeFlowPresentation