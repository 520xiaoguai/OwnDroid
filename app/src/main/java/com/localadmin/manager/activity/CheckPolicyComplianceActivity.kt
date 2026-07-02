package com.localadmin.manager.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.localadmin.manager.MyApplication
import com.localadmin.manager.R
import com.localadmin.manager.ui.theme.OwnDroidTheme

class CheckPolicyComplianceActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val myApp = application as MyApplication
        setContent {
            val theme by myApp.container.themeState.collectAsState()
            OwnDroidTheme(theme) {
                AlertDialog(
                    text = {
                        Text(stringResource(R.string.info_personal_apps_suspended))
                    },
                    confirmButton = {
                        TextButton(::finish) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    onDismissRequest = {
                        finish()
                    }
                )
            }
        }
    }
}
