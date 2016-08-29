package ric.ov.TimeTable.Utils;

import android.widget.BaseAdapter;

import java.util.List;

public abstract class LoadableAdapter<T> extends BaseAdapter
{
    //========================================================================= VARIABLES
    private List<T> _items;

    //========================================================================= FUNCTIONS
    public final void clearItems()
    {
        _items = null;
    }

    //========================================================================= PROPERTIES
    public final void setItems(List<T> items)
    {
        _items = items;
        notifyDataSetChanged();
    }

    public final boolean isLoaded()
    {
        return _items != null;
    }

    public final int getCount()
    {
        return _items != null ? _items.size() : 0;
    }
    public final T getItem(int index)
    {
        return _items.get(index);
    }
    public final long getItemId(int index)
    {
        return index;
    }
}
