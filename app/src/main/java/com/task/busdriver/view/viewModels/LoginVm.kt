package com.task.busdriver.view.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.busdriver.data.repositoryImpl.RepositoryImpl
import com.task.busdriver.domain.entities.LoginParams
import com.task.busdriver.domain.entities.TripEntity
import com.task.busdriver.domain.entities.TripPointEntity
import com.task.busdriver.view.states.AuthIntent
import com.task.busdriver.view.states.AuthState
import com.task.busdriver.view.states.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class LoginVM  @Inject constructor(
    private val repositoryImpl :  RepositoryImpl ) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
      //  processIntents()
    }

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.ObserveAuth -> observeAuth()
            is AuthIntent.SignIn -> signIn(intent.email,intent.password, intent.context)
        }
    }

    private fun observeAuth() {
        viewModelScope.launch {
            repositoryImpl.authStateFlow.collect { firebaseUser ->
                _state.value = when (firebaseUser) {
                    null -> AuthState.Unauthenticated
                    else -> {
                        // Map FirebaseUser to LoginParams
                        val loginParams = LoginParams(
                            username = firebaseUser.email ?: "",
                            passwordHash = hashPassword("") // You might not have password here, so leave empty or use a stored hash if any
                        )
                        AuthState.Authenticated(loginParams)
                    }
                }
            }
        }
    }
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    private fun signIn(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _state.value = AuthState.Loading

            val result = repositoryImpl.signInWithEmail(email, password, context) // pass context here

            _state.value = when (result) {
                is Result.Success -> AuthState.Authenticated(user = result.data)
                is Result.Failure -> AuthState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }
    fun saveTripPoints(predefinedRoute: ArrayList<TripPointEntity>,tripEntity: TripEntity) {
        repositoryImpl.saveTripPoints(predefinedRoute,tripEntity)
    }
    fun register(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = repositoryImpl.registerUserWithEmail(email, password, context)
            _state.value = when (result) {
                is Result.Success -> AuthState.Authenticated(result.data)
                is Result.Failure -> AuthState.Error(result.exception.message ?: "Registration failed")
            }
        }
    }
    fun resetState() {
        _state.value = AuthState.Idle
    }
}

