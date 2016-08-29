package ric.ov.TimeTable;

import android.content.Context;
import android.util.Log;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.Course;
import ric.ov.TimeTable.Data.SQL;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Utils.TimeSpan;

public final class Test
{
    public static void main(String[] args)
    {
    }


    public static void loadData(Context context)
    {
        try
        {
            Course course;

            SQL.deleteAll(context);

            course = Course.createSTS(155023, "COSC1127", "Artificial Intelligence");
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-1127-AUSCY-S1-LEC01/1", course, "Lecture", Day.Wednesday, new TimeSpan(0, 12, 30), new TimeSpan(0, 14, 30), "12.07.02"));
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-1127-AUSCY-S1-TUT01/1", course, "Tutorial", Day.Wednesday, new TimeSpan(0, 15, 30), new TimeSpan(0, 16, 30), "14.06.19"));
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-1127-AUSCY-S1-PRA01/1", course, "Practical", Day.Wednesday, new TimeSpan(0, 14, 30), new TimeSpan(0, 15, 30), "14.10.31"));

            course = Course.createSTS(147592, "COSC2406", "Database Systems");
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-2406-AUSCY-S1-LEC01/01", course, "Lecture", Day.Monday, new TimeSpan(0, 15, 30), new TimeSpan(0, 17, 30), "37.03.04"));
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-2406-AUSCY-S1-TUT01/03", course, "Tutorial", Day.Friday, new TimeSpan(0, 10, 30), new TimeSpan(0, 11, 30), "12.11.19"));
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-2406-AUSCY-S1-PRA01/02", course, "Practical", Day.Friday, new TimeSpan(0, 11, 30), new TimeSpan(0, 12, 30), "14.10.30"));

            course = Course.createSTS(154953, "COSC2299", "Software Engineering: Process and Tools");
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-2299-AUSCY-S1-LEC01/01", course, "Lecture", Day.Thursday, new TimeSpan(0, 16, 30), new TimeSpan(0, 18, 30), "56.06.82"));
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-2299-AUSCY-S1-TUT01/02", course, "Tutorial", Day.Friday, new TimeSpan(0, 12, 30), new TimeSpan(0, 14, 30), "51.06.22"));

            course = Course.createSTS(155856, "COSC1179", "Network Programming");
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-1179-AUSCY-S1-LEC01/01", course, "Lecture", Day.Monday, new TimeSpan(0, 12, 30), new TimeSpan(0, 14, 30), "08.11.61"));
            SQL.insertClassIfAbsent(context, Class.createSTS("COSC-1179-AUSCY-S1-PRA01/01", course, "Practical", Day.Monday, new TimeSpan(0, 14, 30), new TimeSpan(0, 16, 30), "14.10.31"));

            course = Course.createNonSTS("Security Club");
            SQL.insertClassIfAbsent(context, Class.createNonSTS(course, "", Day.Friday, new TimeSpan(0, 16, 30), new TimeSpan(0, 19, 0), "80.08.06"));

            course = Course.createNonSTS("Chess Club");
            SQL.insertClassIfAbsent(context, Class.createNonSTS(course, "", Day.Thursday, new TimeSpan(0, 17, 30), new TimeSpan(0, 19, 30), "56.05.97"));
        }
        catch (Exception ex)
        {
            Log.i("TT-Test", "Loading test data failed");
        }
    }
}
