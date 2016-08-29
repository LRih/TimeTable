package ric.ov.TimeTable.Data;

public final class Schema
{
    private Schema()
    {
        throw new AssertionError();
    }


    public static abstract class Courses
    {
        public static final String TABLE_NAME = "courses";
        public static final String COL_ID = "id";
        public static final String COL_OFFERING_ID = "offering_id";

        public static final String COL_CODE = "code";
        public static final String COL_NAME = "name";
        public static final String COL_COLOR = "color";
    }

    public static abstract class Classes
    {
        public static final String TABLE_NAME = "classes";
        public static final String COL_ID = "id";
        public static final String COL_COURSE_ID = "course_id";
        public static final String COL_STS_ID = "sts_id";

        public static final String COL_TYPE = "type";
        public static final String COL_DAY = "day";
        public static final String COL_START_TIME = "start";
        public static final String COL_END_TIME = "end";
        public static final String COL_ROOM = "room";
    }
}
