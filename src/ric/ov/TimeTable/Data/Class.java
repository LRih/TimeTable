package ric.ov.TimeTable.Data;

import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Utils.TimeSpan;
import ric.ov.TimeTable.Utils.TimeUtils;

public final class Class implements Comparable<Class>
{
    //========================================================================= VARIABLES
    public final long id;
    public final String stsId;

    public final Course course;

    public final String type;

    public final Day day;
    public final TimeSpan start;
    public final TimeSpan end;
    public final String room;

    //========================================================================= INITIALIZE
    public Class(long id, String stsId, Course course, String type, Day day, TimeSpan start, TimeSpan end, String room)
    {
        this.id = id;
        this.stsId = stsId;

        this.course = course;

        this.type = type;

        this.day = day;
        this.start = start;
        this.end = end;
        this.room = room;
    }

    public static Class createNonSTS(Course course, String type, Day day, TimeSpan start, TimeSpan end, String room)
    {
        return new Class(-1, "", course, type, day, start, end, room);
    }
    public static Class createSTS(String stsId, Course course, String type, Day day, TimeSpan start, TimeSpan end, String room)
    {
        return new Class(-1, stsId, course, type, day, start, end, room);
    }

    //========================================================================= FUNCTIONS
    public final boolean isClash(Class cls)
    {
        return day == cls.day && start.compareTo(cls.end) < 0 && cls.start.compareTo(end) < 0;
    }
    public final boolean isClash(Day day, TimeSpan ts)
    {
        return this.day == day && ts.compareTo(start) >= 0 && ts.compareTo(end) < 0;
    }

    public final int compareTo(Class cls)
    {
        // show current today first
        int firstDay = TimeUtils.today().ordinal();

        int day1 = day.ordinal();
        if (day1 >= firstDay)
            day1 -= 7;

        int day2 = cls.day.ordinal();
        if (day2 >= firstDay)
            day2 -= 7;

        if (day1 < day2) return -1;
        else if (day1 > day2) return 1;


        int startCmp = start.compareTo(cls.start);
        if (startCmp != 0) return startCmp;

        int endCmp = end.compareTo(cls.end);
        if (endCmp != 0) return endCmp;

        int typeCmp = type.compareToIgnoreCase(cls.type);
        if (typeCmp != 0) return typeCmp;

        int courseCmp = course.compareTo(cls.course);
        if (courseCmp != 0) return courseCmp;

        return room.compareToIgnoreCase(cls.room);
    }

    //========================================================================= PROPERTIES
    public final boolean isFromSTS()
    {
        return stsId.length() > 0;
    }
}
