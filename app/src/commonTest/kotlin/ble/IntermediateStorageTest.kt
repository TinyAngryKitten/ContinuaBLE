package ble

import data.*
import iso.CharacteristicUUIDs
import kotlin.test.*

class IntermediateStorageTest {

    val device = PeripheralDescription("devicename")

    val manufacturerName = "manufacturer name"
    val softwareRevName = "softwareRev"
    val serialNumber = "serialnumber"


    @Test
    fun `weight resolution is default when bodycomposition features is not present`() {
        var result : Any? = null

        val storage = IntermediateRecordStorage {
            result = it

            if(it is BodyCompositionRecord) {
                assertEquals(WeightMeasurementResolution.NotSpecified,it.weightMeasurementResolution)
            } else assertTrue { false }
        }
        storage.addRecord(BodyCompositionRecord(1,null,null,null,null,10,null,null,null,null,device))


        assertNotNull(result)
    }

    @Test
    fun `weight resolution is updated when bodycompositon features exist`() {
        val resolution = WeightMeasurementResolution.Res2
        var result : Any? = null

        val storage = IntermediateRecordStorage {
            result = it

            if(it is BodyCompositionRecord) {
                assertEquals(resolution,it.weightMeasurementResolution)
            } else assertTrue { false }
        }

        storage.addRecord(BodyCompositionFeature(false,false,false,false,false,false,false,false,false,false,false,resolution,HeightMeasurementResolution.NotSpecified,device))

        storage.addRecord(BodyCompositionRecord(1,null,null,null,null,10,null,null,null,null,device))

        assertNotNull(result)
    }

    @Test
    fun `weight resolution is default when wheightfeatures is not present`() {
        val weight = 50
        var result : Any? = null

        val storage = IntermediateRecordStorage {
            result = it

            if(it is WeightRecord) {
                assertEquals(weight,it.weight)
                assertEquals(WeightMeasurementResolution.NotSpecified,it.weightMeasurementResolution)
            } else assertTrue { false }
        }
        storage.addRecord(WeightRecord(weight,WeightUnit.KG,null,null,null,null,null,device,HeightMeasurementResolution.NotSpecified,
            WeightMeasurementResolution.NotSpecified))

        assertNotNull(result)
    }

    @Test
    fun `weight resolution is updated when weightfeatures exist`() {
        val weight = 50
        val resolution = WeightMeasurementResolution.Res2
        var result : Any? = null

        val storage = IntermediateRecordStorage {
            result = it

            if(it is WeightRecord) {
                assertEquals(weight,it.weight)
                assertEquals(resolution,it.weightMeasurementResolution)
            } else assertTrue { false }
        }

        storage.addRecord(WeightFeatures(false,false,false,resolution,
            HeightMeasurementResolution.MediumRes,device))

        storage.addRecord(WeightRecord(weight,WeightUnit.KG,null,null,null,null,null,device,HeightMeasurementResolution.NotSpecified,
            WeightMeasurementResolution.NotSpecified))

        assertNotNull(result)
    }


    @Test
    fun `no result callback when adding second of three DeviceInfoComponents`() {

        val storage = IntermediateRecordStorage {
            //this should not be called
            assertTrue { false }
        }

        storage.addDeviceCapability(device,CharacteristicUUIDs.manufacturerName)
        storage.addDeviceCapability(device,CharacteristicUUIDs.softwareRevision)
        storage.addDeviceCapability(device,CharacteristicUUIDs.serialNumber)

        storage.addRecord(DeviceInfoComponent.ManufacturerName(manufacturerName, device))
        storage.addRecord(DeviceInfoComponent.SoftwareRevision(softwareRevName, device))

    }

    @Test
    fun `add last DeviceInfoComponent`() {
        var result : DataRecord? = null

        val storage = IntermediateRecordStorage {

            assertTrue { it is DeviceInfoRecord }
            if(it is DeviceInfoRecord) {
                assertEquals(device, it.device)
                assertEquals(manufacturerName,it.manufacturerName)
                assertEquals(softwareRevName,it.softwareRevision)
                assertEquals(serialNumber,it.serialNumber)
            }
            result = it
        }

        storage.addDeviceCapability(device,CharacteristicUUIDs.manufacturerName)
        storage.addDeviceCapability(device,CharacteristicUUIDs.softwareRevision)
        storage.addDeviceCapability(device,CharacteristicUUIDs.serialNumber)

        storage.addRecord(DeviceInfoComponent.ManufacturerName(manufacturerName, device))
        storage.addRecord(DeviceInfoComponent.SoftwareRevision(softwareRevName, device))
        storage.addRecord(DeviceInfoComponent.SerialNumber(serialNumber, device))

        assertNotNull(result)
    }
}