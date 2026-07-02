package com.localadmin.manager.feature.settings

import androidx.annotation.Keep
import com.localadmin.manager.R
import kotlinx.serialization.Serializable

@Serializable
data class MySettings(
    val privilege: Privilege = Privilege(),
    var theme: Theme = Theme(),
    val appLock: AppLock = AppLock(),
    val shortcut: Shortcut = Shortcut(),
    val notifications: MutableList<Int> = mutableListOf(),
    var displayDangerousFeatures: Boolean = false,
    var applicationsListView: Boolean = true,
) {
    @Serializable
    data class Privilege(
        var dhizuku: Boolean = false,
        var dhizukuServer: Boolean = false,
        var managedProfileActivated: Boolean = false,
        var defaultAffiliationIdSet: Boolean = false,
    )

    // Use `val` since it is used as UI state
    @Serializable
    data class Theme(
        val materialYou: Boolean = true,
        val dark: DarkMode = DarkMode.FollowSystem,
        val black: Boolean = false,
    )

    @Keep
    enum class DarkMode(val text: Int) {
        FollowSystem(R.string.follow_system), On(R.string.on), Off(R.string.off)
    }

    @Serializable
    data class AppLock(
        var passwordHash: String = "",
        var biometrics: Boolean = false,
        var lockWhenLeaving: Boolean = false,
        var securityQuestion: String = "",
        var securityAnswerHash: String = "",
    )

    @Serializable
    data class Shortcut(
        var enabled: Boolean = true,
        var key: String = "",
    )
}
