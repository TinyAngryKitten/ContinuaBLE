package iso
//collection of IDs for different bluetooth services found at:
//https://www.bluetooth.com/specifications/gatt/services/

/**
 * Collection of service UUIDs, name is only used to make debuging easier
 */
sealed class ServiceUUID(val id : String) {
    val nr = id.substring(2)
    abstract val name : String

    object glucose : ServiceUUID("0x1808") {
        override val name = "Glucose Service"
    }

    object weight: ServiceUUID("0x181D") {
        override val name = "Weight Service"
    }

    object bodyComposition : ServiceUUID("0x180F") {
        override val name = "Body Composition Service"
    }

    object deviceInformation : ServiceUUID("0x180A") {
        override val name = "Device Information Service"
    }

    object battery : ServiceUUID( "0x180F") {
        override val name = "Battery Service"
    }

    object bloodPressure : ServiceUUID("0x1810"){
        override val name = "Blood Pressure Service"
    }

    //additional samsung health devices
    object health : ServiceUUID("0xFE00") {
        override val name = "Health Service"
    }

    object sleep : ServiceUUID("0x0300") {
        override val name = "Sleep Service"
    }

    object step : ServiceUUID("0x0200") {
        override val name = "Step Service"
    }

    object enhancedHeartRate : ServiceUUID("0x0100") {
        override val name = "Enhanced Heart Rate Service"
    }

}