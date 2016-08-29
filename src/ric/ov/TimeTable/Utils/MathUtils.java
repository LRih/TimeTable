package ric.ov.TimeTable.Utils;

public final class MathUtils
{
    private MathUtils()
    {
        throw new AssertionError();
    }

    public static int rand(int min, int max)
    {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}
