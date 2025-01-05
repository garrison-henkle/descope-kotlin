package com.descope.session

import com.descope.sdk.DescopeLogger

internal actual fun createdEncryptedStore(projectId: String, logger: DescopeLogger?): SessionStorage.Store =
    KeychainStorage(projectId = projectId, logger = logger)