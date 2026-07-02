package com.localadmin.manager.feature.work_profile

data class CreateWorkProfileOptions(
    val skipEncrypt: Boolean,
    val offline: Boolean,
    val migrateAccount: Boolean,
    val accountName: String,
    val accountType: String,
    val keepAccount: Boolean
)
