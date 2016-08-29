package ric.ov.TimeTable.STS;

public final class School implements Comparable<School>
{
    //========================================================================= VARIABLES
    public final String acadOrg;
    public final String name;

    //========================================================================= INITIALIZE
    public School(String acadOrg, String name)
    {
        this.acadOrg = acadOrg;
        this.name = name;
    }

    //========================================================================= FUNCTIONS
    public final int compareTo(School s)
    {
       return name.compareToIgnoreCase(s.name);
    }
}
