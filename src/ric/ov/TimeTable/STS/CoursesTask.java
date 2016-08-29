package ric.ov.TimeTable.STS;

import ric.ov.TimeTable.Data.Course;
import ric.ov.TimeTable.Utils.Task;

import java.io.IOException;
import java.util.List;

public final class CoursesTask extends Task<List<Course>>
{
    //========================================================================= VARIABLES
    private final String _acadOrg;
    private final String _semester;

    //========================================================================= INITIALIZE
    public CoursesTask(String acadOrg, String semester)
    {
        _acadOrg = acadOrg;
        _semester = semester;
    }

    //========================================================================= FUNCTIONS
    protected final List<Course> doInBackground() throws IOException
    {
        return STSManager.getCourses(_acadOrg, _semester);
    }
}
