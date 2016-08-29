package ric.ov.TimeTable.Utils;

public final class TimeSpan implements Comparable<TimeSpan>
{
    //========================================================================= VARIABLES
    public final int totalMinutes;

    //=========================================================================
    public TimeSpan(int totalMinutes)
    {
        this.totalMinutes = totalMinutes;
    }
    public TimeSpan(int days, int hours, int minutes)
    {
        totalMinutes = days * 60 * 24 + hours * 60 + minutes;
    }

    //========================================================================= FUNCTIONS
    public final TimeSpan add(TimeSpan ts)
    {
        return new TimeSpan(totalMinutes + ts.totalMinutes);
    }
    public final TimeSpan sub(TimeSpan ts)
    {
        return new TimeSpan(totalMinutes - ts.totalMinutes);
    }

    public final int compareTo(TimeSpan ts)
    {
        return totalMinutes - ts.totalMinutes;
    }

    //========================================================================= PROPERTIES
    public final int day()
    {
        return (totalMinutes / 60) / 24;
    }
    public final int hour()
    {
        return (totalMinutes / 60) % 24;
    }
    public final int minute()
    {
        return totalMinutes % 60;
    }

    public final String toLongTimeString()
    {
        String result = "";

        if (day() != 0)
        {
            if (result.length() > 0)
                result += " ";
            result += String.format("%dd", day());
        }

        if (hour() != 0)
        {
            if (result.length() > 0)
                result += " ";
            result += String.format("%dh", hour());
        }

        if (minute() != 0)
        {
            if (result.length() > 0)
                result += " ";
            result += String.format("%dm", minute());
        }

        // negative sign
        if (totalMinutes < 0)
            result = "-" + result;

        return result;
    }
    public final String toTimeString()
    {
        String prefix = totalMinutes < 0 ? "-" : "";

        if (day() != 0)
            return String.format("%s%d:%02d:%02d", prefix, day(), hour(), minute());
        return String.format("%s%d:%02d", prefix, hour(), minute());
    }
}
