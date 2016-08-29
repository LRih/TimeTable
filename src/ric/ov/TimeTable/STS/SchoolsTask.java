package ric.ov.TimeTable.STS;

import ric.ov.TimeTable.Utils.Task;

import java.io.IOException;
import java.util.List;

public final class SchoolsTask extends Task<List<School>>
{
    //========================================================================= FUNCTIONS
    protected final List<School> doInBackground() throws IOException
    {
        return STSManager.getSchools();
    }
}
