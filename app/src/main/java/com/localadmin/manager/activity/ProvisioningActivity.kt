package com.localadmin.manager.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.localadmin.manager.MyApplication
import com.localadmin.manager.feature.provisioning.ProvisioningScreen
import com.localadmin.manager.feature.provisioning.ProvisioningViewModel
import com.localadmin.manager.ui.theme.DeviceManagerTheme

@RequiresApi(29)
class ProvisioningActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myApp = application as MyApplication
        val vm by viewModels<ProvisioningViewModel>()
        vm.params = vm.getParamsFromIntent(intent)
        setContent {
            val theme by myApp.container.themeState.collectAsState()
            DeviceManagerTheme(theme) {
                ProvisioningScreen(vm.params) {
                    setResult(RESULT_OK, vm.buildResultIntent(it))
                    finish()
                }
            }
        }
    }
}
