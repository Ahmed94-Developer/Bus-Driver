package com.task.busdriver.view.ui.activities.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.task.busdriver.view.states.AuthIntent
import com.task.busdriver.view.ui.activities.home.HomeActivity
import com.task.busdriver.view.viewModels.LoginVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            val viewModel: LoginVM = hiltViewModel()
            LaunchedEffect(Unit) {
                viewModel.handleIntent(AuthIntent.ObserveAuth)
            }
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    // e.g., start HomeActivity or finish()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            )
        }
    }

}