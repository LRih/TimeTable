package ric.ov.TimeTable.Views.WeekViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ric.ov.TimeTable.Data.UIClass;

import java.util.List;

public final class WeekListView extends ListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    //========================================================================= VARIABLES
    private WeekListAdapter _adapter;

    private OnClassClickListener _listener;

    //========================================================================= INITIALIZE
    public WeekListView(Context context)
    {
        super(context);
        initialize();
    }
    public WeekListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    private void initialize()
    {
        _adapter = new WeekListAdapter();
        setAdapter(_adapter);

        setDivider(null);
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    //========================================================================= PROPERTIES
    public final void setClasses(List<UIClass> classes)
    {
        _adapter.setClasses(classes);
    }

    public final void setOnClassClickListener(OnClassClickListener listener)
    {
        _listener = listener;
    }

    //========================================================================= EVENTS
    public final void onItemClick(AdapterView<?> parent, View view, int index, long id)
    {
        if (_listener != null)
            _listener.onClick(((UIClass)_adapter.getItem(index)).cls);
    }

    public final boolean onItemLongClick(AdapterView<?> parent, View view, int index, long id)
    {
        if (_listener != null)
            _listener.onLongClick(((UIClass)_adapter.getItem(index)).cls);
        return true;
    }
}
