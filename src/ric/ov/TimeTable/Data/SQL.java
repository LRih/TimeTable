package ric.ov.TimeTable.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Utils.MathUtils;
import ric.ov.TimeTable.Utils.StringUtils;
import ric.ov.TimeTable.Utils.TimeSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SQL
{
    //========================================================================= VARIABLES
    private static SQLHelper _sqlHelper;

    //========================================================================= INITIALIZE
    private SQL()
    {
        throw new AssertionError();
    }

    private static synchronized SQLiteDatabase getDB(Context context)
    {
        if (_sqlHelper == null)
            _sqlHelper = new SQLHelper(context);
        return _sqlHelper.getWritableDatabase();
    }

    //========================================================================= FUNCTIONS
    private static long insertCourseIfAbsent(Context context, Course newCourse) throws Exception
    {
        // validate data
        if (!isCourseValid(newCourse))
            throw new Exception();

        String name = StringUtils.trimSpaces(newCourse.name);
        Course course;

        // use offering id as identifier for STS courses
        if (newCourse.isFromSTS())
            course = loadCourse(context, newCourse.offeringId);
        else // otherwise use name from non STS courses
            course = loadCourse(context, -1, name);

        if (course != null)
        {
            Log.i("TT-SQL", "Course '" + name + "' already exists");
            return course.id;
        }
        else
        {
            Log.i("TT-SQL", "Adding course '" + name + "'");
            return insertCourse(context, newCourse);
        }
    }
    public static void insertClassIfAbsent(Context context, Class newClass) throws Exception
    {
        // validate data
        if (!isClassValid(newClass))
            throw new Exception();

        // don't add if STS class already exists
        if (newClass.isFromSTS() && loadClass(context, newClass.stsId) != null)
        {
            Log.i("TT-SQL", "Class '" + newClass.stsId + "' already exists");
            return;
        }

        getDB(context).beginTransaction();
        try
        {
            long courseId = insertCourseIfAbsent(context, newClass.course);
            insertClass(context, courseId, newClass);

            getDB(context).setTransactionSuccessful();
        }
        finally
        {
            getDB(context).endTransaction();
        }
    }

    public static void updateClassAndCourse(Context context, Class updatedClass) throws Exception
    {
        if (updatedClass.isFromSTS())
        {
            Log.i("TT-SQL", "Updating STS class");
            updateClass(context, updatedClass);
            return;
        }

        Class cls = loadClass(context, updatedClass.id);

        // unchanged course
        if (updatedClass.course.name.equals(cls.course.name))
        {
            Log.i("TT-SQL", "Updating class (no course change)");
            updateClass(context, updatedClass);
            return;
        }

        // changed course
        getDB(context).beginTransaction();
        try
        {
            Log.i("TT-SQL", "Updating class (course change)");

            deleteClass(context, cls);

            // delete course with no items
            if (loadClasses(context, cls.course.id).isEmpty())
            {
                deleteCourse(context, cls.course);
                Log.i("TT-SQL", "Deleting course '" + cls.course.name + "'");
            }

            // insert class and also course if required
            long courseId = insertCourseIfAbsent(context, updatedClass.course);
            insertClass(context, courseId, updatedClass);

            getDB(context).setTransactionSuccessful();
        }
        finally
        {
            getDB(context).endTransaction();
        }
    }

    public static void deleteClassAndEmptyCourse(Context context, Class cls)
    {
        getDB(context).beginTransaction();
        try
        {
            deleteClass(context, cls);

            // delete course with no items
            if (loadClasses(context, cls.course.id).isEmpty())
            {
                deleteCourse(context, cls.course);
                Log.i("TT-SQL", "Deleting course '" + cls.course.name + "'");
            }

            getDB(context).setTransactionSuccessful();
        }
        finally
        {
            getDB(context).endTransaction();
        }
    }

    public static void deleteAll(Context context)
    {
        getDB(context).beginTransaction();
        try
        {
            getDB(context).delete(Schema.Courses.TABLE_NAME, null, null);
            getDB(context).delete(Schema.Classes.TABLE_NAME, null, null);
            getDB(context).setTransactionSuccessful();
        }
        finally
        {
            getDB(context).endTransaction();
        }
    }

    //------------------------------------------------------------------------- base sql functions
    public static List<String> loadCourseNames(Context context)
    {
        List<String> names = new ArrayList<>();

        String[] select = new String[] { Schema.Courses.COL_NAME };

        Cursor c = getDB(context).query(Schema.Courses.TABLE_NAME, select, null, null, null, null, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            names.add(c.getString(0));

        c.close();

        Collections.sort(names);

        return names;
    }
    private static List<Course> loadCourses(Context context)
    {
        List<Course> courses = new ArrayList<>();

        String[] select = new String[] {
            Schema.Courses.COL_ID, Schema.Courses.COL_OFFERING_ID,
            Schema.Courses.COL_CODE, Schema.Courses.COL_NAME, Schema.Courses.COL_COLOR
        };

        Cursor c = getDB(context).query(Schema.Courses.TABLE_NAME, select, null, null, null, null, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            courses.add(new Course(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getInt(4)));

        c.close();

        Collections.sort(courses);

        return courses;
    }
    private static Course loadCourse(Context context, long offeringId)
    {
        Course course = null;

        String[] select = new String[] {
            Schema.Courses.COL_ID, Schema.Courses.COL_OFFERING_ID,
            Schema.Courses.COL_CODE, Schema.Courses.COL_NAME, Schema.Courses.COL_COLOR
        };
        String where = Schema.Courses.COL_OFFERING_ID + "=" + offeringId;

        Cursor c = getDB(context).query(Schema.Courses.TABLE_NAME, select, where, null, null, null, null);

        if (c.moveToFirst())
            course = new Course(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getInt(4));

        c.close();

        return course;
    }
    private static Course loadCourse(Context context, long offeringId, String name)
    {
        Course course = null;

        String[] select = new String[] {
            Schema.Courses.COL_ID, Schema.Courses.COL_OFFERING_ID,
            Schema.Courses.COL_CODE, Schema.Courses.COL_NAME, Schema.Courses.COL_COLOR
        };
        String where = Schema.Courses.COL_OFFERING_ID + "=" + offeringId + " AND LOWER(" + Schema.Courses.COL_NAME + ")=LOWER(?)";
        String[] whereArgs = new String[] { name };

        Cursor c = getDB(context).query(Schema.Courses.TABLE_NAME, select, where, whereArgs, null, null, null);

        if (c.moveToFirst())
            course = new Course(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getInt(4));

        c.close();

        return course;
    }
    public static List<Class> loadClasses(Context context)
    {
        List<Class> classes = new ArrayList<>();

        String query = String.format(
            "SELECT %s.%s, %s, " +
                "%s, %s, %s, " +
                "%s.%s, %s, %s, " +
                "%s, %s, %s, %s " +
            "FROM %s, %s " +
            "WHERE %s.%s=%s.%s",
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, Schema.Courses.COL_OFFERING_ID,
            Schema.Courses.COL_CODE, Schema.Courses.COL_NAME, Schema.Courses.COL_COLOR,
            Schema.Classes.TABLE_NAME, Schema.Classes.COL_ID, Schema.Classes.COL_STS_ID, Schema.Classes.COL_TYPE,
            Schema.Classes.COL_DAY, Schema.Classes.COL_START_TIME, Schema.Classes.COL_END_TIME, Schema.Classes.COL_ROOM,
            Schema.Courses.TABLE_NAME, Schema.Classes.TABLE_NAME,
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, Schema.Classes.TABLE_NAME, Schema.Classes.COL_COURSE_ID
        );

        Cursor c = getDB(context).rawQuery(query, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            Course course = new Course(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getInt(4));
            classes.add(new Class(c.getLong(5), c.getString(6), course, c.getString(7), Day.fromId(c.getInt(8)), new TimeSpan(c.getInt(9)), new TimeSpan(c.getInt(10)), c.getString(11)));
        }

        c.close();

        Collections.sort(classes);

        return classes;
    }
    private static List<Class> loadClasses(Context context, long courseId)
    {
        List<Class> classes = new ArrayList<>();

        String query = String.format(
            "SELECT %s.%s, %s, " +
                "%s, %s, %s, " +
                "%s.%s, %s, %s, " +
                "%s, %s, %s, %s " +
            "FROM %s, %s " +
            "WHERE %s.%s=%s.%s AND " +
                "%s.%s=%d",
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, Schema.Courses.COL_OFFERING_ID,
            Schema.Courses.COL_CODE, Schema.Courses.COL_NAME, Schema.Courses.COL_COLOR,
            Schema.Classes.TABLE_NAME, Schema.Classes.COL_ID, Schema.Classes.COL_STS_ID, Schema.Classes.COL_TYPE,
            Schema.Classes.COL_DAY, Schema.Classes.COL_START_TIME, Schema.Classes.COL_END_TIME, Schema.Classes.COL_ROOM,
            Schema.Courses.TABLE_NAME, Schema.Classes.TABLE_NAME,
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, Schema.Classes.TABLE_NAME, Schema.Classes.COL_COURSE_ID,
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, courseId
        );

        Cursor c = getDB(context).rawQuery(query, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            Course course = new Course(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getInt(4));
            classes.add(new Class(c.getLong(5), c.getString(6), course, c.getString(7), Day.fromId(c.getInt(8)), new TimeSpan(c.getInt(9)), new TimeSpan(c.getInt(10)), c.getString(11)));
        }

        c.close();

        Collections.sort(classes);

        return classes;
    }
    public static Class loadClass(Context context, long id)
    {
        Class cls = null;

        String query = String.format(
            "SELECT %s.%s, %s, " +
                "%s, %s, %s, " +
                "%s.%s, %s, %s, " +
                "%s, %s, %s, %s " +
            "FROM %s, %s " +
            "WHERE %s.%s=%s.%s AND " +
                "%s.%s=%d",
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, Schema.Courses.COL_OFFERING_ID,
            Schema.Courses.COL_CODE, Schema.Courses.COL_NAME, Schema.Courses.COL_COLOR,
            Schema.Classes.TABLE_NAME, Schema.Classes.COL_ID, Schema.Classes.COL_STS_ID, Schema.Classes.COL_TYPE,
            Schema.Classes.COL_DAY, Schema.Classes.COL_START_TIME, Schema.Classes.COL_END_TIME, Schema.Classes.COL_ROOM,
            Schema.Courses.TABLE_NAME, Schema.Classes.TABLE_NAME,
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, Schema.Classes.TABLE_NAME, Schema.Classes.COL_COURSE_ID,
            Schema.Classes.TABLE_NAME, Schema.Classes.COL_ID, id
        );

        Cursor c = getDB(context).rawQuery(query, null);

        if (c.moveToFirst())
        {
            Course course = new Course(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getInt(4));
            cls = new Class(c.getLong(5), c.getString(6), course, c.getString(7), Day.fromId(c.getInt(8)), new TimeSpan(c.getInt(9)), new TimeSpan(c.getInt(10)), c.getString(11));
        }

        c.close();

        return cls;
    }
    private static Class loadClass(Context context, String stsId)
    {
        Class cls = null;

        String query = String.format(
            "SELECT %s.%s, %s, " +
                "%s, %s, %s, " +
                "%s.%s, %s, %s, " +
                "%s, %s, %s, %s " +
                "FROM %s, %s " +
                "WHERE %s.%s=%s.%s AND " +
                "%s.%s=?",
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, Schema.Courses.COL_OFFERING_ID,
            Schema.Courses.COL_CODE, Schema.Courses.COL_NAME, Schema.Courses.COL_COLOR,
            Schema.Classes.TABLE_NAME, Schema.Classes.COL_ID, Schema.Classes.COL_STS_ID, Schema.Classes.COL_TYPE,
            Schema.Classes.COL_DAY, Schema.Classes.COL_START_TIME, Schema.Classes.COL_END_TIME, Schema.Classes.COL_ROOM,
            Schema.Courses.TABLE_NAME, Schema.Classes.TABLE_NAME,
            Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID, Schema.Classes.TABLE_NAME, Schema.Classes.COL_COURSE_ID,
            Schema.Classes.TABLE_NAME, Schema.Classes.COL_STS_ID
        );
        String[] queryArgs = new String[] { stsId };

        Cursor c = getDB(context).rawQuery(query, queryArgs);

        if (c.moveToFirst())
        {
            Course course = new Course(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getInt(4));
            cls = new Class(c.getLong(5), c.getString(6), course, c.getString(7), Day.fromId(c.getInt(8)), new TimeSpan(c.getInt(9)), new TimeSpan(c.getInt(10)), c.getString(11));
        }

        c.close();

        return cls;
    }

    private static long insertCourse(Context context, Course course) throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(Schema.Courses.COL_OFFERING_ID, course.offeringId);
        values.put(Schema.Courses.COL_CODE, StringUtils.trimSpaces(course.code));
        values.put(Schema.Courses.COL_NAME, StringUtils.trimSpaces(course.name));
        values.put(Schema.Courses.COL_COLOR, getNextColor(context));

        long id = getDB(context).insert(Schema.Courses.TABLE_NAME, null, values);

        if (id == -1)
            throw new Exception();

        return id;
    }
    private static void insertClass(Context context, long courseId, Class cls) throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(Schema.Classes.COL_COURSE_ID, courseId);
        values.put(Schema.Classes.COL_STS_ID, StringUtils.trimSpaces(cls.stsId));
        values.put(Schema.Classes.COL_TYPE, StringUtils.trimSpaces(cls.type));
        values.put(Schema.Classes.COL_DAY, cls.day.ordinal());
        values.put(Schema.Classes.COL_START_TIME, cls.start.totalMinutes);
        values.put(Schema.Classes.COL_END_TIME, cls.end.totalMinutes);
        values.put(Schema.Classes.COL_ROOM, StringUtils.trimSpaces(cls.room));

        if (getDB(context).insert(Schema.Classes.TABLE_NAME, null, values) == -1)
            throw new Exception();
    }

    private static void updateClass(Context context, Class cls) throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(Schema.Classes.COL_STS_ID, StringUtils.trimSpaces(cls.stsId));
        values.put(Schema.Classes.COL_TYPE, StringUtils.trimSpaces(cls.type));
        values.put(Schema.Classes.COL_DAY, cls.day.ordinal());
        values.put(Schema.Classes.COL_START_TIME, cls.start.totalMinutes);
        values.put(Schema.Classes.COL_END_TIME, cls.end.totalMinutes);
        values.put(Schema.Classes.COL_ROOM, StringUtils.trimSpaces(cls.room));

        String where = Schema.Classes.COL_ID + "=" + cls.id;
        if (getDB(context).update(Schema.Classes.TABLE_NAME, values, where, null) == 0)
            throw new Exception();
    }

    private static void deleteCourse(Context context, Course course)
    {
        String where;

        // delete using offeringId if STS course
        if (course.isFromSTS())
            where = Schema.Courses.COL_OFFERING_ID + "=" + course.offeringId;
        else
            where = Schema.Courses.COL_ID + "=" + course.id;

        getDB(context).delete(Schema.Courses.TABLE_NAME, where, null);
    }
    private static void deleteClass(Context context, Class cls)
    {
        // delete using stsId if STS class
        if (cls.isFromSTS())
        {
            String where = Schema.Classes.COL_STS_ID + "=?";
            String[] whereArgs = new String[] { cls.stsId };
            getDB(context).delete(Schema.Classes.TABLE_NAME, where, whereArgs);
        }
        else
        {
            String where = Schema.Classes.COL_ID + "=" + cls.id;
            getDB(context).delete(Schema.Classes.TABLE_NAME, where, null);
        }
    }

    //------------------------------------------------------------------------- util functions
    private static int getNextColor(Context context)
    {
        String[] hexColors = context.getResources().getStringArray(R.array.courses);
        List<Course> courses = loadCourses(context);

        // get first unused color
        for (String hexColor : hexColors)
        {
            int color = Color.parseColor(hexColor);
            if (!isColorUsed(courses, color))
                return color;
        }

        // random color if all used
        return Color.parseColor(hexColors[MathUtils.rand(0, hexColors.length - 1)]);
    }
    private static boolean isColorUsed(List<Course> courses, int color)
    {
        for (Course course : courses)
            if (course.color == color)
                return true;
        return false;
    }

    public static boolean isCourseValid(Course course)
    {
        return !StringUtils.trimSpaces(course.name).isEmpty();
    }
    public static boolean isClassValid(Class cls)
    {
        if (cls.start.compareTo(cls.end) > 0)
            return false;
        return cls.start.totalMinutes >= 0 && cls.end.totalMinutes < 1440;
    }
}
