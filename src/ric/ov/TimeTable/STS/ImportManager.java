package ric.ov.TimeTable.STS;

import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.Course;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Utils.TimeSpan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImportManager
{
    //========================================================================= VARIABLES
    private HashMap<SourceKey, String> _sources = new HashMap<>();

    //========================================================================= FUNCTIONS
    public final List<Class> parsePrintPreview(String data) throws IOException
    {
        List<Class> classes = new ArrayList<>();

        Matcher m = Pattern.compile(
            "(\\d+)\t*" +
            "(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)\t*" +
            "(.+?)\t*" +
            "(\\d{3}\\.\\d{2}\\.\\d{3})\t*" +
            "(.+?)\t*" +
            "((.{4})-(\\d{4})-.+?-.+?-.+?)\t*" +
            "(\\d{2}\\.\\d{2})\t*" +
            "(\\d{2}\\.\\d{2})"
        ).matcher(data);

        while (m.find())
        {
            String semester = m.group(1);
            Day day = Day.valueOf(m.group(2));
            String courseName = m.group(3);
            String room = m.group(4);
            String type = m.group(5);
            String stsId = m.group(6);
            String courseCode = m.group(7) + m.group(8);

            String[] rawStart = m.group(9).split("\\.");
            int startHr = Integer.parseInt(rawStart[0]);
            int startMin = Integer.parseInt(rawStart[1]);
            TimeSpan start = new TimeSpan(startHr * 60 + startMin);

            String[] rawEnd = m.group(10).split("\\.");
            int endHr = Integer.parseInt(rawEnd[0]);
            int endMin = Integer.parseInt(rawEnd[1]);
            TimeSpan end = new TimeSpan(endHr * 60 + endMin);

            long offeringId = getOfferingId(courseCode, semester);

            // don't link to STS if course is not found
            if (offeringId != -1)
            {
                Course course = Course.createSTS(offeringId, courseCode, courseName);
                classes.add(Class.createSTS(stsId, course, type, day, start, end, room));
            }
            else
            {
                Course course = Course.createNonSTS(courseName);
                classes.add(Class.createNonSTS(course, type, day, start, end, room));
            }
        }

        return classes;
    }

    private long getOfferingId(String courseCode, String semester) throws IOException
    {
        for (String acadOrg : STSManager.getAcadOrgs(courseCode))
        {
            long offeringId = getOfferingId(courseCode, acadOrg, semester);
            if (offeringId != -1)
                return offeringId;
        }

        return -1;
    }
    private long getOfferingId(String courseCode, String acadOrg, String semester) throws IOException
    {
        courseCode = courseCode.substring(0, 4) + " " + courseCode.substring(4);

        // cache source
        SourceKey key = new SourceKey(acadOrg, semester);
        if (!_sources.containsKey(key))
            _sources.put(key, STSManager.getCoursesDocument(key.acadOrg, key.semester).html());

        Matcher m = Pattern.compile("\"(\\d+)\">" + courseCode + " :").matcher(_sources.get(key));

        return m.find() ? Long.parseLong(m.group(1)) : -1;
    }

    //========================================================================= CLASSES
    private class SourceKey
    {
        public final String acadOrg;
        public final String semester;

        private SourceKey(String acadOrg, String semester)
        {
            this.acadOrg = acadOrg;
            this.semester = semester;
        }

        public final boolean equals(Object o)
        {
            if (!(o instanceof SourceKey))
                return false;

            SourceKey s = (SourceKey)o;

            return acadOrg.equals(s.acadOrg) && semester.equals(s.semester);
        }

        public final int hashCode()
        {
            return (acadOrg + "-" + semester).hashCode();
        }
    }
}
