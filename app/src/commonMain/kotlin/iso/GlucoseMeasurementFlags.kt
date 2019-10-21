package iso

import iso.MessagePosition.*

val timeOffsetFlagPosition: FlagPosition  = FlagPosition(0, 0)
val concentrationTypeAndSampleFlagPosition: FlagPosition =  FlagPosition(0, 1)
val concentrationUnitPosition: FlagPosition =  FlagPosition(0, 2)
val sensorStatusAnnunciationFlag: FlagPosition = FlagPosition(0, 3)
val contextInformationFollowsFlag: FlagPosition = FlagPosition(0, 4)

data class GlucoseMeasurementFlags(
    val timeOffset : Boolean,
    val concentrationTypeAndSample : Boolean,
    val concentrationUnit : Boolean,
    val sensorstatusAnnunciation : Boolean,
    val contextInformationFollows : Boolean

)