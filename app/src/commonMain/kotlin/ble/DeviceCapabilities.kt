package ble

import co.touchlab.stately.collections.frozenCopyOnWriteList
import co.touchlab.stately.collections.frozenHashMap
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID

/*
sealed class DeviceCapabilities(val device: PeripheralDescription){

    class DeviceServices(
        val services: MutableMap<String,ServiceCharacteristics> = frozenHashMap<>(),
        device: PeripheralDescription
    ) : DeviceCapabilities(device) {
        fun add(deviceCapabilities: DeviceCapabilities) = when(deviceCapabilities) {
            is ServiceCapability -> services[deviceCapabilities..put(deviceCapabilities.ser)
        }
    }

    class ServiceCharacteristics(
        val service: ServiceUUID,
        val characteristics: MutableList<CharacteristicUUIDs> = frozenCopyOnWriteList(),
        device: PeripheralDescription
    ) : DeviceCapabilities(device)

    class ServiceCapability(
        val characteristic: CharacteristicUUIDs,
        device: PeripheralDescription
    ) : DeviceCapabilities(device)

}
*/