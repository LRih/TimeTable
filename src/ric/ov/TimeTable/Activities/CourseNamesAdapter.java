package ric.ov.TimeTable.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import ric.ov.TimeTable.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public final class CourseNamesAdapter extends BaseAdapter implements Filterable
{
    //========================================================================= VARIABLES
    private final List<String> _names;
    private List<String> _filteredNames = new ArrayList<>();

    private final Filter _filter = new CourseNamesFilter();

    //========================================================================= INITIALIZE
    public CourseNamesAdapter(List<String> names)
    {
        _names = names;
    }

    //========================================================================= FUNCTIONS
    public final View getView(int index, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_dialog, parent, false);
        }

        ((TextView)convertView).setText(getItem(index));

        return convertView;
    }

    //========================================================================= PROPERTIES
    public final int getCount()
    {
        return _filteredNames.size();
    }
    public final String getItem(int index)
    {
        return _filteredNames.get(index);
    }
    public final long getItemId(int index)
    {
        return index;
    }

    public final Filter getFilter()
    {
        return _filter;
    }

    //========================================================================= CLASSES
    private final class CourseNamesFilter extends Filter
    {
        protected final FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();
            List<String> names = new ArrayList<>();

            if (constraint != null)
            {
                for (String name : _names)
                    if (name.toLowerCase().contains(constraint.toString().toLowerCase()))
                        names.add(name);
            }

            Collections.sort(names);
            results.values = names;

            return results;
        }

        protected final void publishResults(CharSequence constraint, FilterResults results)
        {
            _filteredNames = (List<String>)results.values;
            notifyDataSetChanged();
        }
    }
}
