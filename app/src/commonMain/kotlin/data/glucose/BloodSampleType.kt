package data.glucose

sealed class BloodSampleType {
    object ReservedForFutureUse : BloodSampleType()
    object CapillaryWholeBlood : BloodSampleType()
    object CapillaryPlasma : BloodSampleType()
    object VenousWholeBlood : BloodSampleType()
    object VenousPlasma : BloodSampleType()
    object ArterialWholeBlood : BloodSampleType()
    object ArterialPlasma : BloodSampleType()
    object UndeterminedWholeBlood : BloodSampleType()
    object UndeterminedPlasma: BloodSampleType()
    object InterstitialFluid : BloodSampleType()
    object ControlSolution : BloodSampleType()

    override fun toString(): String = this::class.simpleName ?: ""

    companion object {
        fun fromInt(i : Int) = when(i) {
            1 -> CapillaryWholeBlood
            2 -> CapillaryPlasma
            3-> VenousWholeBlood
            4 -> VenousPlasma
            5 -> ArterialWholeBlood
            6 -> ArterialPlasma
            7 -> UndeterminedWholeBlood
            8 -> UndeterminedPlasma
            9 -> InterstitialFluid
            10 -> ControlSolution
            else -> ReservedForFutureUse
        }
    }
}