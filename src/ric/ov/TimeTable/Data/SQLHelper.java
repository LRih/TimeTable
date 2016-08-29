package ric.ov.TimeTable.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class SQLHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "timetable.db";


    public SQLHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public final void onCreate(SQLiteDatabase db)
    {
        // courses table
        String query = String.format(
            "CREATE TABLE %s " +
            "(%s INTEGER PRIMARY KEY, %s, " +
            "%s, %s, %s)",
            Schema.Courses.TABLE_NAME,
            Schema.Courses.COL_ID, Schema.Courses.COL_OFFERING_ID,
            Schema.Courses.COL_CODE, Schema.Courses.COL_NAME, Schema.Courses.COL_COLOR
        );

        db.execSQL(query);


        // items table
        query = String.format(
            "CREATE TABLE %s " +
            "(%s INTEGER PRIMARY KEY, %s, %s, %s, " +
            "%s, %s, %s, %s, " +
            "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE)",
            Schema.Classes.TABLE_NAME,
            Schema.Classes.COL_ID, Schema.Classes.COL_COURSE_ID, Schema.Classes.COL_STS_ID, Schema.Classes.COL_TYPE,
            Schema.Classes.COL_DAY, Schema.Classes.COL_START_TIME, Schema.Classes.COL_END_TIME, Schema.Classes.COL_ROOM,
            Schema.Classes.COL_COURSE_ID, Schema.Courses.TABLE_NAME, Schema.Courses.COL_ID
        );

        db.execSQL(query);
    }

    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + Schema.Courses.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Schema.Classes.TABLE_NAME);

        onCreate(db);
    }

    public final void onOpen(SQLiteDatabase db)
    {
        // enforce foreign key constraints
        if (!db.isReadOnly())
            db.execSQL("PRAGMA foreign_keys=ON;");
    }
}
