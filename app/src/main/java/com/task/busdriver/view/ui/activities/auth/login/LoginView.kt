package com.task.busdriver.view.ui.activities.auth.login
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication3.view.styles.TextStyles
import com.task.busdriver.R
import com.task.busdriver.domain.entities.LoginParams
import com.task.busdriver.view.components.logintextfield.OutLineLoginTextFieldWithState
import com.task.busdriver.view.states.AuthIntent
import com.task.busdriver.view.states.AuthState
import com.task.busdriver.view.ui.activities.auth.register.RegisterActivity
import com.task.busdriver.view.ui.activities.home.HomeActivity
import com.task.busdriver.view.viewModels.InputVM
import com.task.busdriver.view.viewModels.LoginVM


@SuppressLint("SuspiciousIndentation")
@Composable
fun LoginScreen(viewModel: LoginVM,
                 onLoginSuccess: (LoginParams) -> Unit){
    val colorGreen = Color(0xFF8FC9F8)
    val colorWhite = Color.White
    var checked : Boolean = false
    val gradient = Brush.linearGradient(0f to colorGreen, 1000f to colorWhite)

        val inputVm : InputVM = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current



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
                    text = "Login", fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 16.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    fontFamily = FontFamily(Font(R.font.poppin))
                )

             /*   Text(
                    text = "الرجاء إدخال بياناتك لتسجيل الدخول.",
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(vertical = 30.dp)
                        .padding(horizontal = 5.dp),
                    fontFamily = FontFamily(Font(R.font.newfont)),
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    color = Color.Gray, textAlign = TextAlign.Center
                )*/
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

                   OutLineLoginTextFieldWithState (
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

                OutLineLoginTextFieldWithState (
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
                        val loginParams: LoginParams =
                            LoginParams(username = inputVm.phoneField.value
                                , passwordHash = inputVm.passwordField.value)
                       viewModel.handleIntent(AuthIntent.SignIn(loginParams.username,
                           loginParams.passwordHash, context))
                    },
                ) {
                    Text("Login")
                }

                when (state) {
                    is AuthState.Idle->{
                        Box(){}
                    }
                    is AuthState.Loading -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }

                    is AuthState.Error -> {
                        val errorMsg = (state as AuthState.Error).message
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = errorMsg, color = Color.Red)
                    }

                    is AuthState.Authenticated -> {
                        val user = (state as AuthState.Authenticated).user
                        // Navigate or trigger success callback
                        LaunchedEffect(user) {
                            onLoginSuccess(user)
                        }
                    }

                    else -> Unit // do nothing on unauthenticated here
                }
                Box(modifier = Modifier.height(7.dp))
                TextButton(onClick = {
                    context.startActivity(Intent(context, RegisterActivity::class.java))
                }) {
                    Text(text = "Register", style = TextStyles()
                        .textStyleBold13.copy(color = Color.Black))
                }


            }

        }

}