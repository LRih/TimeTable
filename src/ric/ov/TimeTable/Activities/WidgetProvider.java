package ric.ov.TimeTable.Activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.SQL;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Utils.TimeSpan;
import ric.ov.TimeTable.Utils.TimeUtils;

import java.util.List;

public final class WidgetProvider extends AppWidgetProvider
{
    public final void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        TimeUtils.updateDayTime();
        List<Class> classes = SQL.loadClasses(context);

        for (int id : appWidgetIds)
        {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            Class next = getNextClass(classes);
            Class last = getLastClass(classes, next);

            if (last != null && last.isClash(TimeUtils.today(), TimeUtils.now()))
                setClass(context, views, last, true);
            else
                setClass(context, views, next, false);

            appWidgetManager.updateAppWidget(id, views);
        }
    }

    private void setClass(Context context, RemoteViews views, Class cls, boolean showPast)
    {
        views.setTextViewText(R.id.lblStart, cls.start.toTimeString());
        views.setTextViewText(R.id.lblEnd, cls.end.toTimeString());

        views.setInt(R.id.viewColor, "setBackgroundColor", cls.course.color);

        views.setTextViewText(R.id.lblCourse, cls.course.name);
        views.setTextViewText(R.id.lblSubtitle, getSubtitle(cls));

        if (showPast)
        {
            TimeSpan time = TimeUtils.getDifference(cls.day, cls.start, TimeUtils.today(), TimeUtils.now());
            views.setTextViewText(R.id.lblNextTimeMessage, context.getString(R.string.started_x_ago, getVerboseTimeString(context, time)));
        }
        else
        {
            TimeSpan time = TimeUtils.getDifference(TimeUtils.today(), TimeUtils.now(), cls.day, cls.start);
            views.setTextViewText(R.id.lblNextTimeMessage, context.getString(R.string.starts_in_x, getVerboseTimeString(context, time)));
        }
    }

    /* from class view */
    private String getSubtitle(Class cls)
    {
        if (!cls.type.isEmpty() && !cls.room.isEmpty())
            return cls.type + " (" + cls.room + ")";
        else if (!cls.type.isEmpty())
            return cls.type;
        return cls.room;
    }

    /* from next view */
    private String getVerboseTimeString(Context context, TimeSpan ts)
    {
        if (ts.totalMinutes < 0)
            throw new RuntimeException("Invalid time: " + ts.totalMinutes);

        if (ts.day() != 0)
        {
            int decimal = (ts.hour() * 10) / 24;

            if (ts.day() == 1 && decimal > 0)
                return String.format("%d.%d %s", ts.day(), decimal, context.getResources().getQuantityString(R.plurals.days, 2)); // always plural
            return String.format("%d %s", ts.day(), context.getResources().getQuantityString(R.plurals.days, ts.day()));
        }
        else if (ts.hour() != 0)
        {
            int decimal = (ts.minute() * 10) / 60;

            if (ts.hour() == 1 && decimal > 0)
                return String.format("%d.%d %s", ts.hour(), decimal, context.getResources().getQuantityString(R.plurals.hours, 2)); // always plural
            return String.format("%d %s", ts.hour(), context.getResources().getQuantityString(R.plurals.hours, ts.hour()));
        }

        return String.format("%d %s", ts.minute(), context.getResources().getQuantityString(R.plurals.minutes, ts.minute()));
    }

    /* from main activity */
    private Class getLastClass(List<Class> classes, Class from)
    {
        for (int i = 0; i < classes.size(); i++)
        {
            if (classes.get(i) == from)
            {
                // wrap around to last class if index is zero
                int index = (i - 1 + classes.size()) % classes.size();
                return classes.get(index);
            }
        }

        return null;
    }
    private Class getNextClass(List<Class> classes)
    {
        Day today = TimeUtils.today();
        TimeSpan now = TimeUtils.now();

        // find after current today/time
        for (Class c : classes)
            if (c.day != today || c.start.compareTo(now) > 0)
                return c;

        // wrap around to first class of the day
        if (!classes.isEmpty())
            return classes.get(0);

        return null;
    }
}