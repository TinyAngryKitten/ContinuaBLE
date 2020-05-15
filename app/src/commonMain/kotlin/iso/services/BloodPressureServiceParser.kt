package iso.services

import bledata.BLEReading
import data.*
import iso.parse
import util.positiveBitAt


fun parseBloodPressureFeature(reading : BLEReading) =
    parse(reading) {
        flags(0..1)

        BloodPressureFeatures(
            flag(0),
            flag(1),
            flag(2),
            flag(3),
            flag(4),
            flag(5),
            reading.device
        )
    }

fun parseBloodPressureMeasurement(reading : BLEReading) =
    parse(reading) {
        flags(0..0)

        BloodPressureRecord.finalFromISO(
            systolic = sfloat(),
            diastolic = sfloat(),
            meanArtieralPressure = sfloat(),
            timeStamp = onCondition( flag(1), dateTime),
            unit = if(flag(0)) BloodPressureUnit.kPa else BloodPressureUnit.mmHg,
            bpm = requirement {
                flag = 2
                format = sfloat
            },
            userId = requirement {
                flag = 3
                format = uint8
            },
            status = requirement<MeasurementStatus> {
                flag=4
                format = {
                    val flags = sint16().byte1//only the first bit contains any value (guidelines 2019)
                    MeasurementStatus(
                        flags.positiveBitAt(0),
                        flags.positiveBitAt(1),
                        flags.positiveBitAt(2),
                        flags.positiveBitAt(3),//01
                        flags.positiveBitAt(4),//10
                        flags.positiveBitAt(5)
                    )
                }
            },
            device = reading.device
        ) ?: EmptyRecord(reading.device)
    }

fun intermediateCuffPressureParser(reading : BLEReading) =
    parse(reading) {
        BloodPressureRecord.intermediateFromISO(
            systolic = sfloat(),
            timeStamp = requirement {
                flag =1
                format = dateTime
            },
            unit = if(flag(0)) BloodPressureUnit.kPa else BloodPressureUnit.mmHg,
            bpm = requirement {
                flag = 2
               format = sfloat
            },
            userId = requirement{
                flag = 3
                format= uint8
            },
            status = null,//onCondition(flag(4), ISOValue.Flags())
            device = reading.device
            ) ?: EmptyRecord(reading.device)
        }