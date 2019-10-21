package data

data class PeripheralDescription(
    val UUID : String,
    val name : String = "Unknown"
) {
    companion object {
        fun fromNullable(id : String, name : String?) : PeripheralDescription{
            return if(name == null) PeripheralDescription(id)
            else PeripheralDescription(id,name)
        }
    }
}