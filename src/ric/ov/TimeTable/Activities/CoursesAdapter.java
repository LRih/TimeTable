package ric.ov.TimeTable.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Data.Course;
import ric.ov.TimeTable.Utils.LoadableAdapter;

public final class CoursesAdapter extends LoadableAdapter<Course>
{
    //========================================================================= FUNCTIONS
    public final View getView(int index, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_course, parent, false);
        }

        Course course = getItem(index);

        TextView lblName = (TextView)convertView.findViewById(R.id.lblName);
        TextView lblCode = (TextView)convertView.findViewById(R.id.lblCode);

        lblName.setText(course.name);
        lblCode.setText(course.code);

        return convertView;
    }
}
