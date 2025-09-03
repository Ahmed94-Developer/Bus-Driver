package com.task.busdriver.view.states

import android.content.Context

sealed class AuthIntent {
    object ObserveAuth : AuthIntent()
    data class SignIn(val email: String, val password: String, val context: Context) : AuthIntent()
}