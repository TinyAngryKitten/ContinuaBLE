package iso

import java.util.*

val UUID.identifier
    get() = toString().substring(4,8)

fun ServiceUUID.equalsAndroidUUID(uuid: UUID) = uuid.identifier.equals(nr,true)
fun CharacteristicUUIDs.equalsAndroidUUID(uuid: UUID) = uuid.identifier.equals(nr,true)