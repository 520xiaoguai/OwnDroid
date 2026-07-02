package com.localadmin.manager.feature.work_profile

import android.os.Build.VERSION
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.localadmin.manager.PrivilegeHelper
import com.localadmin.manager.utils.PrivilegeStatus
import com.localadmin.manager.utils.ToastChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkProfileViewModel(
    val ph: PrivilegeHelper, val privilegeState: StateFlow<PrivilegeStatus>,
    val toastChannel: ToastChannel
) : ViewModel() {
    val personalAppSuspendedState = MutableStateFlow(0)

    @RequiresApi(30)
    fun getPersonalAppsSuspendedReason() = ph.safeDpmCall {
        personalAppSuspendedState.value = dpm.getPersonalAppsSuspendedReasons(dar)
    }

    @RequiresApi(30)
    fun setPersonalAppsSuspended(suspended: Boolean) = ph.safeDpmCall {
        dpm.setPersonalAppsSuspended(dar, suspended)
        getPersonalAppsSuspendedReason()
    }

    val profileMaxTimeOffState = MutableStateFlow("")

    @RequiresApi(30)
    fun getProfileMaxTimeOff() = ph.safeDpmCall {
        profileMaxTimeOffState.value = dpm.getManagedProfileMaximumTimeOff(dar).toString()
    }

    fun setProfileMaxTimeOff(input: String) {
        profileMaxTimeOffState.value = input
    }

    @RequiresApi(30)
    fun applyProfileMaxTimeOff() = ph.safeDpmCall {
        dpm.setManagedProfileMaximumTimeOff(dar, profileMaxTimeOffState.value.toLong())
        toastChannel.sendStatus(true)
    }

    fun deleteProfile(flags: Int, reason: String) = ph.safeDpmCall {
        if (VERSION.SDK_INT >= 28 && reason.isNotEmpty()) {
            dpm.wipeData(flags, reason)
        } else {
            dpm.wipeData(flags)
        }
    }
}
