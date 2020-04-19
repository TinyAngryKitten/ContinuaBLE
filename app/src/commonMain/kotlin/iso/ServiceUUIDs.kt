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
    object heartRate : ServiceUUID("0x180D") {
        override val name = "Heart Rate Service"
    }
    object thermometer : ServiceUUID("0x1809") {
        override val name = "Health Thermometer Service"
    }
    object pulseOximeter : ServiceUUID("0x1822") {
        override val name = "Pulse Oximeter Service"
    }

    object currentTime : ServiceUUID("0x1805") {
        override val name: String
            get() = "Current Time Service"
    }

    object unknown : ServiceUUID("0x0000") {
        override val name: String
            get() = "unknown"

        override fun toString(): String {
            return "unknown servicee"
        }
    }


    companion object {
        fun getAll() = listOf(
            glucose,
            weight,
            bodyComposition,
            deviceInformation,
            battery,
            bloodPressure,
            heartRate,
            thermometer,
            currentTime
        )

        //get services that should be scanned for
        fun getDiscoverable() = getAll().minus(listOf(
            deviceInformation,
            battery,
            currentTime
        ))


        fun fromNr(nr : String) = getAll().find { it.nr.equals(nr,ignoreCase = true) }
        fun fromId(id : String) = getAll().find { it.id.equals(id,ignoreCase = true) }

    }
}