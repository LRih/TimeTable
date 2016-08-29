package ric.ov.TimeTable.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.STS.School;
import ric.ov.TimeTable.Utils.LoadableAdapter;

public final class SchoolsAdapter extends LoadableAdapter<School>
{
    //========================================================================= FUNCTIONS
    public final View getView(int index, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_school, parent, false);
        }

        ((TextView)convertView).setText(getItem(index).name);

        return convertView;
    }
}
