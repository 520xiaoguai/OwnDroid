package com.bintianqi.owndroid.feature.privilege

import android.app.admin.DevicePolicyManager
import android.os.Build.VERSION
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bintianqi.owndroid.IUserService
import com.bintianqi.owndroid.MyApplication
import com.bintianqi.owndroid.PrivilegeHelper
import com.bintianqi.owndroid.R
import com.bintianqi.owndroid.feature.settings.SettingsRepository
import com.bintianqi.owndroid.useShizuku
import com.bintianqi.owndroid.utils.ACTIVATE_DEVICE_OWNER_COMMAND
import com.bintianqi.owndroid.utils.MyAdminComponent
import com.bintianqi.owndroid.utils.PrivilegeStatus
import com.bintianqi.owndroid.utils.ToastChannel
import com.bintianqi.owndroid.utils.activateOrgProfileCommand
import com.bintianqi.owndroid.utils.getPrivilegeStatus
import com.bintianqi.owndroid.utils.handlePrivilegeChange
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WorkingModesViewModel(
    val application: MyApplication, val ph: PrivilegeHelper, val sr: SettingsRepository,
    val ps: MutableStateFlow<PrivilegeStatus>, val toastChannel: ToastChannel
) : ViewModel() {

    fun getPrivilegeState() = ph.safeDpmCall {
        ps.value = getPrivilegeStatus(dpm, dar)
    }

    @RequiresApi(24)
    fun isCreatingWorkProfileAllowed(): Boolean {
        return ph.myDpm.isProvisioningAllowed(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE)
    }

    fun activateDoByShizuku(callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            useShizuku(application) { service ->
                if (service == null) {
                    callback(false, null)
                    return@useShizuku
                }
                try {
                    val result = IUserService.Stub.asInterface(service)
                        .execute(ACTIVATE_DEVICE_OWNER_COMMAND)
                    if (result == null) {
                        callback(false, null)
                    } else if (result.getInt("code", -1) != 0) {
                        callback(
                            false, result.getString("output") + "\n" + result.getString("error")
                        )
                    } else {
                        updateStatus()
                        callback(
                            true, result.getString("output") + "\n" + result.getString("error")
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(false, null)
                }
            }
        }
    }

    fun activateDoByRoot(callback: (Boolean, String?) -> Unit) {
        Shell.getShell { shell ->
            if (shell.isRoot) {
                val result = Shell.cmd(ACTIVATE_DEVICE_OWNER_COMMAND).exec()
                val output = result.out.joinToString("\n") + "\n" + result.err.joinToString("\n")
                if (result.isSuccess) updateStatus()
                callback(result.isSuccess, output)
            } else {
                callback(false, application.getString(R.string.permission_denied))
            }
        }
    }

    fun activateOrgProfileByShizuku(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            useShizuku(application) { service ->
                if (service == null) {
                    callback(false)
                    toastChannel.sendStatus(false)
                    return@useShizuku
                }
                val result =
                    IUserService.Stub.asInterface(service).execute(activateOrgProfileCommand)
                val succeed = result?.getInt("code", -1) == 0
                callback(succeed)
                if (succeed) {
                    updateStatus()
                } else {
                    toastChannel.sendStatus(false)
                }
            }
        }
    }

    fun deactivate() {
        if (ps.value.device) {
            ph.myDpm.clearDeviceOwnerApp(application.packageName)
        } else if (VERSION.SDK_INT >= 24) {
            ph.myDpm.clearProfileOwner(MyAdminComponent)
        }
        updateStatus()
    }

    private fun updateStatus() = ph.safeDpmCall {
        ps.value = getPrivilegeStatus(dpm, dar)
        handlePrivilegeChange(application, ps.value, ph, sr)
    }
}
