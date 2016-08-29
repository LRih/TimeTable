package ric.ov.TimeTable.Data;

import android.graphics.Color;

public final class Course implements Comparable<Course>
{
    //========================================================================= VARIABLES
    public final long id;
    public final long offeringId;

    public final String code;
    public final String name;

    public final int color;

    //========================================================================= INITIALIZE
    public Course(long id, long offeringId, String code, String name, int color)
    {
        this.id = id;
        this.offeringId = offeringId;

        this.code = code;
        this.name = name;

        this.color = color;
    }

    public static Course createNonSTS(String name)
    {
        return new Course(-1, -1, "", name, Color.GRAY);
    }
    public static Course createSTS(long offeringId, String code, String name)
    {
        return new Course(-1, offeringId, code, name, Color.GRAY);
    }

    //========================================================================= FUNCTIONS
    public final int compareTo(Course c)
    {
        int nameCmp = name.compareToIgnoreCase(c.name);
        if (nameCmp != 0) return nameCmp;

        return code.compareToIgnoreCase(c.code);
    }

    //========================================================================= PROPERTIES
    public final boolean isFromSTS()
    {
        return offeringId != -1;
    }

    public final String abbreviation()
    {
        return name.isEmpty() ? "" : String.valueOf(name.charAt(0));
    }
}
