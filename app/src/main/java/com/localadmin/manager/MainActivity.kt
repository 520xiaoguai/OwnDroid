package com.localadmin.manager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.localadmin.manager.feature.applications.AppChooserViewModel
import com.localadmin.manager.ui.NavTransition
import com.localadmin.manager.ui.navigation.Destination
import com.localadmin.manager.ui.navigation.myEntryProvider
import com.localadmin.manager.ui.navigation.rememberSharedViewModelStoreNavEntryDecorator
import com.localadmin.manager.ui.screen.AppLockDialog
import com.localadmin.manager.ui.screen.ForgotPasswordDialog
import com.localadmin.manager.ui.theme.OwnDroidTheme
import com.localadmin.manager.utils.DhizukuError
import com.localadmin.manager.utils.hash
import com.localadmin.manager.utils.popToast
import com.localadmin.manager.utils.registerPackageRemovedReceiver
import com.localadmin.manager.utils.viewModelFactory
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val context = this
        val myApp = (application as MyApplication)
        val settingsRepo = myApp.container.settingsRepo
        if (
            VERSION.SDK_INT >= 33 &&
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        val appChooserVm: AppChooserViewModel by viewModels(
            factoryProducer = {
                viewModelFactory { AppChooserViewModel(myApp) }
            }
        )
        registerPackageRemovedReceiver(this) {
            appChooserVm.onPackageRemoved(it)
        }
        if (
            myApp.container.privilegeState.value.work &&
            !settingsRepo.data.privilege.managedProfileActivated
        ) {
            myApp.container.privilegeHelper.dpm.setProfileEnabled(
                myApp.container.privilegeHelper.dar
            )
            settingsRepo.update {
                it.privilege.managedProfileActivated = true
            }
            context.popToast(R.string.work_profile_activated)
        }
        lifecycleScope.launch {
            while (true) {
                val text = myApp.container.toastChannel.channel.receive()
                context.popToast(text)
            }
        }
        setContent {
            val dhizukuError by myApp.container.dhizukuErrorState.collectAsState()
            var appLockDialog by rememberSaveable { mutableStateOf(false) }
            var showForgotPassword by rememberSaveable { mutableStateOf(false) }
            val theme by myApp.container.themeState.collectAsState()
            OwnDroidTheme(theme) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
                val backstack = rememberNavBackStack(Destination.Home)
                LaunchedEffect(Unit) {
                    if (settingsRepo.data.appLock.passwordHash.isEmpty()) {
                        backstack.add(Destination.SetupPassword)
                        backstack.removeFirstOrNull()
                    } else if (!myApp.container.privilegeState.value.activated) {
                        backstack.add(Destination.WorkingModes(false))
                        backstack.removeFirstOrNull()
                    }
                }
                NavDisplay(
                    backstack,
                    onBack = {
                        backstack.removeLastOrNull()
                    },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberSharedViewModelStoreNavEntryDecorator()
                    ),
                    transitionSpec = {
                        NavTransition.transition
                    },
                    popTransitionSpec = {
                        NavTransition.popTransition
                    },
                    predictivePopTransitionSpec = {
                        NavTransition.popTransition
                    }
                ) {
                    myEntryProvider(it as Destination, backstack, appChooserVm, myApp.container)
                }
                val lifecycleOwner = LocalLifecycleOwner.current
                if (appLockDialog) {
                    AppLockDialog(
                        config = myApp.container.settingsRepo.data.appLock,
                        onSucceed = { appLockDialog = false },
                        onDismiss = { moveTaskToBack(true) },
                        onForgotPassword = {
                            appLockDialog = false
                            showForgotPassword = true
                        }
                    )
                }
                if (showForgotPassword) {
                    ForgotPasswordDialog(
                        config = myApp.container.settingsRepo.data.appLock,
                        onResetPassword = { newPassword ->
                            settingsRepo.update {
                                it.appLock.passwordHash = newPassword.hash()
                            }
                            showForgotPassword = false
                        },
                        onDismiss = { showForgotPassword = false }
                    )
                }
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (
                            settingsRepo.data.appLock.passwordHash.isNotEmpty() &&
                            (event == Lifecycle.Event.ON_CREATE ||
                                    (event == Lifecycle.Event.ON_RESUME &&
                                            settingsRepo.data.appLock.lockWhenLeaving))
                        ) {
                            appLockDialog = true
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                if (dhizukuError != null) {
                    DhizukuErrorDialog(
                        dhizukuError!!, {
                            myApp.container.dhizukuErrorState.value = null
                        }, {
                            myApp.container.dhizukuErrorState.value = null
                            settingsRepo.update { it.privilege.dhizuku = false }
                            backstack += Destination.WorkingModes(false)
                            repeat(backstack.size - 1) {
                                backstack.removeFirstOrNull()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DhizukuErrorDialog(error: DhizukuError, onClose: () -> Unit, onDisable: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(onDisable) {
                Text(stringResource(R.string.disable))
            }
        },
        dismissButton = {
            TextButton(onClose) {
                Text(stringResource(R.string.confirm))
            }
        },
        title = { Text(stringResource(R.string.error)) },
        text = {
            val text = stringResource(
                when (error) {
                    DhizukuError.Init -> R.string.failed_to_init_dhizuku
                    DhizukuError.Permission -> R.string.dhizuku_permission_not_granted
                    else -> R.string.failed_to_init_dhizuku
                }
            )
            Text(text)
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}
