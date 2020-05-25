package gatt

import bledata.BLEReading
import data.EmptyRecord
import bledata.PeripheralDescription
import util.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RequirementTest {

    val readingWithPositiveFirstFlag = BLEReading(
        device = PeripheralDescription("",""),
        characteristic = CharacteristicUUIDs.manufacturerName,
        data = toByteArray(listOf("FF","FF", "01"))
    )


    @Test
    fun `parses value on positive condition`() {


        readingWithPositiveFirstFlag.parse {
            assertEquals<GATTValue.UInt8?>(
                uint8(),
                requirement {
                    condition = true
                    format = {GATTValue.UInt8(1)}
                }
            )

            EmptyRecord(PeripheralDescription(""))
        }
    }

    @Test
    fun `returns null on negative condition`() {
        readingWithPositiveFirstFlag.parse {
            assertNull(
                requirement {
                    condition = false
                    format = sint8
                }
            )

            EmptyRecord(PeripheralDescription(""))
        }
    }

    @Test
    fun `returns null on negative flag`() {
        readingWithPositiveFirstFlag.parse {
            flags(0..0)
            assertNull(
                requirement {
                    flag = 1
                    format = sint8
                }
            )

            EmptyRecord(PeripheralDescription(""))
        }
    }

    @Test
    fun `parses value on positive flag`() {
        readingWithPositiveFirstFlag.parse {
            flags(0..0)
            assertEquals<GATTValue.UInt8?>(
                uint8(),
                requirement {
                    flag = 0
                    format = uint8
                }
            )

            EmptyRecord(PeripheralDescription(""))
        }
    }
}