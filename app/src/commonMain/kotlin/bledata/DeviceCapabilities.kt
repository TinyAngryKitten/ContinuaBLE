package bledata

import co.touchlab.stately.collections.frozenHashMap
import co.touchlab.stately.collections.frozenLinkedList
import data.PeripheralDescription
import iso.CharacteristicUUIDs
import iso.ServiceUUID

class DeviceCapabilities(val device : PeripheralDescription) {
    val capabilities : MutableMap<ServiceUUID,MutableList<CharacteristicUUIDs>> = frozenHashMap()

    fun addCharacteristic(characteristic: CharacteristicUUIDs, service : ServiceUUID = characteristic.service) {
        if(!capabilities.containsKey(service)) capabilities[service] = frozenLinkedList()
        capabilities[service]?.add(characteristic)
    }
}