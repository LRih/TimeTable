package ric.ov.TimeTable.Views.WeekViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Utils.LoadableAdapter;
import ric.ov.TimeTable.Utils.TimeUtils;
import ric.ov.TimeTable.Views.ClassView;

import java.util.ArrayList;
import java.util.List;

public final class WeekListAdapter extends LoadableAdapter<Object>
{
    //========================================================================= VARIABLES
    private static final int TYPE_DAY = 0;
    private static final int TYPE_CLASS = 1;
    private static final int TYPE_BREAK = 2;

    //========================================================================= FUNCTIONS
    public final View getView(int index, View convertView, ViewGroup parent)
    {
        int type = getItemViewType(index);

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch (type)
            {
                case TYPE_DAY: convertView = inflater.inflate(R.layout.item_header, parent, false); break;
                case TYPE_CLASS: convertView = inflater.inflate(R.layout.item_class, parent, false); break;
                case TYPE_BREAK: convertView = inflater.inflate(R.layout.item_break, parent, false); break;
            }
        }

        switch (type)
        {
            case TYPE_DAY: initializeDay(convertView, (Day)getItem(index)); break;
            case TYPE_CLASS: initializeClass(convertView, (UIClass)getItem(index)); break;
            case TYPE_BREAK: initializeBreak(convertView, (int)getItem(index)); break;
        }

        return convertView;
    }

    private void initializeDay(View view, Day day)
    {
        TextView lblDay = (TextView)view.findViewById(R.id.text);
        lblDay.setText(day == TimeUtils.today() ? day.name().toUpperCase() : day.name());
    }
    private void initializeClass(View view, UIClass c)
    {
        ((ClassView)view).setClass(c);
    }
    private void initializeBreak(View view, int hours)
    {
        ((TextView)view).setText(view.getContext().getString(R.string.x_hour_break, hours));
    }

    //========================================================================= PROPERTIES
    public final void setClasses(List<UIClass> classes)
    {
        List<Object> items = new ArrayList<>();

        Day day = null;
        for (int i = 0; i < classes.size(); i++)
        {
            UIClass c = classes.get(i);

            // add today header
            if (day == null || day != c.cls.day)
            {
                day = c.cls.day;
                items.add(day);
            }
            else if (classes.get(i - 1).cls.end.compareTo(c.cls.start) < 0) // add break
                items.add(c.cls.start.sub(classes.get(i - 1).cls.end).hour());

            // add class
            items.add(c);
        }

        setItems(items);
    }

    public final boolean areAllItemsEnabled()
    {
        return false;
    }
    public final boolean isEnabled(int index)
    {
        return getItemViewType(index) == TYPE_CLASS;
    }

    public final int getItemViewType(int index)
    {
        if (getItem(index) instanceof UIClass)
            return TYPE_CLASS;
        else if (getItem(index) instanceof Day)
            return TYPE_DAY;
        return TYPE_BREAK;
    }
    public final int getViewTypeCount()
    {
        return 3;
    }
}
