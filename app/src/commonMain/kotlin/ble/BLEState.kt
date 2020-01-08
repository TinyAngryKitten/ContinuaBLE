package ble

sealed class BLEState {
    object Off : BLEState()
    object On : BLEState()
    object NotSupported : BLEState()
    object NotAuthorized : BLEState()
    object Resetting : BLEState()
    object UnknownErrorState : BLEState()
}