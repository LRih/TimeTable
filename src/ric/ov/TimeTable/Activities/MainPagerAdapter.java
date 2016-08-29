package ric.ov.TimeTable.Activities;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Views.WeekViews.NextView;
import ric.ov.TimeTable.Views.WeekViews.WeekGridView;
import ric.ov.TimeTable.Views.WeekViews.WeekListView;

public final class MainPagerAdapter extends PagerAdapter
{
    //========================================================================= VARIABLES
    private static final int PAGE_COUNT = 3;

    public static final int WEEK_GRID_INDEX = 0;
    public static final int NEXT_INDEX = 1;
    public static final int WEEK_LIST_INDEX = 2;

    private final Context _context;
    private final WeekGridView _weekGridView;
    private final NextView _nextView;
    private final WeekListView _weekListView;

    //========================================================================= INITIALIZE
    public MainPagerAdapter(Context context, WeekGridView weekGridView, NextView nextView, WeekListView weekListView)
    {
        _context = context;
        _weekGridView = weekGridView;
        _nextView = nextView;
        _weekListView = weekListView;
    }

    //========================================================================= FUNCTIONS
    public final Object instantiateItem(ViewGroup collection, int index)
    {
        View view;

        switch (index)
        {
            case WEEK_GRID_INDEX:
                view = _weekGridView;
                break;
            case NEXT_INDEX:
                view = _nextView;
                break;
            case WEEK_LIST_INDEX:
                view = _weekListView;
                break;
            default:
                throw new RuntimeException("Invalid index: " + index);
        }

        collection.addView(view);

        return view;
    }

    public final void destroyItem(ViewGroup collection, int index, Object view)
    {
        collection.removeView((View)view);
    }

    public boolean isViewFromObject(View view, Object o)
    {
        return view == o;
    }

    //========================================================================= PROPERTIES
    public final CharSequence getPageTitle(int index)
    {
        switch (index)
        {
            case WEEK_GRID_INDEX:
                return _context.getString(R.string.grid);
            case NEXT_INDEX:
                return _context.getString(R.string.next);
            case WEEK_LIST_INDEX:
                return _context.getString(R.string.list);
        }

        throw new RuntimeException("Invalid index: " + index);
    }

    public int getCount()
    {
        return PAGE_COUNT;
    }
}
