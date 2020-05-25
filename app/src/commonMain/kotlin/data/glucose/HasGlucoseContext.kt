package data.glucose

import data.GlucoseRecordContext

sealed class HasGlucoseContext {
    object NotReceivedYet : HasGlucoseContext()
    object NotSupported : HasGlucoseContext()
    class Context(val value : GlucoseRecordContext) : HasGlucoseContext() {
        override fun toString(): String {
            return value.toString()
        }
    }
}