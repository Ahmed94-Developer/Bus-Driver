package com.task.busdriver.view.states


sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>()
    data class Failure(val exception: Throwable): Result<Nothing>()
}