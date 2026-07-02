package com.localadmin.manager.feature.privilege

import android.app.admin.DevicePolicyManager
import android.content.pm.PackageManager
import android.os.Build.VERSION
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localadmin.manager.IUserService
import com.localadmin.manager.MyApplication
import com.localadmin.manager.PrivilegeHelper
import com.localadmin.manager.R
import com.localadmin.manager.feature.settings.SettingsRepository
import com.localadmin.manager.useShizuku
import com.localadmin.manager.utils.ACTIVATE_DEVICE_OWNER_COMMAND
import com.localadmin.manager.utils.MyAdminComponent
import com.localadmin.manager.utils.PrivilegeStatus
import com.localadmin.manager.utils.ToastChannel
import com.localadmin.manager.utils.activateOrgProfileCommand
import com.localadmin.manager.utils.getPrivilegeStatus
import com.localadmin.manager.utils.handlePrivilegeChange
import com.rosan.dhizuku.api.Dhizuku
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WorkingModesViewModel(
    val application: MyApplication, val ph: PrivilegeHelper, val sr: SettingsRepository,
    val ps: MutableStateFlow<PrivilegeStatus>, val toastChannel: ToastChannel
) : ViewModel() {

    fun getPrivilegeState() = ph.safeDpmCall {
        ps.value = getPrivilegeStatus(dpm, dar, ph.dhizuku)
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

    @RequiresApi(28)
    fun activateDoByDhizuku(callback: (Boolean, String?) -> Unit) = ph.safeDpmCall {
        dpm.transferOwnership(dar, MyAdminComponent, null)
        sr.update { it.privilege.dhizuku = false }
        ph.dhizuku = false
        updateStatus()
        callback(true, null)
    }

    fun activateDhizukuMode(callback: (Boolean, String?) -> Unit) {
        fun onSucceed() {
            sr.update { it.privilege.dhizuku = true }
            ph.dhizuku = true
            updateStatus()
            callback(true, null)
        }
        if (Dhizuku.init(application)) {
            if (Dhizuku.isPermissionGranted()) {
                onSucceed()
            } else {
                Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
                    override fun onRequestPermission(grantResult: Int) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) onSucceed()
                        else callback(
                            false, application.getString(R.string.dhizuku_permission_not_granted)
                        )
                    }
                })
            }
        } else {
            callback(false, application.getString(R.string.failed_to_init_dhizuku))
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
        if (ps.value.dhizuku) {
            sr.update { it.privilege.dhizuku = false }
            ph.dhizuku = false
        } else {
            if (ps.value.device) {
                ph.myDpm.clearDeviceOwnerApp(application.packageName)
            } else if (VERSION.SDK_INT >= 24) {
                ph.myDpm.clearProfileOwner(MyAdminComponent)
            }
        }
        updateStatus()
    }

    private fun updateStatus() = ph.safeDpmCall {
        ps.value = getPrivilegeStatus(dpm, dar, ph.dhizuku)
        handlePrivilegeChange(application, ps.value, ph, sr)
    }
}
