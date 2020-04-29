package iso.services

import bledata.BLEReading
import data.*
import iso.parse
import util.positiveBitAt

fun parsePulseOximeterFeatures(reading: BLEReading) =
    parse(reading) {
        val supportedFeatures = take(2)
        val measurementStatusSupport = take(2)
        val deviceAndSensorStatusSupport = take(3)

        PulseOximeterFeatures(

            PulseOximeterSupportedFeatures(
                measurementStatus = supportedFeatures.positiveBitAt(0),
                deviceAndSensorStatus = supportedFeatures.positiveBitAt(1),
                measurementStoreForSpotCheck = supportedFeatures.positiveBitAt(2),
                timestampForSpotCheck = supportedFeatures.positiveBitAt(3),
                spo2PRFast = supportedFeatures.positiveBitAt(4),
                spo2PRSlow = supportedFeatures.positiveBitAt(5),
                pulseAmplitudeIndexField = supportedFeatures.positiveBitAt(6),
                multipleBonds = supportedFeatures.positiveBitAt(7)
            ),

            requirement {
                condition = supportedFeatures.positiveBitAt(0)
                format = {
                    PulseOximeterMeasurementStatusSupport(
                        measurementOngoing = measurementStatusSupport.positiveBitAt(5),
                        earlyEstimatedData = measurementStatusSupport.positiveBitAt(6),
                        validatedData = measurementStatusSupport.positiveBitAt(7),
                        fullyQualifiedData = measurementStatusSupport.positiveBitAt(8),
                        dataFromMeasurementStorage = measurementStatusSupport.positiveBitAt(9),
                        dataForDemonstration = measurementStatusSupport.positiveBitAt(10),
                        dataForTesting = measurementStatusSupport.positiveBitAt(11),
                        calibrationOngoing = measurementStatusSupport.positiveBitAt(12),
                        measurementUnavailable = measurementStatusSupport.positiveBitAt(13),
                        questionableMeasurementDetected = measurementStatusSupport.positiveBitAt(14),
                        invalidMeasurementDetected = measurementStatusSupport.positiveBitAt(15)
                    )
                }
            },

            requirement {
                condition = supportedFeatures.positiveBitAt(1)
                format = {
                    PulseOximeterDeviceAndSensorStatusSupport(
                        extendedDisplayUpdateOngoing = deviceAndSensorStatusSupport.positiveBitAt(0),
                        equipmentMalfunctionDetected = deviceAndSensorStatusSupport.positiveBitAt(1),
                        signalProcessingIrregularityDetected = deviceAndSensorStatusSupport.positiveBitAt(2),
                        inadequateSignalDetected = deviceAndSensorStatusSupport.positiveBitAt(3),
                        poorSignalDetected = deviceAndSensorStatusSupport.positiveBitAt(4),
                        lowPerfusionDetected = deviceAndSensorStatusSupport.positiveBitAt(5),
                        erraticSignalDetected = deviceAndSensorStatusSupport.positiveBitAt(6),
                        nonPulseatileSignalDetected = deviceAndSensorStatusSupport.positiveBitAt(7),
                        questionablePulseDetected = deviceAndSensorStatusSupport.positiveBitAt(8),
                        signalAnalysisOngoing = deviceAndSensorStatusSupport.positiveBitAt(9),
                        sensorInterfaceDetected = deviceAndSensorStatusSupport.positiveBitAt(10),
                        sensorUnconnectedToUser = deviceAndSensorStatusSupport.positiveBitAt(11),
                        unknownSensorConnected = deviceAndSensorStatusSupport.positiveBitAt(12),
                        sensorDisplaced = deviceAndSensorStatusSupport.positiveBitAt(13),
                        sensorMalfunction = deviceAndSensorStatusSupport.positiveBitAt(14),
                        sensorDisconnected = deviceAndSensorStatusSupport.positiveBitAt(15)
                    )
                }
            },

            reading.device
        )
    }

fun parsePlxSpotCheck(reading:  BLEReading) =
    parse(reading) {
        flags(0..0)
        PLXSpotCheck.fromISO(
            spo2 = sfloat(),
            PR = sfloat(),
            timeStamp = onCondition(flag(0),dateTime),
            measurementStatus = onCondition(flag(1),sint16),
            sensorstatus1 = onCondition(flag(2),sint16),
            sensorstatus2 = onCondition(flag(2),sint8),
            pulseAmplitudeIndex = onCondition(flag(3),sfloat),
            device = reading.device

        )?: EmptyRecord(reading.device)
    }

fun parseContinousPlxMeasurement(reading:  BLEReading) =
    parse(reading) {
        flags(0..0)
        PLXContinousMeasurement.fromISO(
            spo2Normal= sfloat(),
            PRNormal= sfloat(),
            spo2Fast = onCondition(flag(0), sfloat),
            PRFast = onCondition(flag(0), sfloat),
            spo2Slow = onCondition(flag(1), sfloat),
            PRSlow = onCondition(flag(1), sfloat),
            measurementStatus = onCondition(flag(2),sint16),
            sensorstatus1 = onCondition(flag(3),sint16),
            sensorstatus2 = onCondition(flag(3),sint8),
            pulseAmplitudeIndex = onCondition(flag(4),sfloat),
            device = reading.device
        )?: EmptyRecord(reading.device)
    }
