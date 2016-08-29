package ric.ov.TimeTable.Activities;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.Course;
import ric.ov.TimeTable.Data.SQL;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Utils.TimeSpan;

public final class EditClassActivity extends BackActivity
{
    //========================================================================= VARIABLES
    public static final String EXTRA_CLASS_ID = "classId";
    private static final String KEY_SELECTED_DAY = "selectedDay";

    private AutoCompleteTextView _txtCourse;
    private EditText _txtType;
    private EditText _txtRoom;

    private ViewGroup _layDays;

    private EditText _txtStartHr, _txtStartMin;
    private EditText _txtEndHr, _txtEndMin;

    private Day _selectedDay;

    private Class _class;

    //========================================================================= INITIALIZE
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState, R.layout.activity_edit_class);

        initializeViews();
        initializeData();
    }

    private void initializeViews()
    {
        _txtCourse = (AutoCompleteTextView)findViewById(R.id.txtCourse);

        // autocomplete crashes on gingerbread
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            _txtCourse.setAdapter(new ArrayAdapter<>(this, R.layout.item_autocomplete, R.id.text, SQL.loadCourseNames(this)));

        _txtType = (EditText)findViewById(R.id.txtType);
        _txtRoom = (EditText)findViewById(R.id.txtRoom);

        initializeDayViews();

        _txtStartHr = (EditText)findViewById(R.id.txtStartHr);
        _txtStartMin = (EditText)findViewById(R.id.txtStartMin);

        _txtEndHr = (EditText)findViewById(R.id.txtEndHr);
        _txtEndMin = (EditText)findViewById(R.id.txtEndMin);
    }
    private void initializeDayViews()
    {
        _layDays = (ViewGroup)findViewById(R.id.layDays);

        for (int i = 0; i < _layDays.getChildCount(); i++)
        {
            final int index = i;
            Button btn = (Button)_layDays.getChildAt(index);

            btn.setText(Day.fromId(index).abbreviation());
            btn.setOnClickListener(new View.OnClickListener()
            {
                public final void onClick(View v)
                {
                    setSelectedDay(Day.fromId(index));
                }
            });
        }

        setSelectedDay(Day.Monday);
    }

    private void initializeData()
    {
        if (getIntent().hasExtra(EXTRA_CLASS_ID))
            setEditMode(getIntent().getLongExtra(EXTRA_CLASS_ID, -1));
    }

    //========================================================================= FUNCTIONS
    private boolean tryAddOrUpdateClass()
    {
        if (_txtCourse.getText().toString().isEmpty())
        {
            _txtCourse.setError(getString(R.string.course_cannot_be_empty));
            _txtCourse.requestFocus();
            return false;
        }
        else if (Integer.parseInt(_txtStartHr.getText().toString()) > 23)
        {
            _txtStartHr.setError(getString(R.string.hour_must_be_between_0_23));
            _txtStartHr.requestFocus();
            return false;
        }
        else if (Integer.parseInt(_txtStartMin.getText().toString()) > 59)
        {
            _txtStartMin.setError(getString(R.string.minute_must_be_between_0_59));
            _txtStartMin.requestFocus();
            return false;
        }
        else if (Integer.parseInt(_txtEndHr.getText().toString()) > 23)
        {
            _txtEndHr.setError(getString(R.string.hour_must_be_between_0_23));
            _txtEndHr.requestFocus();
            return false;
        }
        else if (Integer.parseInt(_txtEndMin.getText().toString()) > 59)
        {
            _txtEndMin.setError(getString(R.string.minute_must_be_between_0_59));
            _txtEndMin.requestFocus();
            return false;
        }
        else if (Integer.parseInt(_txtEndMin.getText().toString()) > 59)
        {
            _txtEndMin.setError(getString(R.string.minute_must_be_between_0_59));
            _txtEndMin.requestFocus();
            return false;
        }

        TimeSpan start = new TimeSpan(0, Integer.parseInt(_txtStartHr.getText().toString()), Integer.parseInt(_txtStartMin.getText().toString()));
        TimeSpan end = new TimeSpan(0, Integer.parseInt(_txtEndHr.getText().toString()), Integer.parseInt(_txtEndMin.getText().toString()));

        if (start.compareTo(end) > 0)
        {
            Toast.makeText(this, getString(R.string.end_time_must_be_after_start_time), Toast.LENGTH_SHORT).show();
            return false;
        }

        Course course = createCourse();
        Class cls = createClass(course);

        return isEditMode() ? tryUpdateClass(cls) : tryAddClass(cls);
    }

    private boolean tryAddClass(Class cls)
    {
        try
        {
            SQL.insertClassIfAbsent(this, cls);
            Toast.makeText(this, getString(R.string.class_added), Toast.LENGTH_SHORT).show();
            return true;
        }
        catch (Exception e)
        {
            Toast.makeText(this, getString(R.string.class_added_failure), Toast.LENGTH_SHORT).show();
        }

        return false;
    }
    private boolean tryUpdateClass(Class cls)
    {
        try
        {
            SQL.updateClassAndCourse(this, cls);
            Toast.makeText(this, getString(R.string.class_updated), Toast.LENGTH_SHORT).show();
            return true;
        }
        catch (Exception e)
        {
            Toast.makeText(this, getString(R.string.class_updated_failure), Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private Course createCourse()
    {
        if (isEditMode())
            return new Course(_class.course.id, _class.course.offeringId, _class.course.code, _txtCourse.getText().toString(), _class.course.color);
        else
            return Course.createNonSTS(_txtCourse.getText().toString());
    }
    private Class createClass(Course course)
    {
        TimeSpan start = new TimeSpan(0, Integer.parseInt(_txtStartHr.getText().toString()), Integer.parseInt(_txtStartMin.getText().toString()));
        TimeSpan end = new TimeSpan(0, Integer.parseInt(_txtEndHr.getText().toString()), Integer.parseInt(_txtEndMin.getText().toString()));

        if (isEditMode())
            return new Class(_class.id, _class.stsId, course, _txtType.getText().toString(), _selectedDay, start, end, _txtRoom.getText().toString());
        else
            return Class.createNonSTS(course, _txtType.getText().toString(), _selectedDay, start, end, _txtRoom.getText().toString());
    }

    //========================================================================= PROPERTIES
    private void setEditMode(long classId)
    {
        _class = SQL.loadClass(this, classId);

        setTitle(R.string.edit_class);
        setFields(_class);

        // disable fields for STS class and focus on text box
        if (_class.isFromSTS())
        {
            _txtCourse.setEnabled(false);
            _txtType.setEnabled(false);

            for (int i = 0; i < _layDays.getChildCount(); i++)
                _layDays.getChildAt(i).setEnabled(false);

            _txtStartHr.setEnabled(false);
            _txtStartMin.setEnabled(false);
            _txtEndHr.setEnabled(false);
            _txtEndMin.setEnabled(false);

            _txtRoom.requestFocus();
        }
        else
            _txtType.requestFocus();
    }

    private void setFields(Class cls)
    {
        _txtCourse.setText(cls.course.name);
        _txtType.setText(cls.type);
        _txtRoom.setText(cls.room);

        setSelectedDay(cls.day);

        _txtStartHr.setText(String.format("%02d", cls.start.hour()));
        _txtStartMin.setText(String.format("%02d", cls.start.minute()));
        _txtEndHr.setText(String.format("%02d", cls.end.hour()));
        _txtEndMin.setText(String.format("%02d", cls.end.minute()));
    }

    private boolean isEditMode()
    {
        return _class != null;
    }


    private void setSelectedDay(Day day)
    {
        _selectedDay = day;

        for (int i = 0; i < _layDays.getChildCount(); i++)
            _layDays.getChildAt(i).setSelected(i == day.ordinal());
    }

    //========================================================================= EVENTS
    public final boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.edit_class, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public final boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuOK:
                if (tryAddOrUpdateClass())
                    finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected final void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SELECTED_DAY, _selectedDay);
    }
    protected final void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        setSelectedDay((Day)savedInstanceState.getSerializable(KEY_SELECTED_DAY));
    }
}
