package iso

import bledata.DayOfWeekEnum
import java.util.*

fun getDayOfWeek() : DayOfWeekEnum =
    Calendar.getInstance()?.let {
        when(it.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> DayOfWeekEnum.Monday
            Calendar.TUESDAY -> DayOfWeekEnum.Tuesday
            Calendar.WEDNESDAY -> DayOfWeekEnum.Wednsday
            Calendar.THURSDAY -> DayOfWeekEnum.Thursday
            Calendar.FRIDAY -> DayOfWeekEnum.Friday
            Calendar.SATURDAY -> DayOfWeekEnum.Saturday
            Calendar.SUNDAY -> DayOfWeekEnum.Sunday
            else -> DayOfWeekEnum.Monday
        }
    } ?: DayOfWeekEnum.Monday
