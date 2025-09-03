
package com.task.busdriver.view.viewModels
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.task.busdriver.view.components.logintextfield.InputError
import com.task.busdriver.view.components.logintextfield.Validation


class InputVM : ViewModel() {
    var phoneField by mutableStateOf(InputError.FieldInput())
    var passwordField by mutableStateOf(InputError.FieldInput())

    val phoneErrorStatus by derivedStateOf {
       Validation().validateName(phoneField.value)
    }
    val passwordErrorStatus by derivedStateOf {
        Validation().validateName(passwordField.value)
    }

}