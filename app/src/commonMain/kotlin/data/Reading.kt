package data

import iso.SFloat

sealed class DataSample

object EmptyReading : DataSample()

class GlucoseReading(
    val unit : BloodGlucoseMeasurement,
    val amount : SFloat,
    val sequenceNumber : Int,
    val context : GlucoseReadingContext?
) : DataSample()

class GlucoseReadingContext : DataSample()
