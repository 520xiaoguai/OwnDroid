package com.localadmin.manager.ui.screen

import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.localadmin.manager.R
import com.localadmin.manager.feature.settings.MySettings
import com.localadmin.manager.utils.hash
import com.localadmin.manager.utils.showOperationResultToast

@Composable
fun AppLockDialog(
    config: MySettings.AppLock,
    onSucceed: () -> Unit,
    onDismiss: () -> Unit,
    onForgotPassword: () -> Unit,
) = Dialog(onDismiss, DialogProperties(true, false)) {
    val context = LocalContext.current
    val fm = LocalFocusManager.current
    val fr = remember { FocusRequester() }
    var input by rememberSaveable { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    fun unlock() {
        if (input.hash() == config.passwordHash) {
            fm.clearFocus()
            onSucceed()
        } else {
            isError = true
        }
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 28 && config.biometrics) {
            startBiometricsUnlock(context, onSucceed)
        } else {
            fr.requestFocus()
        }
    }
    BackHandler(onBack = onDismiss)
    Card(Modifier.pointerInput(Unit) { detectTapGestures(onTap = { fm.clearFocus() }) }, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    input, { input = it; isError = false }, Modifier.width(200.dp).focusRequester(fr),
                    label = { Text(stringResource(R.string.password)) }, isError = isError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = if(input.length >= 4) ImeAction.Go else ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions({ fm.clearFocus() }, { unlock() }),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                painter = painterResource(
                                    id = if (showPassword) R.drawable.visibility_fill0 else R.drawable.visibility_off_fill0
                                ),
                                contentDescription = if (showPassword) "Hide password" else "Show password"
                            )
                        }
                    }
                )
                if (Build.VERSION.SDK_INT >= 28 && config.biometrics) {
                    FilledTonalIconButton({ startBiometricsUnlock(context, onSucceed) }, Modifier.padding(start = 4.dp)) {
                        Icon(painterResource(R.drawable.fingerprint_fill0), null)
                    }
                }
            }
            Button(::unlock, Modifier.align(Alignment.End).padding(top = 8.dp)) {
                Text(stringResource(R.string.unlock))
            }
            if (config.securityQuestion.isNotEmpty()) {
                TextButton(
                    { onForgotPassword() },
                    Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.forgot_password))
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordDialog(
    config: MySettings.AppLock,
    onResetPassword: (String) -> Unit,
    onDismiss: () -> Unit,
) = Dialog(onDismiss, DialogProperties(true, false)) {
    val fm = LocalFocusManager.current
    // 0 = 回答安全问题, 1 = 设置新密码
    var stage by rememberSaveable { mutableStateOf(0) }
    var answer by rememberSaveable { mutableStateOf("") }
    var answerError by rememberSaveable { mutableStateOf(false) }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    BackHandler(onBack = onDismiss)
    Card(Modifier.pointerInput(Unit) { detectTapGestures(onTap = { fm.clearFocus() }) }, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            if (stage == 0) {
                Text(stringResource(R.string.answer_security_question))
                Spacer(Modifier.height(8.dp))
                Text(
                    config.securityQuestion,
                    Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    answer, { answer = it; answerError = false },
                    Modifier.width(280.dp),
                    label = { Text(stringResource(R.string.security_answer)) },
                    isError = answerError,
                    supportingText = {
                        if (answerError) Text(stringResource(R.string.security_answer_incorrect))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        fm.clearFocus()
                        if (answer.hash() == config.securityAnswerHash) stage = 1 else answerError = true
                    }),
                    singleLine = true
                )
                Button(
                    {
                        fm.clearFocus()
                        if (answer.hash() == config.securityAnswerHash) {
                            stage = 1
                        } else {
                            answerError = true
                        }
                    },
                    Modifier.align(Alignment.End).padding(top = 8.dp),
                    enabled = answer.isNotBlank()
                ) {
                    Text(stringResource(R.string.next))
                }
            } else {
                Text(stringResource(R.string.reset_via_security_question))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    newPassword, { newPassword = it; passwordError = false },
                    Modifier.width(280.dp),
                    label = { Text(stringResource(R.string.password)) },
                    supportingText = { Text(stringResource(R.string.minimum_length_4)) },
                    isError = passwordError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    confirmPassword, { confirmPassword = it; passwordError = false },
                    Modifier.width(280.dp).padding(top = 4.dp),
                    label = { Text(stringResource(R.string.confirm_password)) },
                    isError = passwordError,
                    supportingText = {
                        if (passwordError) Text(stringResource(R.string.passwords_not_match))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        fm.clearFocus()
                        if (newPassword.length >= 4 && newPassword == confirmPassword) onResetPassword(newPassword) else passwordError = true
                    }),
                    singleLine = true
                )
                Button(
                    {
                        fm.clearFocus()
                        if (newPassword.length >= 4 && newPassword == confirmPassword) {
                            onResetPassword(newPassword)
                        } else {
                            passwordError = true
                        }
                    },
                    Modifier.align(Alignment.End).padding(top = 8.dp),
                    enabled = newPassword.length >= 4 && confirmPassword.isNotEmpty()
                ) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }
    }
}

@RequiresApi(28)
fun startBiometricsUnlock(context: Context, onSucceed: () -> Unit) {
    context.getSystemService(FingerprintManager::class.java) ?: return
    val callback = object : AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            onSucceed()
        }
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            if(errorCode != BiometricPrompt.BIOMETRIC_ERROR_CANCELED) context.showOperationResultToast(false)
        }
    }
    val cancel = CancellationSignal()
    BiometricPrompt.Builder(context)
        .setTitle(context.getText(R.string.unlock))
        .setNegativeButton(context.getString(R.string.cancel), context.mainExecutor) { _, _ -> cancel.cancel() }
        .build()
        .authenticate(cancel, context.mainExecutor, callback)
}
