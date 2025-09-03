package com.task.busdriver.view.states

import com.google.firebase.auth.FirebaseUser
import com.task.busdriver.domain.entities.LoginParams

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: LoginParams) : AuthState()
    data class Error(val message: String) : AuthState()
}