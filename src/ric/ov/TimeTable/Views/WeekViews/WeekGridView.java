package ric.ov.TimeTable.Views.WeekViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Utils.TimeUtils;
import ric.ov.TimeTable.Views.ClassView;

import java.util.List;

public final class WeekGridView extends LinearLayout implements TableView.OnClassStateChangeListener
{
    //========================================================================= VARIABLES
    private TableView _tableView;
    private View _layClass;
    private ClassView _classView;

    private OnClassClickListener _listener;

    //========================================================================= INITIALIZE
    public WeekGridView(Context context)
    {
        super(context);
        initialize(context);
    }
    public WeekGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_week_grid, this, true);

        _tableView = (TableView)findViewById(R.id.tableView);
        _tableView.setOnClassStateChangeListener(this);

        _layClass = findViewById(R.id.layClass);
        _classView = (ClassView)findViewById(R.id.classView);
    }

    //========================================================================= FUNCTIONS
    private View.OnClickListener createClickListener(final Class c)
    {
        return new OnClickListener()
        {
            public final void onClick(View v)
            {
                if (_listener != null && c != null)
                    _listener.onClick(c);
            }
        };
    }
    private View.OnLongClickListener createLongClickListener(final Class c)
    {
        return new OnLongClickListener()
        {
            public final boolean onLongClick(View v)
            {
                if (_listener != null && c != null)
                    _listener.onLongClick(c);
                return true;
            }
        };
    }

    //========================================================================= PROPERTIES
    public final void setClasses(List<UIClass> classes)
    {
        _tableView.setClasses(classes, TimeUtils.today(), TimeUtils.now());
    }

    public final TableView tableView()
    {
        return _tableView;
    }

    public final void setOnClassClickListener(OnClassClickListener listener)
    {
        _listener = listener;
    }

    //========================================================================= EVENTS
    public final void onSelected(UIClass c, boolean isTop)
    {
        int gravity = isTop ? Gravity.BOTTOM : Gravity.TOP;
        _layClass.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, gravity));
        _layClass.setVisibility(VISIBLE);

        _classView.setClass(c);
        _classView.setOnClickListener(createClickListener(c.cls));
        _classView.setOnLongClickListener(createLongClickListener(c.cls));
    }
    public final void onDeselected()
    {
        _layClass.setVisibility(GONE);
        _classView.setClass(null);
    }
}
