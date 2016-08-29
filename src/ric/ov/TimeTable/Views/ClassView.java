package ric.ov.TimeTable.Views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.R;

public final class ClassView extends LinearLayout
{
    //========================================================================= VARIABLES
    private final TextView _lblStart;
    private final TextView _lblEnd;

    private final View _viewColor;

    private final TextView _lblCourse;
    private final TextView _lblSubtitle;

    private final View _lblClash;

    //========================================================================= INITIALIZE
    public ClassView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_class, this, true);

        _lblStart = (TextView)findViewById(R.id.lblStart);
        _lblEnd = (TextView)findViewById(R.id.lblEnd);

        _viewColor = findViewById(R.id.viewColor);

        _lblCourse = (TextView)findViewById(R.id.lblCourse);
        _lblSubtitle = (TextView)findViewById(R.id.lblSubtitle);

        _lblClash = findViewById(R.id.lblClash);
    }

    //========================================================================= FUNCTIONS
    private String getSubtitle(Class cls)
    {
        if (!cls.type.isEmpty() && !cls.room.isEmpty())
            return cls.type + " (" + cls.room + ")";
        else if (!cls.type.isEmpty())
            return cls.type;
        return cls.room;
    }

    //========================================================================= PROPERTIES
    public final void setClass(UIClass cls)
    {
        if (cls == null)
            setClass(null, false);
        else
            setClass(cls.cls, cls.isClash);
    }
    public final void setClass(Class cls, boolean showClash)
    {
        if (cls != null)
        {
            _lblStart.setText(cls.start.toTimeString());
            _lblEnd.setText(cls.end.toTimeString());

            _viewColor.setBackgroundColor(cls.course.color);

            _lblCourse.setText(cls.course.name);
            _lblSubtitle.setText(getSubtitle(cls));
        }
        else
        {
            _lblStart.setText("");
            _lblEnd.setText("");

            _viewColor.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

            _lblCourse.setText("");
            _lblSubtitle.setText("");
        }

        _lblClash.setVisibility(showClash ? VISIBLE : GONE);
    }
}
