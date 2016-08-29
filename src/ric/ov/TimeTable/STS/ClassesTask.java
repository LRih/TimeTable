package ric.ov.TimeTable.STS;

import ric.ov.TimeTable.Data.Classes;
import ric.ov.TimeTable.Utils.Task;

public final class ClassesTask extends Task<Classes>
{
    //========================================================================= VARIABLES
    private final long _offeringId;

    //========================================================================= INITIALIZE
    public ClassesTask(long offeringId)
    {
        _offeringId = offeringId;
    }

    //========================================================================= FUNCTIONS
    protected final Classes doInBackground() throws Exception
    {
        return STSManager.getClasses(_offeringId);
    }
}
