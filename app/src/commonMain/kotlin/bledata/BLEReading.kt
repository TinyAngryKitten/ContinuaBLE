package bledata

import data.CharacteristicDescription
import data.PeripheralDescription
import util.strRepresentation

data class BLEReading(
    val device: PeripheralDescription,
    val characteristic :CharacteristicDescription,
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