package data.glucose

sealed class GlucoseSampleLocation {
    object ReservedForFutureUse : GlucoseSampleLocation()
    object Finger : GlucoseSampleLocation()
    object AlternateTestSite: GlucoseSampleLocation()
    object Earlobe : GlucoseSampleLocation()
    object ControlSolution: GlucoseSampleLocation()
    object SampleLocationNotAvailable : GlucoseSampleLocation()

    override fun toString(): String = this::class.simpleName ?: ""

    companion object {
        fun fromInt(i : Int) = when(i) {
            1 -> Finger
            2 -> AlternateTestSite
            3 -> Earlobe
            4 -> ControlSolution
            15 -> SampleLocationNotAvailable
            else -> ReservedForFutureUse
        }
    }
}