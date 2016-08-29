package ric.ov.TimeTable.Views.WeekViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.Utils.Day;

public final class VerticalTableView extends TableView
{
    //========================================================================= INITIALIZE
    public VerticalTableView(Context context)
    {
        super(context);
    }
    public VerticalTableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    //========================================================================= FUNCTIONS
    protected final int calculateRows()
    {
        return endHour - startHour;
    }
    protected final int calculateColumns()
    {
        for (UIClass c : classes)
            if (c.cls.day == Day.Saturday || c.cls.day == Day.Sunday)
                return 7;
        return 5;
    }

    //========================================================================= PROPERTIES
    protected final RectF getRect(Class c)
    {
        return new RectF(
            getX(c.day.ordinal()),
            getY(toIndex(c.start)),
            getX(c.day.ordinal() + 1),
            getY(toIndex(c.end))
        );
    }

    protected final float paddingLeft()
    {
        return rectTime.width() + textMargin * 2;
    }
    protected final float paddingTop()
    {
        return rectDay.height() + textMargin * 2;
    }

    //========================================================================= EVENTS
    protected final void onDraw(Canvas canvas)
    {
        drawColumns(canvas);
        drawRows(canvas);

        drawClasses(canvas);
        drawSelectedBorder(canvas);

        drawTimeMarker(canvas);

        drawDayText(canvas);
        drawTimeText(canvas);
    }

    private void drawColumns(Canvas canvas)
    {
        paintFill.setColor(colGridDay);

        for (int i = 0; i < columns; i += 2)
            canvas.drawRect(getX(i), getY(0), getX(i + 1), getY(rows), paintFill);
    }
    private void drawRows(Canvas canvas)
    {
        paintStroke.setColor(colGridTime);
        paintStroke.setStrokeWidth(dividerWidth);

        for (int i = 0; i < rows; i++)
        {
            float y = getY(i + 0.5f);
            canvas.drawLine(getX(0), y, getX(columns), y, paintStroke);
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
            if (rect.height() - textMargin * 2 > rectDay.height())
                canvas.drawText(c.cls.course.abbreviation(), rect.centerX(), rect.centerY() - rectDay.exactCenterY(), paintText);
        }
    }
    private void drawSelectedBorder(Canvas canvas)
    {
        if (isClassSelected())
        {
            paintStroke.setColor(colSelected);
            paintStroke.setStrokeWidth(dividerWidth * 3);

            RectF rect = getRect(selectedClass.cls);
            rect.right--;
            rect.bottom--;
            canvas.drawRect(rect, paintStroke);
        }
    }

    private void drawTimeMarker(Canvas canvas)
    {
        if (today != null && now != null && today.ordinal() < columns)
        {
            float x1 = getX(today.ordinal());
            float x2 = getX(today.ordinal() + 1);
            float y = getY(Math.min(Math.max(toIndex(now), 0), rows));

            paintStroke.setColor(colSelected);
            paintStroke.setStrokeWidth(dividerWidth * 2);
            canvas.drawLine(x1, y, x2, y, paintStroke);
        }
    }

    private void drawDayText(Canvas canvas)
    {
        paintText.setColor(colTextGrid);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(textSize);

        for (int i = 0; i < columns; i++)
        {
            float x = getX(i + 0.5f);
            float y = getY(0) - textMargin;
            canvas.drawText(Day.fromId(i).abbreviation(), x, y, paintText);
        }
    }
    private void drawTimeText(Canvas canvas)
    {
        paintText.setColor(colTextGrid);
        paintText.setTextAlign(Paint.Align.RIGHT);
        paintText.setTextSize(textSize);

        for (int i = 0; i < rows; i++)
        {
            float x = getX(0) - textMargin;
            float y = getY(i + 0.5f) - rectTime.exactCenterY();
            canvas.drawText((startHour + i) + ":30", x, y, paintText);
        }
    }
}
