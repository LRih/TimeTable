package ric.ov.TimeTable.Data;

/*
    Class wrapper for storing UI state data.
 */
public final class UIClass
{
    //========================================================================= VARIABLES
    public final Class cls;
    public final boolean isClash;
    public final boolean isSelected;

    //========================================================================= INITIALIZE
    public UIClass(Class cls, boolean isClash, boolean isSelected)
    {
        this.cls = cls;
        this.isClash = isClash;
        this.isSelected = isSelected;
    }
}
