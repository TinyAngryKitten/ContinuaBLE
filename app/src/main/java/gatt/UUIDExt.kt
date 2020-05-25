package gatt

import java.util.*

val UUID.identifier
    get() = toString().substring(4,8)

fun ServiceUUID.equalsAndroidUUID(uuid: UUID) = uuid.identifier.equals(nr,true)
//will only work for id defined in the GATT specification
fun ServiceUUID.toAndroidUUID() = "0000${nr.toLowerCase()}-0000-1000-8000-00805f9b34fb"
fun CharacteristicUUIDs.equalsAndroidUUID(uuid: UUID) = uuid.identifier.equals(nr,true)