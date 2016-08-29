package ric.ov.TimeTable.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Utils.LoadableAdapter;

public final class LoadingListView extends FrameLayout
{
    //========================================================================= VARIABLES
    private ListView _list;

    private View _layEmpty;
    private TextView _lblEmpty;
    private View _btnRetry;

    private View _progressBar;

    private LoadableAdapter _adapter;

    //========================================================================= INITIALIZE
    public LoadingListView(Context context)
    {
        super(context);
        initialize();
    }
    public LoadingListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();

        TypedArray ta = context.obtainStyledAttributes(attrs, new int[] {
            android.R.attr.divider, android.R.attr.dividerHeight, android.R.attr.fastScrollEnabled, android.R.attr.text
        });

        if (ta.hasValue(0))
            _list.setDivider(ta.getDrawable(0));
        _list.setDividerHeight(ta.getDimensionPixelSize(1, 0));
        _list.setFastScrollEnabled(ta.getBoolean(2, false));
        if (ta.hasValue(3))
            setEmptyText(ta.getString(3));

        ta.recycle();
    }

    private void initialize()
    {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_loading_list, this, true);

        _list = (ListView)findViewById(R.id.list);

        _layEmpty = findViewById(R.id.layEmpty);
        _lblEmpty = (TextView)findViewById(R.id.lblEmpty);
        _btnRetry = findViewById(R.id.btnRetry);

        _progressBar = findViewById(R.id.progressBar);

        updateVisibility();
    }

    //========================================================================= FUNCTIONS
    /* shows progress bar on list if it is in a loading state */
    public final void updateVisibility()
    {
        if (_adapter != null)
        {
            _layEmpty.setVisibility(_adapter.isLoaded() && _adapter.isEmpty() ? VISIBLE : GONE);
            _progressBar.setVisibility(!_adapter.isLoaded() ? VISIBLE : GONE);
        }
        else
        {
            _layEmpty.setVisibility(VISIBLE);
            _progressBar.setVisibility(GONE);
        }
    }

    //========================================================================= PROPERTIES
    public final void setAdapter(LoadableAdapter adapter)
    {
        _adapter = adapter;
        _list.setAdapter(_adapter);

        updateVisibility();
    }

    public final void setEmptyText(String text)
    {
        _lblEmpty.setText(text);
    }
    public final void setRetryVisible(boolean visible)
    {
        _btnRetry.setVisibility(visible ? VISIBLE : GONE);
    }

    public final void setOnItemClickListener(AdapterView.OnItemClickListener listener)
    {
        _list.setOnItemClickListener(listener);
    }
    public final void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener)
    {
        _list.setOnItemLongClickListener(listener);
    }
    public final void setOnRetryClickListener(OnClickListener listener)
    {
        _btnRetry.setOnClickListener(listener);
    }
}
