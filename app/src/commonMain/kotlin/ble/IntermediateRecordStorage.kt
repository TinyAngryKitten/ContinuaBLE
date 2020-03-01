package ble

import co.touchlab.stately.collections.frozenCopyOnWriteList
import co.touchlab.stately.collections.frozenHashMap
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.freeze
import data.*
import sample.logger
import kotlin.native.concurrent.ThreadLocal

class IntermediateRecordStorage(val onCompleteRecord : (DataRecord) -> Unit) {
    //val deviceCapabilities: MutableMap<String, DeviceCapabilities.DeviceServices> = frozenHashMap()

    val glucoseRecords = frozenHashMap<String,GlucoseRecord>()
    val glucoseContextRecords = frozenHashMap<String,GlucoseRecordContext>()

    val deviceInfoRecords = frozenHashMap<String,DeviceInfoBuilder>()

    val bodyCompositionFeatureMap = frozenHashMap<String,BodyCompositionFeature>()
    val weightFeateatureMap = frozenHashMap<String,WeightFeatures>()

    /*
    fun addDeviceCapabilities(capability : DeviceCapabilities) = when(capability) {
        is DeviceCapabilities.DeviceServices -> deviceCapabilities[capability.device.UUID] = capability
        is DeviceCapabilities.ServiceCharacteristics -> {
            deviceCapabilities[capability.device.UUID]?.services?.add(capability)
            Unit
        }
        if
    }*/


    fun addRecord(record : DataRecord): Unit =
        when(record) {
            is EmptyRecord -> logger.debug("empty record added to record central")

            is GlucoseRecord -> when(record.context) {
                is HasGlucoseContext.NotReceivedYet-> {

                    glucoseContextRecords[record.device.UUID]?.let {
                        completeRecordCallback(record.copyWithContext(HasGlucoseContext.Context(it)))
                    }
                    glucoseRecords[record.device.UUID] = record
                }
                else -> {//contex does not follow, send record as is
                    completeRecordCallback(record)
                }
            }
            is GlucoseRecordContext -> {
                if(glucoseRecords.containsKey(record.device.UUID)) {

                    completeRecordCallback(
                        glucoseRecords[record.device.UUID]?.copyWithContext(
                            HasGlucoseContext.Context(
                                record
                            )
                        )
                    )
                } else glucoseContextRecords[record.device.UUID]= record
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
            //deviceInfo shouldnt be changing, so just keep it in memory
            completeRecordCallback(deviceInfo)
        }
    }



    /**
     *A record has been completed, freeze it and send it with the callback if it is valid
     */
    fun completeRecordCallback(record : DataRecord?) {
        if(record != null ) {
            //do some cleanup to ensure that there are no old records
            if(record is GlucoseRecord) {
                glucoseContextRecords.remove(record.device.UUID)
                glucoseRecords.remove(record.device.UUID)
            }
            onCompleteRecord(record.freeze())
        }
    }
}

//TODO: Deal with the fact that some fields might not be sendt?
@ThreadLocal
class DeviceInfoBuilder(val device : PeripheralDescription) {
    val modelNumber = AtomicReference(null as String?)
    val serialNumber = AtomicReference(null as String?)
    val firmwareRevision = AtomicReference(null as String?)
    val hardwareRevision = AtomicReference(null as String?)
    val softwareRevision = AtomicReference(null as String?)
    val manufacturerName = AtomicReference(null as String?)
    val changedFields = frozenCopyOnWriteList(listOf(false,false,false))

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
            //changedFields[2] = true
        }
        is DeviceInfoComponent.HardwareRevision -> {
            hardwareRevision.set(record.value)
            //changedFields[4] = true
        }
        is DeviceInfoComponent.SoftwareRevision -> {
            softwareRevision.set(record.value)
            //changedFields[5] = true
        }
        is DeviceInfoComponent.ManufacturerName -> {
            manufacturerName.set(record.value)
            changedFields[2] = true
        }
    }

    //TODO: is it acceptible to possibly not register revision numbers even when they are available?
    //build deviceInfoRecord if all fields are set, there is a chance that only one or no fields are present
    // this ignores hardware firmware and software revision  because it might never be sendt, but device info should still be of some value
    fun build() = if(changedFields.all { it }) {
        DeviceInfoRecord(
            modelNumber.get()?:"",//
            serialNumber.get()?:"",//
            firmwareRevision.get()?:"",//
            hardwareRevision.get()?:"",
            softwareRevision.get()?:"",
            manufacturerName.get()?:"",//
            device
        )
    } else null
}