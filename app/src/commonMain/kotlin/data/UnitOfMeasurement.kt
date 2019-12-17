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

sealed class WeightUnit : UnitOfMeasurement() {
    object KG : WeightUnit()
    object LB : WeightUnit()
}

sealed class LengthUnit : UnitOfMeasurement() {
    object M : LengthUnit()
    object Inch : LengthUnit()
}