package ric.ov.TimeTable.STS;

import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Utils.Task;

import java.io.IOException;
import java.util.List;

public final class ImportTask extends Task<List<Class>>
{
    //========================================================================= VARIABLES
    private final String _data;

    //========================================================================= INITIALIZE
    public ImportTask(String data)
    {
        _data = data;
    }

    //========================================================================= FUNCTIONS
    protected final List<Class> doInBackground() throws IOException
    {
        return new ImportManager().parsePrintPreview(_data);
    }
}
