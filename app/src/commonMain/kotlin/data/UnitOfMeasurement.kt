package data

sealed class UnitOfMeasurement

sealed class BloodGlucoseMeasurement {
    object MMOL : BloodGlucoseMeasurement()
    object DL : BloodGlucoseMeasurement()
}