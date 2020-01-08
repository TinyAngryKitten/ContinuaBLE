package bledata

import data.PeripheralDescription
import iso.CharacteristicUUIDs
import util.strRepresentation

data class BLEReading(
    val device: PeripheralDescription,
    val characteristic :CharacteristicUUIDs,
    val data : ByteArray
    ) {

    override fun toString(): String = """ 
    BLEReading(
    device = $device,
    characteristic = $characteristic,
    data = ${data.strRepresentation()}
    )
    """.trimIndent()
}