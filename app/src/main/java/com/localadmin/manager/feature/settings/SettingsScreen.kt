package com.localadmin.manager.feature.settings

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.localadmin.manager.R
import com.localadmin.manager.ui.FunctionItem
import com.localadmin.manager.ui.MyScaffold
import com.localadmin.manager.ui.Notes
import com.localadmin.manager.ui.SwitchItem
import com.localadmin.manager.ui.navigation.Destination
import com.localadmin.manager.utils.BottomPadding
import com.localadmin.manager.utils.MyNotificationChannel
import com.localadmin.manager.utils.NotificationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel, onNavigate: (Destination) -> Unit, onNavigateUp: () -> Unit
) {
    val privilege by vm.privilegeState.collectAsStateWithLifecycle()
    val exportLogsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) {
            if (it != null) vm.exportLogs(it)
        }
    var dropdown by remember { mutableStateOf(false) }
    MyScaffold(
        R.string.settings, onNavigateUp, 0.dp,
        {

            Box {
                IconButton({ dropdown = true }) {
                    Icon(Icons.Default.MoreVert, null)
                }
                DropdownMenu(dropdown, { dropdown = false }) {
                    DropdownMenuItem(
                        { Text(stringResource(R.string.export_logs)) },
                        {
                            dropdown = false
                            val time =
                                SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                                    .format(Date(System.currentTimeMillis()))
                            exportLogsLauncher.launch("owndroid_log_$time")
                        },
                        leadingIcon = {
                            Icon(painterResource(R.drawable.description_fill0), null)
                        }
                    )
                    DropdownMenuItem(
                        { Text(stringResource(R.string.exit)) },
                        { exitProcess(0) },
                        leadingIcon = { Icon(Icons.Default.Close, null) }
                    )
                }
            }
        }
    ) {
        FunctionItem(R.string.options, icon = R.drawable.tune_fill0) {
            onNavigate(Destination.SettingsOptions)
        }
        FunctionItem(R.string.appearance, icon = R.drawable.format_paint_fill0) {
            onNavigate(Destination.AppearanceSettings)
        }
        FunctionItem(R.string.app_lock, icon = R.drawable.lock_fill0) {
            onNavigate(Destination.AppLockSettings)
        }
        if (privilege.device && !privilege.dhizuku) {
            FunctionItem(R.string.notifications, icon = R.drawable.notifications_fill0) {
                onNavigate(Destination.NotificationSettings)
            }
        }
        FunctionItem(R.string.about, icon = R.drawable.info_fill0) {
            onNavigate(Destination.About)
        }
        Spacer(Modifier.height(BottomPadding))
    }
}

@Composable
fun SettingsOptionsScreen(
    vm: SettingsViewModel, onNavigateUp: () -> Unit
) {
    val dangerousFeatures by vm.dangerousFeaturesState.collectAsState()
    val shortcuts by vm.shortcutsState.collectAsState()
    MyScaffold(R.string.options, onNavigateUp, 0.dp) {
        SwitchItem(
            R.string.show_dangerous_features, dangerousFeatures, vm::setDisplayDangerousFeatures,
            R.drawable.warning_fill0
        )
        SwitchItem(
            R.string.shortcuts, shortcuts, vm::setShortcutsEnabled, R.drawable.open_in_new
        )
    }
}

@Composable
fun AppearanceScreen(
    vm: SettingsViewModel, onNavigateUp: () -> Unit
) {
    val uiState by vm.themeState.collectAsState()
    var darkThemeMenu by remember { mutableStateOf(false) }
    MyScaffold(R.string.appearance, onNavigateUp, 0.dp) {
        if (VERSION.SDK_INT >= 31) {
            SwitchItem(R.string.material_you_color, uiState.materialYou, vm::setMaterialYou)
        }
        Box {
            FunctionItem(R.string.dark_theme, stringResource(uiState.dark.text)) {
                darkThemeMenu = true
            }
            DropdownMenu(
                darkThemeMenu, { darkThemeMenu = false },
                offset = DpOffset(x = 25.dp, y = 0.dp)
            ) {
                MySettings.DarkMode.entries.forEach {
                    DropdownMenuItem(
                        { Text(stringResource(it.text)) },
                        {
                            vm.setDarkMode(it)
                            darkThemeMenu = false
                        }
                    )
                }
            }
        }
        AnimatedVisibility(
            uiState.dark == MySettings.DarkMode.On ||
                    (uiState.dark == MySettings.DarkMode.FollowSystem && isSystemInDarkTheme())
        ) {
            SwitchItem(R.string.black_theme, uiState.black, vm::setBlackTheme)
        }
    }
}

