package iso

import bledata.BLEReading
import data.DataRecord
import data.EmptyRecord
import iso.services.parseBatteryLevel
import iso.services.parseGlucoseFeatures
import iso.services.parseGlucoseReading
import sample.logger

//look for a supported characteristic with the same nr(hex id without the leading 0x) as the reading have
//if found, use the assosiated parse method to parse the reading, if not found return empty reading
fun parseBLEReading(reading : BLEReading): DataRecord =
    CharacteristicUUIDs.getAll().fold(EmptyRecord(reading.device) as DataRecord){
    acc,characteristic ->

    if(characteristic == reading.characteristic) {
        return characteristic.parse(reading)
    }
    else acc
}
