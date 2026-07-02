package com.localadmin.manager.feature.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.localadmin.manager.R
import com.localadmin.manager.ui.Notes
import com.localadmin.manager.utils.BottomPadding
import com.localadmin.manager.utils.adaptiveInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPasswordScreen(
    vm: SettingsViewModel, onComplete: () -> Unit
) {
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var question by rememberSaveable { mutableStateOf("") }
    var answer by rememberSaveable { mutableStateOf("") }
    var confirmAnswer by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    var answerError by rememberSaveable { mutableStateOf(false) }
    var questionError by rememberSaveable { mutableStateOf(false) }

    val passwordValid = password.length >= 4 && password == confirmPassword
    val questionValid = question.isNotBlank()
    val answerValid = answer.isNotBlank() && answer == confirmAnswer
    val canSubmit = passwordValid && questionValid && answerValid

    val sb = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        Modifier
            .fillMaxSize()
            .nestedScroll(sb.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.setup_password)) },
                scrollBehavior = sb
            )
        },
        contentWindowInsets = adaptiveInsets()
    ) { padding ->
        androidx.compose.foundation.layout.Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Notes(R.string.setup_password_intro)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                password, {
                    password = it; passwordError = false
                },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.password)) },
                supportingText = { Text(stringResource(R.string.minimum_length_4)) },
                isError = passwordError,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    androidx.compose.material3.IconButton(onClick = { showPassword = !showPassword }) {
                        androidx.compose.material3.Icon(
                            androidx.compose.ui.res.painterResource(
                                id = if (showPassword) R.drawable.visibility_fill0 else R.drawable.visibility_off_fill0
                            ),
                            contentDescription = null
                        )
                    }
                }
            )
            OutlinedTextField(
                confirmPassword, { confirmPassword = it; passwordError = false },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.confirm_password)) },
                isError = passwordError,
                supportingText = {
                    if (passwordError) Text(stringResource(R.string.passwords_not_match))
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                question, { question = it; questionError = false },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.security_question)) },
                supportingText = { Text(stringResource(R.string.security_question_hint)) },
                isError = questionError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                answer, { answer = it; answerError = false },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.security_answer)) },
                isError = answerError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                confirmAnswer, { confirmAnswer = it; answerError = false },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.confirm_answer)) },
                isError = answerError,
                supportingText = {
                    if (answerError) Text(stringResource(R.string.answers_not_match))
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                {
                    if (!passwordValid) passwordError = true
                    if (!questionValid) questionError = true
                    if (!answerValid) answerError = true
                    if (canSubmit) {
                        vm.setInitialPassword(password, question, answer)
                        onComplete()
                    }
                },
                Modifier.fillMaxWidth(),
                enabled = password.length >= 4 && confirmPassword.isNotEmpty() &&
                        question.isNotEmpty() && answer.isNotEmpty() && confirmAnswer.isNotEmpty()
            ) {
                Text(stringResource(R.string.confirm))
            }
            Spacer(Modifier.height(BottomPadding))
        }
    }
}
