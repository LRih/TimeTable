package ric.ov.TimeTable.Views.WeekViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.Utils.Day;

public final class HorizontalTableView extends TableView
{
    //========================================================================= INITIALIZE
    public HorizontalTableView(Context context)
    {
        super(context);
    }
    public HorizontalTableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    //========================================================================= FUNCTIONS
    protected final int calculateRows()
    {
        for (UIClass c : classes)
            if (c.cls.day == Day.Saturday || c.cls.day == Day.Sunday)
                return 7;
        return 5;
    }
    protected final int calculateColumns()
    {
        return endHour - startHour;
    }

    //========================================================================= PROPERTIES
    protected final RectF getRect(Class c)
    {
        return new RectF(
            getX(toIndex(c.start)),
            getY(c.day.ordinal()),
            getX(toIndex(c.end)),
            getY(c.day.ordinal() + 1)
        );
    }

    protected final float paddingLeft()
    {
        return rectDay.width() + textMargin * 2;
    }
    protected final float paddingTop()
    {
        return rectTime.height() + textMargin * 2;
    }

    //========================================================================= EVENTS
    protected final void onDraw(Canvas canvas)
    {
        drawRows(canvas);
        drawColumns(canvas);

        drawClasses(canvas);
        drawSelectedBorder(canvas);

        drawTimeMarker(canvas);

        drawDayText(canvas);
        drawTimeText(canvas);
    }

    private void drawRows(Canvas canvas)
    {
        paintFill.setColor(colGridDay);

        for (int i = 0; i < rows; i += 2)
            canvas.drawRect(getX(0), getY(i), getX(columns), getY(i + 1), paintFill);
    }
    private void drawColumns(Canvas canvas)
    {
        paintStroke.setColor(colGridTime);
        paintStroke.setStrokeWidth(dividerWidth);

        for (int i = 0; i < columns; i++)
        {
            float x = getX(i + 0.5f);
            canvas.drawLine(x, getY(0), x, getY(rows), paintStroke);
        }
    }

    private void drawClasses(Canvas canvas)
    {
        paintText.setColor(colTextClass);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(textSize);

        for (UIClass c : classes)
        {
            paintFill.setColor(c.cls.course.color);

            RectF rect = getRect(c.cls);
            canvas.drawRect(rect, paintFill);

            // only draw course name if class rect is large enough
            if (rect.width() - textMargin * 2 > rectDay.width())
                canvas.drawText(c.cls.course.abbreviation(), rect.centerX(), rect.centerY() - rectDay.exactCenterY(), paintText);
        }
    }
    private void drawSelectedBorder(Canvas canvas)
    {
        if (isClassSelected())
        {
            paintStroke.setStrokeWidth(dividerWidth * 3);
            paintStroke.setColor(colSelected);

            RectF rect = getRect(selectedClass.cls);
            rect.right--;
            rect.bottom--;
            canvas.drawRect(rect, paintStroke);
        }
    }

    private void drawTimeMarker(Canvas canvas)
    {
        if (today != null && now != null && today.ordinal() < rows)
        {
            float x = getX(Math.min(Math.max(toIndex(now), 0), columns));
            float y1 = getY(today.ordinal());
            float y2 = getY(today.ordinal() + 1);

            paintStroke.setColor(colSelected);
            paintStroke.setStrokeWidth(dividerWidth * 2);
            canvas.drawLine(x, y1, x, y2, paintStroke);
        }
    }

    private void drawDayText(Canvas canvas)
    {
        paintText.setColor(colTextGrid);
        paintText.setTextAlign(Paint.Align.RIGHT);
        paintText.setTextSize(textSize);

        for (int i = 0; i < rows; i++)
        {
            float x = getX(0) - textMargin;
            float y = getY(i + 0.5f) - rectDay.exactCenterY();
            canvas.drawText(Day.fromId(i).abbreviation(), x, y, paintText);
        }
    }
    private void drawTimeText(Canvas canvas)
    {
        paintText.setColor(colTextGrid);
        paintText.setTextAlign(Paint.Align.CENTER);

        // resize text if too big
        if (rectTime.width() + textMargin * 2 >= columnWidth)
            paintText.setTextSize(textSize / 2);
        else
            paintText.setTextSize(textSize);

        for (int i = 0; i < columns; i++)
        {
            float x = getX(i + 0.5f);
            float y = getY(0) - textMargin;
            canvas.drawText((startHour + i) + ":30", x, y, paintText);
        }
    }
}
