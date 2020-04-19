package bledata

import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID

class DeviceCapability(val device : PeripheralDescription) {
    val capabilities : MutableMap<ServiceUUID,MutableList<CharacteristicUUIDs>> = mutableMapOf()

    fun addCharacteristic(characteristic: CharacteristicUUIDs, service : ServiceUUID = characteristic.service) {
        if(!capabilities.containsKey(service)) capabilities[service] = mutableListOf()
        capabilities[service]?.add(characteristic)
    }
}