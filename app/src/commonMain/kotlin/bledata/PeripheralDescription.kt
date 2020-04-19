package data

data class PeripheralDescription(
    val UUID : String,
    val name : String = "Unknown"
) {
    override fun equals(other: Any?): Boolean {
        return if(other is PeripheralDescription) UUID == other.UUID
        else false
    }
    companion object {
        fun fromNullable(id : String, name : String?) : PeripheralDescription{
            return if(name == null) PeripheralDescription(id)
            else PeripheralDescription(id,name)
        }
    }
}