package com.localadmin.manager.feature.network

import android.net.Uri
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localadmin.manager.MyApplication
import com.localadmin.manager.PrivilegeHelper
import com.localadmin.manager.utils.ToastChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkLoggingViewModel(
    val application: MyApplication, val ph: PrivilegeHelper, val tc: ToastChannel,
    val repo: NetworkLoggingRepository
) : ViewModel() {
    val enabledState = MutableStateFlow(false)

    @RequiresApi(26)
    fun getEnabled() = ph.safeDpmCall {
        enabledState.value = dpm.isNetworkLoggingEnabled(dar)
    }

    @RequiresApi(26)
    fun setEnabled(enabled: Boolean) = ph.safeDpmCall {
        dpm.setNetworkLoggingEnabled(dar, enabled)
        getEnabled()
    }

    val countState = MutableStateFlow(0)

    fun getCount() {
        viewModelScope.launch(Dispatchers.IO) {
            countState.value = repo.getNetworkLogsCount().toInt()
        }
    }

    fun exportLogs(uri: Uri, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            application.contentResolver.openOutputStream(uri)?.use {
                repo.exportNetworkLogs(it)
            }
            tc.sendStatus(true)
            withContext(Dispatchers.Main) { callback() }
        }
    }

    fun deleteLogs() {
        repo.deleteNetworkLogs()
    }
}
