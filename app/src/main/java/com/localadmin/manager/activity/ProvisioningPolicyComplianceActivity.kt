package com.localadmin.manager.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.localadmin.manager.R
import com.localadmin.manager.utils.popToast

class ProvisioningPolicyComplianceActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_OK)
        popToast(R.string.app_name)
        finish()
    }
}
