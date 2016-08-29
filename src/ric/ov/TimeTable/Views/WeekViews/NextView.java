package ric.ov.TimeTable.Views.WeekViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Utils.TimeSpan;
import ric.ov.TimeTable.Utils.TimeUtils;
import ric.ov.TimeTable.Views.ClassView;

public final class NextView extends LinearLayout
{
    //========================================================================= VARIABLES
    private View _divider;
    private View _layLast;

    private ClassView _nextClass;
    private ClassView _lastClass;

    private TextView _lblNextTimeMessage;
    private TextView _lblLastTimeMessage;

    private OnClassClickListener _listener;

    //========================================================================= INITIALIZE
    public NextView(Context context)
    {
        super(context);
        initialize(context);
    }
    public NextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_next, this, true);

        _divider = findViewById(R.id.divider);
        _layLast = findViewById(R.id.layLast);

        _nextClass = (ClassView)findViewById(R.id.nextClass);
        _lastClass = (ClassView)findViewById(R.id.lastClass);

        _lblNextTimeMessage = (TextView)findViewById(R.id.lblNextTimeMessage);
        _lblLastTimeMessage = (TextView)findViewById(R.id.lblLastTimeMessage);
    }

    //========================================================================= FUNCTIONS
    private View.OnClickListener createClickListener(final Class cls)
    {
        return new OnClickListener()
        {
            public final void onClick(View v)
            {
                if (_listener != null && cls != null)
                    _listener.onClick(cls);
            }
        };
    }
    private View.OnLongClickListener createLongClickListener(final Class cls)
    {
        return new OnLongClickListener()
        {
            public final boolean onLongClick(View v)
            {
                if (_listener != null && cls != null)
                    _listener.onLongClick(cls);
                return true;
            }
        };
    }

    private String getVerboseTimeString(TimeSpan ts)
    {
        if (ts.totalMinutes < 0)
            throw new RuntimeException("Invalid time: " + ts.totalMinutes);

        if (ts.day() != 0)
        {
            int decimal = (ts.hour() * 10) / 24;

            if (ts.day() == 1 && decimal > 0)
                return String.format("%d.%d %s", ts.day(), decimal, getResources().getQuantityString(R.plurals.days, 2)); // always plural
            return String.format("%d %s", ts.day(), getResources().getQuantityString(R.plurals.days, ts.day()));
        }
        else if (ts.hour() != 0)
        {
            int decimal = (ts.minute() * 10) / 60;

            if (ts.hour() == 1 && decimal > 0)
                return String.format("%d.%d %s", ts.hour(), decimal, getResources().getQuantityString(R.plurals.hours, 2)); // always plural
            return String.format("%d %s", ts.hour(), getResources().getQuantityString(R.plurals.hours, ts.hour()));
        }

        return String.format("%d %s", ts.minute(), getResources().getQuantityString(R.plurals.minutes, ts.minute()));
    }

    //========================================================================= PROPERTIES
    public final void setNextClass(Class cls)
    {
        _nextClass.setClass(cls, false);
        _nextClass.setOnClickListener(createClickListener(cls));
        _nextClass.setOnLongClickListener(createLongClickListener(cls));

        if (cls != null)
        {
            TimeSpan time = TimeUtils.getDifference(TimeUtils.today(), TimeUtils.now(), cls.day, cls.start);
            _lblNextTimeMessage.setText(getContext().getString(R.string.starts_in_x, getVerboseTimeString(time)));
        }
        else
            _lblNextTimeMessage.setText("");
    }
    public final void setLastClass(Class cls)
    {
        _lastClass.setClass(cls, false);
        _lastClass.setOnClickListener(createClickListener(cls));
        _lastClass.setOnLongClickListener(createLongClickListener(cls));

        if (_divider != null)
            _divider.setVisibility(GONE);
        _layLast.setVisibility(GONE);

        if (cls != null)
        {
            // class not yet ended
            if (cls.isClash(TimeUtils.today(), TimeUtils.now()))
            {
                TimeSpan time = TimeUtils.getDifference(cls.day, cls.start, TimeUtils.today(), TimeUtils.now());
                _lblLastTimeMessage.setText(getContext().getString(R.string.started_x_ago, getVerboseTimeString(time)));
                if (_divider != null)
                    _divider.setVisibility(VISIBLE);
                _layLast.setVisibility(VISIBLE);
            }
            else
            {
                // TODO ended classes are never shown anymore, remove later
                TimeSpan time = TimeUtils.getDifference(cls.day, cls.end, TimeUtils.today(), TimeUtils.now());
                _lblLastTimeMessage.setText(getContext().getString(R.string.ended_x_ago, getVerboseTimeString(time)));
            }
        }
        else
            _lblLastTimeMessage.setText("");
    }

    public final void setOnClassClickListener(OnClassClickListener listener)
    {
        _listener = listener;
    }
}
