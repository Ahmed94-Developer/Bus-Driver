package com.task.busdriver.view.components.logintextfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.myapplication3.view.styles.TextStyles
import kotlinx.coroutines.delay


@Composable
fun OutLineLoginTextFieldWithState(
    modifier: Modifier = Modifier,
    label: String,
    fieldInput: InputError.FieldInput,
    errorStatus: InputError.ErrorStatus,
    keyboardOptions: KeyboardOptions,
    isPasswordField: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,
) {

    var passwordVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current



    val shouldFocus = false // ← Change this

    LaunchedEffect(shouldFocus) {
        if (shouldFocus) {
            delay(400) // Give time for composition
            focusManager.clearFocus()
        }
    }

    fun passwordToggle() {
        passwordVisible = !passwordVisible
    }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth().onFocusChanged { focusState ->
            if (!focusState.isFocused) {
                focusManager.clearFocus()
            }
        },
        value = fieldInput.value,


        onValueChange = {
            onValueChange(it)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
        ),
        label = {
          Text(text = label, style = TextStyles().textStyleNormal12
              .copy(color = Color.Gray),)
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,


        isError = fieldInput.hasInteracted && errorStatus.isError,
        supportingText = {
            if (fieldInput.hasInteracted && errorStatus.isError) {
                errorStatus.errorMsg?.let {
                    Text(
                        text = it.asString(), modifier = Modifier.fillMaxWidth(),
                        style = TextStyles().textStyleNormal12
                            .copy(color = Color.Red)
                    )
                }
            }
        },
        visualTransformation = if (isPasswordField && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        trailingIcon = {
            if (isPasswordField) {
                val icon = if (passwordVisible) Icons.Outlined.Lock else Icons.Outlined.Info
                IconButton(onClick = { passwordToggle() }) {
                    Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                }
            }
        }
    )
}