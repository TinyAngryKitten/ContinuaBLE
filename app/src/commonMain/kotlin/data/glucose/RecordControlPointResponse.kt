package data.glucose

enum class RecordControlPointResponse {
    ReservedforFutureUse,
    Success,
    OPCodenotSupported,
    InvalidOperator,
    OperatorNotSupported,
    InvalidOperand,
    NoRecordsFound,
    AbortUnsucessful,
    ProcedureNotCompleted,
    OperandNotSupported;

    override fun toString(): String ="RecordControlPointResponse: "+this::class.simpleName

    companion object{
        fun fromInt(i : Int) = when(i) {
            1 -> Success
            2 -> OPCodenotSupported
            3 -> InvalidOperator
            4 -> OperatorNotSupported
            5 -> InvalidOperand
            6 -> NoRecordsFound
            7 -> AbortUnsucessful
            8 -> ProcedureNotCompleted
            9 -> OperandNotSupported
            else -> ReservedforFutureUse
        }
    }
}