package data

sealed class UnitOfMeasurement

sealed class BloodGlucoseUnit : UnitOfMeasurement(){
    object MMOL : BloodGlucoseUnit()
    object DL : BloodGlucoseUnit()
}

sealed class BloodPressureUnit : UnitOfMeasurement() {
    object mmHg : BloodPressureUnit()
    object kPa : BloodPressureUnit()
}