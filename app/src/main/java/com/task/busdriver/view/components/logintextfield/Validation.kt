package com.task.busdriver.view.components.logintextfield

import com.task.busdriver.R

class Validation {
    fun validateName(name: String): InputError.ErrorStatus {
        return when {
            name.trim().isEmpty() -> {
                InputError.ErrorStatus(
                    true,
                    UiText.StringResource(R.string.required)
                )
            }
            else -> {
                InputError.ErrorStatus(false)
            }
        }
    }
}