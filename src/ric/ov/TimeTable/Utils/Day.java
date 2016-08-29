package ric.ov.TimeTable.Utils;

public enum Day
{
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;

    public static Day fromId(int id)
    {
        switch (id)
        {
            case 0: return Monday;
            case 1: return Tuesday;
            case 2: return Wednesday;
            case 3: return Thursday;
            case 4: return Friday;
            case 5: return Saturday;
            case 6: return Sunday;
        }

        throw new RuntimeException("Invalid id: " + id);
    }

    public final String abbreviation()
    {
        return Day.fromId(ordinal()).name().substring(0, 1);
    }
}
