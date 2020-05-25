package gatt.services

import data.HeightMeasurementResolution
import data.WeightMeasurementResolution
import gatt.ISOParser

//methods for parsing weight and height resolution, defined as extensions on ISOParser

val weightResolution : ISOParser.(startIndex : Int) -> WeightMeasurementResolution = {
        startIndex->
    WeightMeasurementResolution.fromInt(
        flag(startIndex) * 1
                + flag(startIndex+1) * 2
                + flag(startIndex+2) * 4
                + flag(startIndex+3) * 8
    )
}

val heightResolution : ISOParser.(startIndex : Int) -> HeightMeasurementResolution = {
        startIndex ->
    HeightMeasurementResolution.fromInt(
        flag(startIndex) * 1
                + flag(startIndex+1) * 2
                + flag(startIndex+2) * 2
    )
}
private operator fun Boolean.times(i : Int) = if(this) i else 0