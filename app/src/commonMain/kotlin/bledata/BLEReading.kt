package bledata

import data.DataRecord
import gatt.CharacteristicUUIDs
import gatt.ISOParser
import gatt.parse
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

    fun parse(function : ISOParser.() -> DataRecord) : DataRecord{
        return parse(this,function)
    }
}