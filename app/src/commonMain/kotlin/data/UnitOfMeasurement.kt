package data

sealed class UnitOfMeasurement

sealed class BloodGlucoseUnit : UnitOfMeasurement(){
    object MMOL : BloodGlucoseUnit() {
        override fun toString(): String {
            return "GlucoseUnit: MMOL"
        }
    }
    object DL : BloodGlucoseUnit() {
        override fun toString(): String {
            return "GlucoseUnit MG/DL"
        }
    }
}

sealed class BloodPressureUnit : UnitOfMeasurement() {
    object mmHg : BloodPressureUnit() {
        override fun toString(): String {
            return "BloodPressureUnit: mmHg"
        }
    }
    object kPa : BloodPressureUnit() {
        override fun toString(): String {
            return "BloodPressureUnit: kPa"
        }
    }
}

sealed class WeightUnit : UnitOfMeasurement() {
    object KG : WeightUnit() {
        override fun toString(): String {
            return "WeightUnit: KG"
        }
    }
    object LB : WeightUnit() {
        override fun toString(): String {
            return "WeightUnit: LB"
        }
    }
}

sealed class LengthUnit : UnitOfMeasurement() {
    object M : LengthUnit() {
        override fun toString(): String {
            return "LengthUnit: M"
        }
    }
    object Inch : LengthUnit() {
        override fun toString(): String {
            return "LengthUnit: Inch"
        }
    }
}