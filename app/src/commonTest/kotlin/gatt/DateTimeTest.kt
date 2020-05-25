package gatt

import util.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeTest {
    @Test
    fun `valid date time is valid`() {
        val seconds = "20"
        val minutes = "10"
        val hours = "1"
        val days = "10"
        val month = "A"
        val year = "07D9"

        val bytes = toByteArray(
            listOf(
                seconds,
                minutes,
                hours,
                days,
                month,
                year.substring(0,2),
                year.substring(2)
            )
        )

        val result = ISOParser(bytes).dateTime()

        assertEquals(result.seconds,seconds.toInt(16))
        assertEquals(result.minutes,minutes.toInt(16))
        assertEquals(result.hours,hours.toInt(16))
        assertEquals(result.day,days.toInt(16))
        assertEquals(result.month.value,month.toInt(16))
        assertEquals(result.year.value,year.toInt(16))
    }
}