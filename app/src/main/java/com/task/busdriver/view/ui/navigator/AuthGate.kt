package com.task.busdriver.view.ui.navigator

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.task.busdriver.domain.entities.LoginParams
import com.task.busdriver.view.states.AuthIntent
import com.task.busdriver.view.states.AuthState
import com.task.busdriver.view.viewModels.LoginVM

@Composable
fun AuthGate(
    viewModel: LoginVM,
    onAuthenticated: (LoginParams) -> Unit,
    onUnauthenticated: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(AuthIntent.ObserveAuth)
    }

    when (state) {
        is AuthState.Loading -> {
            // Simple loading screen while checking auth state
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material.CircularProgressIndicator()
            }
        }

        is AuthState.Authenticated -> {
            val user = (state as AuthState.Authenticated).user
            LaunchedEffect(user) {
                onAuthenticated(user)
            }
        }

        is AuthState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                onUnauthenticated()
            }
        }

        is AuthState.Error -> {
            // Handle errors optionally
            val message = (state as AuthState.Error).message
            Log.e("AuthGate", "Error: $message")
            onUnauthenticated()
        }

        AuthState.Idle -> TODO()
    }
}