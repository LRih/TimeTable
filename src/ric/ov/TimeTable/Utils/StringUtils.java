package ric.ov.TimeTable.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils
{
    private StringUtils()
    {
        throw new AssertionError();
    }

    public static String trimSpaces(String str)
    {
        return str.trim().replaceAll("\\s+", " ");
    }

    public static String getRegexString(String input, String pattern)
    {
        Matcher matcher = Pattern.compile(pattern).matcher(input);
        return matcher.find() ? matcher.group() : "";
    }
}
