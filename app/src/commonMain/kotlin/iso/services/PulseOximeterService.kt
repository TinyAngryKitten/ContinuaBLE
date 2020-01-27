package iso.services

import bledata.BLEReading
import data.EmptyRecord
import iso.parse

//TODO: implement feature parsing(SO MANY FIELDS)....
/*fun parsePulseOximeterFeatures(reading: BLEReading) =
    parse(reading) {

    }*/

fun parsePlxSpotCheck(reading:  BLEReading) =
    parse(reading) {
        EmptyRecord(reading.device)
    }