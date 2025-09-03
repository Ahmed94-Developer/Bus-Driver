package com.task.busdriver.view.ui.navigator

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.task.busdriver.view.ui.activities.home.HomeScreen
import com.task.busdriver.view.ui.activities.auth.login.LoginScreen
import com.task.busdriver.view.ui.activities.splash.SplashScreen
import com.task.busdriver.view.viewModels.LoginVM

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen()
        }

        composable("auth_gate") {
            val viewModel: LoginVM = hiltViewModel()
            AuthGate(
                viewModel = viewModel,
                onAuthenticated = {
                    navController.navigate("home") {
                        popUpTo("auth_gate") { inclusive = true }
                    }
                },
                onUnauthenticated = {
                    navController.navigate("login") {
                        popUpTo("auth_gate") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            val viewModel: LoginVM = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}
