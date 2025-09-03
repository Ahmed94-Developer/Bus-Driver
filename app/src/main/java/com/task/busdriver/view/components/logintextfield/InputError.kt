package com.task.busdriver.view.components.logintextfield


class InputError {
    data class FieldInput(
        val value: String = "",
        val hasInteracted: Boolean = false,
    )

    data class ErrorStatus(
        val isError: Boolean,
        val errorMsg: UiText? = null,
    )
}