package com.localadmin.manager

import com.localadmin.manager.feature.applications.AppGroupRepository
import com.localadmin.manager.feature.network.NetworkLoggingRepository
import com.localadmin.manager.feature.privilege.DhizukuServerRepository
import com.localadmin.manager.feature.settings.SettingsRepository
import com.localadmin.manager.feature.system.SecurityLoggingRepository
import com.localadmin.manager.feature.work_profile.CrossProfileIntentFilterRepository
import com.localadmin.manager.utils.DhizukuError
import com.localadmin.manager.utils.PrivilegeStatus
import com.localadmin.manager.utils.ToastChannel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

class AppContainer(val app: MyApplication) {
    val dbHelper = MyDbHelper(app)
    val networkLoggingRepo = NetworkLoggingRepository(dbHelper)
    val securityLoggingRepo = SecurityLoggingRepository(dbHelper)
    val appGroupRepo = AppGroupRepository(dbHelper)
    val dhizukuServerRepo = DhizukuServerRepository(dbHelper)
    val cpifRepo = CrossProfileIntentFilterRepository(dbHelper)
    val settingsRepo = SettingsRepository(app.filesDir.resolve("settings.json"))
    val dhizukuErrorState = MutableStateFlow<DhizukuError?>(null)
    val privilegeHelper = PrivilegeHelper(
        app, settingsRepo.data.privilege.dhizuku, dhizukuErrorState
    )
    val privilegeState = MutableStateFlow(PrivilegeStatus())
    val appGroupsState = MutableStateFlow(appGroupRepo.getAppGroups())
    val chosenPackage = Channel<String>(1, BufferOverflow.DROP_LATEST)
    val themeState = MutableStateFlow(settingsRepo.data.theme)
    val toastChannel = ToastChannel(app)
    val viewModelFactory = MyViewModelFactory(
        app, privilegeHelper, settingsRepo, networkLoggingRepo, dhizukuServerRepo,
        securityLoggingRepo, appGroupRepo, cpifRepo, appGroupsState, dhizukuErrorState,
        privilegeState, themeState, toastChannel
    )
}
