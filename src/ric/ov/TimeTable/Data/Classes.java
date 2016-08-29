package ric.ov.TimeTable.Data;

import java.util.List;

public final class Classes
{
    //========================================================================= VARIABLES
    public final Course course;
    public final List<Class> items;

    //========================================================================= INITIALIZE
    public Classes(Course course, List<Class> items)
    {
        this.course = course;
        this.items = items;
    }
}
