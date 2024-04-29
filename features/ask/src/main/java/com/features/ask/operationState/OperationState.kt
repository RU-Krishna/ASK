package com.features.ask.operationState

sealed interface OperationState {

    data object Idle: OperationState

    data object Performing: OperationState

    data object Done: OperationState

    data class Error(val error: String) : OperationState
}