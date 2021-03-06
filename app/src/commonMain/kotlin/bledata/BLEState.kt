package bledata

sealed class BLEState {
    object Off : BLEState()
    object On : BLEState()
    object NotSupported : BLEState()
    object NotAuthorized : BLEState()
    object Resetting : BLEState()
    object UnknownErrorState : BLEState()

    override fun toString(): String = this::class.simpleName ?: "UnknownBLEState"
}