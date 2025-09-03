package com.task.busdriver.view.ui.activities.auth.register

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication3.view.styles.TextStyles
import com.google.firebase.database.core.Repo
import com.task.busdriver.R
import com.task.busdriver.data.repositoryImpl.RepositoryImpl
import com.task.busdriver.view.components.logintextfield.OutLineLoginTextFieldWithState
import com.task.busdriver.view.states.AuthState
import com.task.busdriver.view.ui.activities.home.HomeActivity
import com.task.busdriver.view.viewModels.InputVM
import com.task.busdriver.view.viewModels.LoginVM
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
fun RegisterScreen() {
    val colorGreen = Color(0xFF8FC9F8)
    val colorWhite = Color.White
    var checked: Boolean = false
    val gradient = Brush.linearGradient(0f to colorGreen, 1000f to colorWhite)
    val inputVm: InputVM = viewModel()
    val loginVM: LoginVM = hiltViewModel()
    val state by loginVM.state.collectAsStateWithLifecycle()
    val context = LocalContext.current


    val isLoading = state is AuthState.Loading
    val isError = state is AuthState.Error
    val isSuccess = state is AuthState.Authenticated
    val isIdle = state is AuthState.Idle

    val isFormValid = inputVm.phoneField.value.isNotBlank() &&
            inputVm.passwordField.value.length >= 6

    val isButtonEnabled = !isLoading && isFormValid

    LaunchedEffect(state) {
        if (state is AuthState.Authenticated || state is AuthState.Error) {
            delay(2000)
            loginVM.resetState()
        }
    }


    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "login"
            )
            Text(
                text = "Register", fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 16.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                fontFamily = FontFamily(Font(R.font.poppin))
            )

            Text(
                text = "Email",
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp)
                    .padding(horizontal = 5.dp),
                fontFamily = FontFamily(Font(R.font.newfont)),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = Color.Black, textAlign = TextAlign.Start
            )

            OutLineLoginTextFieldWithState(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.enter_the_phone),
                fieldInput = inputVm.phoneField,
                errorStatus = inputVm.phoneErrorStatus,
                isPasswordField = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
            ) {
                inputVm.phoneField = inputVm.phoneField.copy(
                    value = it,
                    hasInteracted = true
                )
            }



            Text(
                text = "Password",
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp)
                    .padding(horizontal = 5.dp),
                fontFamily = FontFamily(Font(R.font.newfont)),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = Color.Black, textAlign = TextAlign.Start
            )

            OutLineLoginTextFieldWithState(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.enter_the_password),
                fieldInput = inputVm.passwordField,
                isPasswordField = true,
                errorStatus = inputVm.passwordErrorStatus,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
            ) {
                inputVm.passwordField = inputVm.passwordField.copy(
                    value = it,
                    hasInteracted = true
                )
            }

            Button(
                shape = RoundedCornerShape(size = 8.dp),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 16.dp),
                colors =
                    ButtonColors(
                        contentColor = Color.White,
                        containerColor = Color(0xff489BFC),
                        disabledContainerColor = Color(0xff489BFC),
                        disabledContentColor = Color(0xff489BFC),
                    ),
                onClick = {
                    loginVM.register(
                        email = inputVm.phoneField.value,
                        password = inputVm.passwordField.value,
                        context = context
                    )
                },
            ) {

                    when (state) {
                        is AuthState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }

                        is AuthState.Error -> {
                            val errorMsg = (state as AuthState.Error).message
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Rounded.Close,
                                tint = Color.Red,
                                contentDescription = "Toggle password visibility"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMsg, style = TextStyles().textStyleNormal12)
                        }
                        is AuthState.Idle -> {
                        Text("Register", style = TextStyles().textStyleNormal15)
                        }

                        is AuthState.Authenticated -> {
                            Toast.makeText(context, "User created successfully", Toast.LENGTH_SHORT).show()
                            val user = (state as AuthState.Authenticated).user
                            // Navigate or trigger success callback
                           context.startActivity(Intent(context, HomeActivity::class.java))
                        }

                        else -> Unit // do nothing on unauthenticated here


                    }
                }



        }

    }

}