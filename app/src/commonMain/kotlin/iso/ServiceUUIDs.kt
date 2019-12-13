package iso
//collection of IDs for different bluetooth services found at:
//https://www.bluetooth.com/specifications/gatt/services/

/**
 * Collection of service UUIDs, name is only used to make debuging easier
 */
sealed class ServiceUUID(val id : String) {
    val nr = id.substring(2)

    object glucose : ServiceUUID("0x1808") {
        val name = "Glucose Service"
    }

    object weight: ServiceUUID("0x181D") {
        val name = "Weight Service"
    }

    object deviceInformation : ServiceUUID("0x180A") {
        val name = "Device Information Service"
    }

    object battery : ServiceUUID( "0x180F") {
        val name = "Battery Service"
    }

    object bloodPressure : ServiceUUID("0x1810"){
        val name = "Blood Pressure Service"
    }

    //additional samsung health devices
    object health : ServiceUUID("0xFE00") {
        val name = "Health Service"
    }

    object sleep : ServiceUUID("0x0300") {
        val name = "Sleep Service"
    }

    object step : ServiceUUID("0x0200") {
        val name = "Step Service"
    }

    object enhancedHeartRate : ServiceUUID("0x0100") {
        val name = "Enhanced Heart Rate Service"
    }

}