package ble

import bledata.DeviceCapabilities
import bledata.PeripheralDescription
import co.touchlab.stately.collections.frozenCopyOnWriteList
import co.touchlab.stately.collections.frozenHashMap
import co.touchlab.stately.collections.frozenLinkedList
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.freeze
import data.*
import data.glucose.HasGlucoseContext
import gatt.CharacteristicUUIDs
import gatt.ServiceUUID
import util.logger
import kotlin.native.concurrent.ThreadLocal

class IntermediateRecordStorage(val onCompleteRecord : (DataRecord) -> Unit) {
    val deviceCapabilities = frozenLinkedList<DeviceCapabilities>()

    val glucoseRecords = frozenHashMap<String,GlucoseRecord>()
    val glucoseContextRecords = frozenHashMap<String,GlucoseRecordContext>()

    val deviceInfoRecords = frozenHashMap<String,DeviceInfoBuilder>()

    val bodyCompositionFeatureMap = frozenHashMap<String,BodyCompositionFeature>()
    val weightFeateatureMap = frozenHashMap<String,WeightFeatures>()

    fun addDeviceCapability(
        device: PeripheralDescription,
        characteristic: CharacteristicUUIDs,
        service: ServiceUUID = characteristic.service
    ) {
        if(deviceCapabilities.find {it.device == device} == null) deviceCapabilities.add(DeviceCapabilities(device))
        deviceCapabilities.find { it.device == device }?.addCharacteristic(characteristic, service)
    }

    fun addRecord(record : DataRecord): Unit =
        when(record) {
            is EmptyRecord -> logger.debug("empty record added to intermediate storage")

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
        val deviceInfo = deviceInfoRecords[record.device.UUID]
            ?.build(
                deviceCapabilities
                    .find { it.device == record.device }
                    ?.capabilities?.get(ServiceUUID.deviceInformation)
                    ?.filter { it !is CharacteristicUUIDs.UnsupportedCharacteristic}
                    ?: listOf()
            )

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
            //do some cleanup to ensure that there are no old records
            if(record is GlucoseRecord) {
                glucoseContextRecords.remove(record.device.UUID)
                glucoseRecords.remove(record.device.UUID)
            }

            onCompleteRecord(record.freeze())
        }
    }
}


//Holds components of device info until all parts are present
@ThreadLocal
class DeviceInfoBuilder(val device : PeripheralDescription) {
    val modelNumber = AtomicReference(null as String?)
    val serialNumber = AtomicReference(null as String?)
    val firmwareRevision = AtomicReference(null as String?)
    val hardwareRevision = AtomicReference(null as String?)
    val softwareRevision = AtomicReference(null as String?)
    val manufacturerName = AtomicReference(null as String?)

    //remember which characteristics has been recorded, for comparison against which characteristics
    //the device supports to determine when all the data is complete
    val characteristicsRecorded = frozenCopyOnWriteList<CharacteristicUUIDs>()

    fun addComponent(record : DeviceInfoComponent) = when(record) {
        is DeviceInfoComponent.ModelNumber -> {
            characteristicsRecorded.add(CharacteristicUUIDs.modelNumber)
            modelNumber.set(record.value)
        }
        is DeviceInfoComponent.SerialNumber -> {
            characteristicsRecorded.add(CharacteristicUUIDs.serialNumber)
            serialNumber.set(record.value)
        }
        is DeviceInfoComponent.FirmwareRevision -> {
            characteristicsRecorded.add(CharacteristicUUIDs.firmwareRevision)
            firmwareRevision.set(record.value)
        }
        is DeviceInfoComponent.HardwareRevision -> {
            characteristicsRecorded.add(CharacteristicUUIDs.hardwareRevision)
            hardwareRevision.set(record.value)
            //changedFields[4] = true
        }
        is DeviceInfoComponent.SoftwareRevision -> {
            characteristicsRecorded.add(CharacteristicUUIDs.softwareRevision)
            softwareRevision.set(record.value)
        }
        is DeviceInfoComponent.ManufacturerName -> {
            characteristicsRecorded.add(CharacteristicUUIDs.manufacturerName)
            manufacturerName.set(record.value)
        }
    }

    fun build(expectedFields: List<CharacteristicUUIDs>) : DeviceInfoRecord?{

        return if (characteristicsRecorded.containsAll(expectedFields)) {
            DeviceInfoRecord(
                modelNumber.get() ?: "",
                serialNumber.get() ?: "",
                firmwareRevision.get() ?: "",
                hardwareRevision.get() ?: "",
                softwareRevision.get() ?: "",
                manufacturerName.get() ?: "",
                device
            )
        } else null
    }
}