@Composable
fun AppLockSettingsScreen(
    vm: SettingsViewModel, onNavigateUp: () -> Unit
) = MyScaffold(R.string.app_lock, onNavigateUp) {
    val config = vm.getAppLockConfig()
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var allowBiometrics by rememberSaveable { mutableStateOf(config.biometrics) }
    var lockWhenLeaving by rememberSaveable { mutableStateOf(config.lockWhenLeaving) }
    var alreadySet by rememberSaveable { mutableStateOf(config.passwordHash.isNotEmpty()) }
    var securityQuestion by rememberSaveable { mutableStateOf(config.securityQuestion) }
    var securityAnswer by rememberSaveable { mutableStateOf("") }
    var confirmSecurityAnswer by rememberSaveable { mutableStateOf("") }
    val isInputLegal = password.length !in 1..3 && (alreadySet || password.isNotBlank())
    OutlinedTextField(
        password, { password = it }, Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        label = { Text(stringResource(R.string.password)) },
        supportingText = {
            Text(
                stringResource(
                    if (alreadySet) R.string.leave_empty_to_remain_unchanged
                    else R.string.minimum_length_4
                )
            )
        },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
        )
    )
    OutlinedTextField(
        confirmPassword, { confirmPassword = it }, Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.confirm_password)) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
        )
    )
    if (VERSION.SDK_INT >= 28) Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        Arrangement.SpaceBetween, Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.allow_biometrics))
        Switch(allowBiometrics, { allowBiometrics = it })
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        Arrangement.SpaceBetween, Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.lock_when_leaving))
        Switch(lockWhenLeaving, { lockWhenLeaving = it })
    }
    Button(
        {
            vm.setAppLockConfig(password, allowBiometrics, lockWhenLeaving)
            onNavigateUp()
        },
        Modifier.fillMaxWidth(),
        isInputLegal && confirmPassword == password
    ) {
        Text(stringResource(if (alreadySet) R.string.update else R.string.set))
    }
    if (alreadySet) {
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.modify_security_question))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            securityQuestion, { securityQuestion = it },
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            label = { Text(stringResource(R.string.security_question)) },
            supportingText = { Text(stringResource(R.string.security_question_hint)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            securityAnswer, { securityAnswer = it },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.security_answer)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            confirmSecurityAnswer, { confirmSecurityAnswer = it },
            Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.confirm_answer)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Button(
            {
                vm.updateSecurityQuestion(securityQuestion, securityAnswer)
                onNavigateUp()
            },
            Modifier.fillMaxWidth(),
            enabled = securityQuestion.isNotBlank() && securityAnswer.isNotBlank() &&
                    securityAnswer == confirmSecurityAnswer
        ) {
            Text(stringResource(R.string.modify_security_question))
        }
    }
}

@Composable
fun NotificationsScreen(
    vm: SettingsViewModel, onNavigateUp: () -> Unit
) = MyScaffold(R.string.notifications, onNavigateUp, 0.dp) {
    val notifications by vm.enabledNotifications.collectAsState()
    NotificationType.entries.filter {
        it.channel == MyNotificationChannel.Events
    }.forEach { type ->
        SwitchItem(type.text, type.id in notifications, { vm.setNotificationEnabled(type, it) })
    }
}

@Composable
fun AboutScreen(onNavigateUp: () -> Unit) {
    val context = LocalContext.current
    val pkgInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val verCode = pkgInfo.versionCode
    val verName = pkgInfo.versionName
    MyScaffold(R.string.about, onNavigateUp, 0.dp) {
        Text(
            stringResource(R.string.app_name) + " v$verName ($verCode)",
            Modifier.padding(start = 16.dp)
        )
        Spacer(Modifier.padding(vertical = 5.dp))
        FunctionItem(R.string.project_homepage, "GitHub", R.drawable.open_in_new) {
            shareLink(
                context, "https://github.com/BinTianqi/OwnDroid"
            )
        }
    }
}

fun shareLink(inputContext: Context, link: String) {
    val uri = link.toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri)
    inputContext.startActivity(Intent.createChooser(intent, "Open in browser"), null)
}
