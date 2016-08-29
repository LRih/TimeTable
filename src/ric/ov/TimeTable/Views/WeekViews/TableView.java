package ric.ov.TimeTable.Views.WeekViews;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Utils.TimeSpan;

import java.util.ArrayList;
import java.util.List;

public abstract class TableView extends View
{
    //========================================================================= VARIABLES
    protected final Paint paintStroke = new Paint();
    protected final Paint paintFill = new Paint();
    protected final Paint paintText = new Paint();

    protected int colSelected;
    protected int colGridTime;
    protected int colGridDay;
    protected int colTextGrid;
    protected int colTextClass;

    protected final Rect rectDay = new Rect();
    protected final Rect rectTime = new Rect();
    protected float textSize;
    protected float textMargin;
    protected float dividerWidth;
    protected float rowHeight;
    protected float columnWidth;

    protected List<UIClass> classes = new ArrayList<>();
    protected int startHour, endHour;
    protected int rows, columns;

    protected UIClass selectedClass;
    protected Day today;
    protected TimeSpan now;

    private GestureDetectorCompat _gestureDetector;

    private OnClassStateChangeListener _listener;

    //========================================================================= INITIALIZE
    public TableView(Context context)
    {
        super(context);
        initialize(context);
    }
    public TableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context)
    {
        setClickable(true);

        colSelected = ContextCompat.getColor(context, R.color.primary);
        colGridTime = ContextCompat.getColor(context, R.color.grid_row);
        colGridDay = ContextCompat.getColor(context, R.color.grid_column);
        colTextGrid = ContextCompat.getColor(context, R.color.text_50);
        colTextClass = ContextCompat.getColor(context, R.color.text_0);

        paintStroke.setAntiAlias(true);
        paintStroke.setStyle(Paint.Style.STROKE);

        paintFill.setAntiAlias(true);
        paintFill.setStyle(Paint.Style.FILL);

        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.FILL);

        _gestureDetector = new GestureDetectorCompat(getContext(), onGesture);
    }

    //========================================================================= FUNCTIONS
    private void selectClass(UIClass c)
    {
        selectedClass = c;
        boolean isTop = getRect(c.cls).centerY() <= getHeight() / 2f;
        _listener.onSelected(c, isTop);

        postInvalidate();
    }
    public final void deselectClass()
    {
        selectedClass = null;
        _listener.onDeselected();

        postInvalidate();
    }

    protected void refreshDimensions()
    {
        paintText.setTextSize(textSize);
        paintText.getTextBounds("M", 0, 1, rectDay);
        paintText.getTextBounds("10:00", 0, 5, rectTime);

        startHour = calculateStartHour();
        endHour = calculateEndHour();
        rows = calculateRows();
        columns = calculateColumns();

        textSize = getResources().getDimensionPixelSize(R.dimen.grid_text);
        textMargin = getResources().getDimensionPixelSize(R.dimen.margin_4);
        dividerWidth = getResources().getDimensionPixelSize(R.dimen.grid_divider);
        rowHeight = (effectiveHeight() - paddingTop()) / rows;
        columnWidth = (effectiveWidth() - paddingLeft()) / columns;
    }

    private int calculateStartHour()
    {
        int start = 100;
        for (UIClass c : classes)
            start = Math.min(c.cls.start.hour(), start);
        return start;
    }
    private int calculateEndHour()
    {
        int end = 0;
        for (UIClass c : classes)
            end = Math.max(c.cls.end.hour() + 1, end);
        return end;
    }

    protected abstract int calculateRows();
    protected abstract int calculateColumns();

    protected final float toIndex(TimeSpan ts)
    {
        return ts.hour() - startHour + ts.minute() / 60f;
    }

    //========================================================================= PROPERTIES
    public final void setClasses(List<UIClass> classes, Day today, TimeSpan now)
    {
        this.classes = classes;
        this.today = today;
        this.now = now;

        deselectClass();

        refreshDimensions();

        postInvalidate();
    }

    public final boolean isClassSelected()
    {
        return selectedClass != null;
    }

    protected abstract RectF getRect(Class c);
    private UIClass getClassAtPt(float x, float y)
    {
        // traverse backwards to check _classes shown on top first (when clashes exists)
        for (int i = classes.size() - 1; i >= 0; i--)
        {
            UIClass c = classes.get(i);
            if (getRect(c.cls).contains(x, y))
                return c;
        }
        return null;
    }


    protected final float effectiveWidth()
    {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }
    protected final float effectiveHeight()
    {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    protected final float getX(float colIndex)
    {
        return getPaddingLeft() + paddingLeft() + columnWidth * colIndex;
    }
    protected final float getY(float rowIndex)
    {
        return getPaddingTop() + paddingTop() + rowHeight * rowIndex;
    }

    protected abstract float paddingLeft();
    protected abstract float paddingTop();


    public final void setOnClassStateChangeListener(OnClassStateChangeListener listener)
    {
        _listener = listener;
    }

    //========================================================================= EVENTS
    protected final void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        refreshDimensions();
    }


    public final boolean onTouchEvent(MotionEvent event)
    {
        return _gestureDetector.onTouchEvent(event);
    }

    private final GestureDetector.SimpleOnGestureListener onGesture = new GestureDetector.SimpleOnGestureListener()
    {
        public final boolean onDown(MotionEvent event)
        {
            return true;
        }

        public final boolean onSingleTapUp(MotionEvent event)
        {
            if (_listener != null)
            {
                UIClass c = getClassAtPt(event.getX(), event.getY());

                if (c != null)
                    selectClass(c);
                else
                    deselectClass();
            }

            return true;
        }
    };

    //========================================================================= CLASS
    public interface OnClassStateChangeListener
    {
        void onSelected(UIClass c, boolean isTop);
        void onDeselected();
    }
}
