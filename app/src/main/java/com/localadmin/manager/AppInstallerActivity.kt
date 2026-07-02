package com.localadmin.manager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import com.localadmin.manager.feature.applications.AppInstaller
import com.localadmin.manager.feature.applications.AppInstallerViewModel
import com.localadmin.manager.ui.theme.OwnDroidTheme
import com.localadmin.manager.utils.viewModelFactory

class AppInstallerActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val myApp = application as MyApplication
        val vm by viewModels<AppInstallerViewModel> {
            viewModelFactory {
                AppInstallerViewModel(myApp, myApp.container.settingsRepo)
            }
        }
        vm.initialize(intent)
        val themeState = myApp.container.themeState
        setContent {
            val theme by themeState.collectAsState()
            OwnDroidTheme(theme) {
                AppInstaller(vm)
            }
        }
    }
}
