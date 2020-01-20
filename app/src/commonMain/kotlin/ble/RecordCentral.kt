package ble

import co.touchlab.stately.collections.frozenCopyOnWriteList
import co.touchlab.stately.collections.frozenHashMap
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.freeze
import data.*
import sample.logger
import kotlin.native.concurrent.ThreadLocal

class RecordCentral(val onCompleteRecord : (DataRecord) -> Unit) {

    val glucoseRecords = frozenHashMap<String,GlucoseRecord>()
    val deviceInfoRecords = frozenHashMap<String,DeviceInfoBuilder>()

    val bodyCompositionFeatureMap = frozenHashMap<String,BodyCompositionFeature>()
    val weightFeateatureMap = frozenHashMap<String,WeightFeatures>()

    fun addRecord(record : DataRecord) =
        when(record) {
            is EmptyRecord -> logger.debug("empty record added to record central")

            is GlucoseRecord -> when(record.context) {
                is HasGlucoseContext.Context -> glucoseRecords[record.device.UUID] = record
                else -> completeRecordCallback(record)
            }
            is GlucoseRecordContext -> {
                if(glucoseRecords.containsKey(record.device.UUID)) {

                    completeRecordCallback(
                        glucoseRecords[record.device.UUID]?.copyWithContect(
                            HasGlucoseContext.Context(
                                record
                            )
                        )
                    )

                } else Unit
            }

            is DeviceInfoComponent -> addDeviceInfoComponent(record)

            is BodyCompositionFeature -> bodyCompositionFeatureMap[record.device.UUID ] = record
            is BodyCompositionRecord -> {//the resolution of the measurement with what the features say
                val newHeightResolution = bodyCompositionFeatureMap[record.device.UUID]?.heightMeasurementResolution
                val newWeightResolution = bodyCompositionFeatureMap[record.device.UUID]?.massMeasurementResolution

                completeRecordCallback(record.run {
                    BodyCompositionRecord(
                        bodyFatPercent,
                        dateTime,
                        userId,
                        basalMetabolism,
                        musclePercent,
                        muscleMass,
                        fatFreeMass,
                        softLeanMass,
                        bodyWaterMass,
                        impedance,
                        device,
                        newHeightResolution ?: heightMeasurementResolution,
                        newWeightResolution ?: weightMeasurementResolution
                    )}
                )
            }

            is WeightFeatures -> weightFeateatureMap[record.device.UUID] = record
            is WeightRecord -> {
                val newHeightResolution= weightFeateatureMap[record.device.UUID]?.heightMeasurementResolution
                val newWeightResolution= weightFeateatureMap[record.device.UUID]?.weightMeasurementResolution

                completeRecordCallback(
                    record.run {
                        WeightRecord(
                            weight,
                            weightUnit,
                            timestamp,
                            userId,
                            BMI,
                            height,
                            heightUnit,
                            device,
                            newHeightResolution ?: heightMeasurementResolution,
                            newWeightResolution ?: weightMeasurementResolution
                        )
                    }
                )
            }

            //other records are not composite
            else -> completeRecordCallback(record)
        }

    /**
     * add deviceInfoComponent to deviceInfoBuilder, send it with callback when the last component is added
     */
    fun addDeviceInfoComponent(record: DeviceInfoComponent) {
        if(!deviceInfoRecords.containsKey(record.device.UUID)) deviceInfoRecords[record.device.UUID] = DeviceInfoBuilder(record.device)

        deviceInfoRecords[record.device.UUID]?.addComponent(record)
        val deviceInfo = deviceInfoRecords[record.device.UUID]?.build()

        if(deviceInfo != null) {
            deviceInfoRecords.remove(record.device.UUID)
            completeRecordCallback(deviceInfo)
        }
    }



    /**
     *A record has been completed, freeze it and send it with the callback if it is valid
     */
    fun completeRecordCallback(record : DataRecord?) {
        if(record != null ) {
            onCompleteRecord(record.freeze())
        }
    }


}

//TODO: Deal with the fact that some fields might not be sendt?
@ThreadLocal
class DeviceInfoBuilder(val device : PeripheralDescription) {
    var modelNumber = AtomicReference(null as String?)
    var serialNumber = AtomicReference(null as String?)
    var firmwareRevision = AtomicReference(null as String?)
    //var hardwareRevision = AtomicReference(null as String?)
    //var softwareRevision = AtomicReference(null as String?)
    var manufacturerName = AtomicReference(null as String?)
    var changedFields = frozenCopyOnWriteList(listOf(false,false,false,false))

    fun addComponent(record : DeviceInfoComponent) = when(record) {
        is DeviceInfoComponent.ModelNumber -> {
            modelNumber.set(record.value)
            changedFields[0] = true
        }
        is DeviceInfoComponent.SerialNumber -> {
            serialNumber.set(record.value)
            changedFields[1] = true
        }
        is DeviceInfoComponent.FirmwareRevision -> {
            firmwareRevision.set(record.value)
            changedFields[2] = true
        }
        is DeviceInfoComponent.HardwareRevision -> {
            //hardwareRevision.set(record.value)
            //changedFields[3] = true
        }
        is DeviceInfoComponent.SoftwareRevision -> {
            //softwareRevision.set(record.value)
            //changedFields[4] = true
        }
        is DeviceInfoComponent.ManufacturerName -> {
            manufacturerName.set(record.value)
            changedFields[3] = true
        }
    }

    //build deviceInfoRecord if all fields are set
    fun build() = if(changedFields.all { it }) {
        DeviceInfoRecord(
            modelNumber.get()?:"",//
            serialNumber.get()?:"",//
            firmwareRevision.get()?:"",//
            "",//hardwareRevision.get()?:"",
            "",//softwareRevision.get()?:"",
            manufacturerName.get()?:"",//
            device
        )
    } else null
}