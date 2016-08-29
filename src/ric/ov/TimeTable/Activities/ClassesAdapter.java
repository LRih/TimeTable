package ric.ov.TimeTable.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.Utils.LoadableAdapter;

import java.util.ArrayList;
import java.util.List;

public final class ClassesAdapter extends LoadableAdapter<Object>
{
    //========================================================================= VARIABLES
    private static final int TYPE_TYPE = 0;
    private static final int TYPE_CLASS = 1;

    //========================================================================= FUNCTIONS
    public final View getView(int index, View convertView, ViewGroup parent)
    {
        int type = getItemViewType(index);

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch (type)
            {
                case TYPE_TYPE: convertView = inflater.inflate(R.layout.item_header, parent, false); break;
                case TYPE_CLASS: convertView = inflater.inflate(R.layout.item_sts_class, parent, false); break;
            }
        }

        switch (type)
        {
            case TYPE_TYPE: initializeType(convertView, (String)getItem(index)); break;
            case TYPE_CLASS: initializeClass(convertView, (UIClass)getItem(index)); break;
        }

        return convertView;
    }

    private void initializeType(View view, String type)
    {
        TextView lblType = (TextView)view.findViewById(R.id.text);
        lblType.setText(type);
    }
    private void initializeClass(View view, UIClass cls)
    {
        CheckBox chkSelected = (CheckBox)view.findViewById(R.id.chkSelected);

        TextView lblStart = (TextView)view.findViewById(R.id.lblStart);
        TextView lblEnd = (TextView)view.findViewById(R.id.lblEnd);

        View viewColor = view.findViewById(R.id.viewColor);

        TextView lblDay = (TextView)view.findViewById(R.id.lblDay);
        TextView lblRoom = (TextView)view.findViewById(R.id.lblRoom);

        View lblClash = view.findViewById(R.id.lblClash);

        // to prevent consuming list item click and to disable toggle on click
        chkSelected.setFocusable(false);
        chkSelected.setClickable(false);
        chkSelected.setChecked(cls.isSelected);

        lblStart.setText(cls.cls.start.toTimeString());
        lblEnd.setText(cls.cls.end.toTimeString());

        viewColor.setBackgroundColor(cls.cls.course.color);

        lblDay.setText(cls.cls.day.name());
        lblRoom.setText(cls.cls.room);

        lblClash.setVisibility(cls.isClash ? View.VISIBLE : View.GONE);
    }

    //========================================================================= PROPERTIES
    public final void setClasses(List<UIClass> classes)
    {
        List<Object> items = new ArrayList<>();

        String name = null;
        for (UIClass cls : classes)
        {
            // add name header
            if (name == null || !name.equals(cls.cls.type))
            {
                name = cls.cls.type;
                items.add(name);
            }

            // add class
            items.add(cls);
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
        return TYPE_TYPE;
    }
    public final int getViewTypeCount()
    {
        return 2;
    }
}
