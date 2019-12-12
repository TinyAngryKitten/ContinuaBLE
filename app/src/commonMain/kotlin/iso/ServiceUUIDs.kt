package iso
//collection of IDs for different bluetooth services found at:
//https://www.bluetooth.com/specifications/gatt/services/

sealed class ServiceUUID(val id : String) {
    val nr = id.substring(2)

    object glucoseServiceUUID : ServiceUUID("0x1808") {
        val name = "Glucose Service"
    }

    object weightServiceUUID: ServiceUUID("0x181D") {
        val name = "Weight Service"
    }

    object deviceInformationServiceUUID : ServiceUUID("0x180A") {
        val name = "Device Information Service"
    }

    object batteryServiceUUID : ServiceUUID( "0x180F") {
        val name = "Battery Service"
    }


    //additional samsung health devices
    object healthServiceUUID : ServiceUUID("0xFE00") {
        val name = "Health Service"
    }

    object sleepServiceUUID : ServiceUUID("0x0300") {
        val name = "Sleep Service"
    }

    object stepServiceUUID : ServiceUUID("0x0200") {
        val name = "Step Service"
    }

    object enhancedHeartRateServiceUUID : ServiceUUID("0x0100") {
        val name = "Enhanced Heart Rate Service"
    }
}