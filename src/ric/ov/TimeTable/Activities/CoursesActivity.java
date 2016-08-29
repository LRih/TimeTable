package ric.ov.TimeTable.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Data.Course;
import ric.ov.TimeTable.STS.CoursesTask;
import ric.ov.TimeTable.Utils.Task;
import ric.ov.TimeTable.Views.LoadingListView;

import java.util.ArrayList;
import java.util.List;

public final class CoursesActivity extends BackActivity implements View.OnClickListener, AdapterView.OnItemClickListener, Task.OnTaskCompleteListener<List<Course>>
{
    //========================================================================= VARIABLES
    public static final String EXTRA_ACAD_ORG = "acadOrg";
    public static final String EXTRA_SEMESTER = "semester";
    public static final String EXTRA_SCHOOL_NAME = "schoolName";
    public static final String EXTRA_SEMESTER_NAME = "semesterName";

    private LoadingListView _lstCourses;
    private CoursesAdapter _adapter;

    private CoursesTask _task;

    //========================================================================= INITIALIZE
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState, R.layout.activity_courses);

        initializeList();
        initializeTitle();

        startLoadingTask();
    }

    private void initializeList()
    {
        _adapter = new CoursesAdapter();
        _lstCourses = (LoadingListView)findViewById(R.id.lstCourses);
        _lstCourses.setAdapter(_adapter);
        _lstCourses.setOnItemClickListener(this);
        _lstCourses.setOnRetryClickListener(this);
    }
    private void initializeTitle()
    {
        if (getIntent().hasExtra(EXTRA_SCHOOL_NAME))
            setTitle(getIntent().getStringExtra(EXTRA_SCHOOL_NAME));

        if (getIntent().hasExtra(EXTRA_SEMESTER_NAME) && getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getIntent().getStringExtra(EXTRA_SEMESTER_NAME));
    }

    //========================================================================= FUNCTIONS
    private void startLoadingTask()
    {
        if (!getIntent().hasExtra(EXTRA_ACAD_ORG) || !getIntent().hasExtra(EXTRA_SEMESTER))
            return;

        if (_task != null)
        {
            _task.cancel(true);
            _task = null;
        }

        String acadOrg = getIntent().getStringExtra(EXTRA_ACAD_ORG);
        String semester = getIntent().getStringExtra(EXTRA_SEMESTER);
        _task = new CoursesTask(acadOrg, semester);
        _task.setOnTaskCompleteListener(this);
        _task.executeOnThreadPool();
    }

    //========================================================================= EVENTS
    public final void onClick(View v)
    {
        _adapter.clearItems();
        _lstCourses.updateVisibility();

        startLoadingTask();
    }

    public final void onItemClick(AdapterView<?> parent, View view, int index, long id)
    {
        Course course = _adapter.getItem(index);

        Intent intent = new Intent(this, ClassesActivity.class);
        intent.putExtra(ClassesActivity.EXTRA_OFFERING_ID, course.offeringId);
        startActivity(intent);
    }

    public final void onTaskComplete(List<Course> result, int errorCode)
    {
        switch (errorCode)
        {
            case CoursesTask.ERROR_NONE:
                _adapter.setItems(result);
                _lstCourses.setEmptyText(getString(R.string.no_courses_found));
                _lstCourses.setRetryVisible(false);
                break;

            case CoursesTask.ERROR_CONNECTION:
                _adapter.setItems(new ArrayList<Course>());
                _lstCourses.setEmptyText(getString(R.string.connection_error));
                _lstCourses.setRetryVisible(true);
                break;

            default:
                _adapter.setItems(new ArrayList<Course>());
                _lstCourses.setEmptyText(getString(R.string.unspecified_error));
                _lstCourses.setRetryVisible(true);
                break;
        }

        _lstCourses.updateVisibility();
    }
}
