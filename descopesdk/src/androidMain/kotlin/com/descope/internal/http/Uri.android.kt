package com.descope.internal.http

import java.net.URL

internal actual fun parseUri(uriString: String): Uri = android.net.Uri.parse(uriString)

// This feature is now supported per comment from Jetbrains here:
// https://youtrack.jetbrains.com/issue/KT-22841/Prohibit-different-member-scopes-for-non-final-expect-and-its-actual#focus=Comments-27-8287209.0-0
@Suppress("ACTUAL_CLASSIFIER_MUST_HAVE_THE_SAME_MEMBERS_AS_NON_FINAL_EXPECT_CLASSIFIER_WARNING")
actual typealias Uri = android.net.Uri

actual typealias UriBuilder = android.net.Uri.Builder

fun Uri.toURL(): URL = URL(this@toURL.toString())