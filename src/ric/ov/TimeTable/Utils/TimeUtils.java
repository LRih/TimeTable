package ric.ov.TimeTable.Utils;

import java.util.Calendar;

public final class TimeUtils
{
    private static Day _today;
    private static TimeSpan _now;


    private TimeUtils()
    {
        throw new AssertionError();
    }


    public static TimeSpan getDifference(Day d1, TimeSpan t1, Day d2, TimeSpan t2)
    {
        if (d1 == d2 && t2.compareTo(t1) >= 0)
            return t2.sub(t1);

        int dayDiff = d2.ordinal() - d1.ordinal();
        if (dayDiff <= 0)
            dayDiff += 7;

        int fullDays = dayDiff - 1;
        TimeSpan startDayTime = new TimeSpan(24 * 60).sub(t1);
        TimeSpan endDayTime = t2;

        return new TimeSpan(fullDays * 24 * 60 + startDayTime.totalMinutes + endDayTime.totalMinutes);
    }

    public static void updateDayTime()
    {
        Calendar cal = Calendar.getInstance();

        _today = Day.fromId((cal.get(Calendar.DAY_OF_WEEK) + 5) % 7);
        _now = new TimeSpan(0, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }


    public static Day today()
    {
        return _today;
    }
    public static TimeSpan now()
    {
        return _now;
    }
}
