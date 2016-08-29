package ric.ov.TimeTable.STS;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.Classes;
import ric.ov.TimeTable.Data.Course;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Utils.StringUtils;
import ric.ov.TimeTable.Utils.TimeSpan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class STSManager
{
    //========================================================================= VARIABLES
    public static final String URL_WEEKLY_VIEW = "https://sts.rmit.edu.au/STS/";
    private static final String URL_STS = "https://sts.rmit.edu.au/STS-ReadOnly/ro_index.jsp";
    private static final String URL_SCHOOL = "https://sts.rmit.edu.au/STS-ReadOnly/ro_courses.jsp";
    private static final String URL_COURSE = "https://sts.rmit.edu.au/STS-ReadOnly/results.jsp";

    public static final String SEMESTER_2_2016 = "1650";

    //========================================================================= INITIALIZE
    private STSManager()
    {
        throw new AssertionError();
    }

    //========================================================================= FUNCTIONS
    public static List<School> getSchools() throws IOException
    {
        List<School> schools = new ArrayList<>();

        Document doc = Jsoup.connect(URL_STS).get();

        Element list = doc.getElementById("acad_org");
        for (Element e : list.children())
            schools.add(new School(e.val(), e.text()));

        Collections.sort(schools);

        return schools;
    }

    public static List<Course> getCourses(String acadOrg, String semester) throws IOException
    {
        List<Course> courses = new ArrayList<>();

        Document doc = getCoursesDocument(acadOrg, semester);
        Element list = doc.getElementById("offering");

        for (Element e : list.children())
        {
            long offeringId = Long.parseLong(e.val());
            String code = StringUtils.getRegexString(e.text(), ".+?(?= :)").replace(" ", "");
            String name = StringUtils.getRegexString(e.text(), "(?<=: ).+").replaceAll("- (\\*{2}|\\+{2})", "");

            courses.add(Course.createSTS(offeringId, code, name));
        }

        Collections.sort(courses);

        return courses;
    }

    public static Classes getClasses(long offeringId) throws IOException
    {
        Document doc = Jsoup.connect(URL_COURSE)
            .data("OFFERING_ID", String.valueOf(offeringId))
            .post();

        List<Class> classes = new ArrayList<>();
        Course course = getCourse(doc, offeringId);

        Elements rows = doc.getElementsByTag("tr");

        for (Element row : rows)
        {
            try
            {
                Elements columns = row.getElementsByTag("td");

                // header row
                if (columns.isEmpty())
                    continue;

                String stsId = columns.get(4).text();

                String type = columns.get(3).text();
                String room = columns.get(5).text().replaceAll("\\A0+", "");
                Day day = Day.valueOf(columns.get(6).text());

                String[] rawStart = columns.get(7).text().split("\\.");
                int startHr = Integer.parseInt(rawStart[0]);
                int startMin = Integer.parseInt(rawStart[1]);
                TimeSpan start = new TimeSpan(startHr * 60 + startMin);

                String[] rawEnd = columns.get(8).text().split("\\.");
                int endHr = Integer.parseInt(rawEnd[0]);
                int endMin = Integer.parseInt(rawEnd[1]);
                TimeSpan end = new TimeSpan(endHr * 60 + endMin);

                classes.add(Class.createSTS(stsId, course, type, day, start, end, room));
            }
            catch (Exception ex)
            {
                Log.i("TT-STS", "Error loading class for offeringId: " + offeringId);
            }
        }

        Collections.sort(classes, new ClassCompare());

        return new Classes(course, classes);
    }
    private static Course getCourse(Document classesDoc, long offeringId)
    {
        Matcher m = Pattern.compile("Timetable options for '(.+?) - (.+)'")
            .matcher(classesDoc.getElementsByTag("h1").get(0).text());

        if (!m.find())
            throw new RuntimeException("Course regex not found for offeringId: " + offeringId);

        String courseCode = m.group(1).replace(" ", "");
        String courseName = m.group(2);

        return Course.createSTS(offeringId, courseCode, courseName);
    }


    public static Document getCoursesDocument(String acadOrg, String semester) throws IOException
    {
        return Jsoup.connect(URL_SCHOOL)
            .data("ACAD_ORG", acadOrg)
            .data("SEMESTER", semester)
            .post();
    }

    public static String[] getAcadOrgs(String courseCode)
    {
        courseCode = courseCode.substring(0, 4);

        switch (courseCode)
        {
            case "ACCT": return new String[] { "615H", "650T" };
            case "AERO": return new String[] { "115H", "130T" };
            case "AERS": return new String[] { "155T" };
            case "ARCH": return new String[] { "320H", "320T", "365H" };
            case "AUTO": return new String[] { "115H" };
            case "BAFI": return new String[] { "615H", "625H" };
            case "BESC": return new String[] { "150H", "155T" };
            case "BIOL": return new String[] { "135H", "150H", "160H", "155T" };
            case "BUIL": return new String[] { "325H" };
            case "BUSM": return new String[] { "615H", "115H", "620H", "625H", "360H", "350H", "660H", "630H", "160H", "325H", "650T", "155T" };
            case "CHEM": return new String[] { "135H", "160H", "155T" };
            case "CIVE": return new String[] { "120H", "130T" };
            case "COMM": return new String[] { "340H", "620H", "345H", "345T", "155T" };
            case "COSC": return new String[] { "135H", "140H", "345H", "155T" };
            case "COTH": return new String[] { "150H" };
            case "CUED": return new String[] { "360H" };
            case "EASC": return new String[] { "120H", "130T" };
            case "ECON": return new String[] { "615H", "625H", "650T" };
            case "EEET": return new String[] { "125H", "130T" };
            case "ENVI": return new String[] { "135H", "120H", "365H", "145H" };
            case "EXTL": return new String[] { "615H", "115H", "135H", "320H", "340H", "620H", "120H", "140H", "625H", "360H", "125H", "350H", "365H", "150H", "630H", "145H", "345H", "325H" };
            case "GEOM": return new String[] { "145H" };
            case "GRAP": return new String[] { "320H", "320T", "350H", "350T", "345H" };
            case "HUSO": return new String[] { "340H", "365H" };
            case "HWSS": return new String[] { "360H", "365H" };
            case "INTE": return new String[] { "320H", "620H", "140H", "365H", "145H", "155T" };
            case "ISYS": return new String[] { "620H", "140H", "650T", "155T" };
            case "JUST": return new String[] { "365H", "325H" };
            case "LANG": return new String[] { "360H", "365H", "345H" };
            case "LAW1": return new String[] { "660H" };
            case "LAW2": return new String[] { "615H", "660H", "650T" };
            case "LIBR": return new String[] { "620H" };
            case "MANU": return new String[] { "115H", "350H", "130T" };
            case "MATH": return new String[] { "150H", "145H", "130T", "155T" };
            case "MEDS": return new String[] { "150H", "160H" };
            case "MIET": return new String[] { "115H", "130T" };
            case "MKTG": return new String[] { "625H", "350H", "660H", "345H", "325H", "650T" };
            case "NURS": return new String[] { "150H" };
            case "OART": return new String[] { "320H", "345H" };
            case "OENG": return new String[] { "115H", "120H", "125H", "130T" };
            case "OHTH": return new String[] { "135H", "150H", "160H", "155T" };
            case "OMGT": return new String[] { "620H", "350H", "630H", "325H", "650T" };
            case "ONPS": return new String[] { "135H", "125H", "145H", "160H", "155T" };
            case "OTED": return new String[] { "360H" };
            case "PERF": return new String[] { "320H", "340H", "345H" };
            case "PHAR": return new String[] { "160H" };
            case "PHIL": return new String[] { "325H" };
            case "PHYS": return new String[] { "135H" };
            case "POLI": return new String[] { "365H" };
            case "PROC": return new String[] { "120H", "130T" };
            case "PUBH": return new String[] { "135H", "150H", "160H", "155T" };
            case "RADI": return new String[] { "160H" };
            case "REHA": return new String[] { "150H" };
            case "SOCU": return new String[] { "320H", "360H", "365H" };
            case "TCHE": return new String[] { "360H", "160H" };
            case "VART": return new String[] { "340H", "345H" };
        }

        return new String[] { };
    }

    //========================================================================= CLASSES
    private static class ClassCompare implements Comparator<Class>
    {
        public final int compare(Class c1, Class c2)
        {
            int typeCmp = c1.type.compareToIgnoreCase(c2.type);
            if (typeCmp != 0) return typeCmp;

            int dayCmp = c1.day.compareTo(c2.day);
            if (dayCmp != 0) return dayCmp;

            int startCmp = c1.start.compareTo(c2.start);
            if (startCmp != 0) return startCmp;

            int endCmp = c1.end.compareTo(c2.end);
            if (endCmp != 0) return endCmp;

            return c1.room.compareToIgnoreCase(c2.room);
        }
    }
}
