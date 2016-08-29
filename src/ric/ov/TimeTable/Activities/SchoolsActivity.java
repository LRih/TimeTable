package ric.ov.TimeTable.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.STS.STSManager;
import ric.ov.TimeTable.STS.SchoolsTask;
import ric.ov.TimeTable.STS.School;
import ric.ov.TimeTable.Utils.Task;
import ric.ov.TimeTable.Views.LoadingListView;

import java.util.ArrayList;
import java.util.List;

public final class SchoolsActivity extends BackActivity implements View.OnClickListener, AdapterView.OnItemClickListener, Task.OnTaskCompleteListener<List<School>>
{
    //========================================================================= VARIABLES
    private LoadingListView _lstSchools;
    private SchoolsAdapter _adapter;

    private SchoolsTask _task;

    //========================================================================= INITIALIZE
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState, R.layout.activity_schools);

        initializeList();

        startLoadingTask();
    }

    private void initializeList()
    {
        _adapter = new SchoolsAdapter();
        _lstSchools = (LoadingListView)findViewById(R.id.lstSchools);
        _lstSchools.setAdapter(_adapter);
        _lstSchools.setOnItemClickListener(this);
        _lstSchools.setOnRetryClickListener(this);
    }

    //========================================================================= FUNCTIONS
    private void startLoadingTask()
    {
        if (_task != null)
        {
            _task.cancel(true);
            _task = null;
        }

        _task = new SchoolsTask();
        _task.setOnTaskCompleteListener(this);
        _task.executeOnThreadPool();
    }

    //========================================================================= EVENTS
    public final void onClick(View v)
    {
        _adapter.clearItems();
        _lstSchools.updateVisibility();

        startLoadingTask();
    }

    public final void onItemClick(AdapterView<?> parent, View view, int index, long id)
    {
        School school = _adapter.getItem(index);

        Intent intent = new Intent(this, CoursesActivity.class);
        intent.putExtra(CoursesActivity.EXTRA_ACAD_ORG, school.acadOrg);
        intent.putExtra(CoursesActivity.EXTRA_SEMESTER, STSManager.SEMESTER_2_2016);
        intent.putExtra(CoursesActivity.EXTRA_SCHOOL_NAME, school.name);
        intent.putExtra(CoursesActivity.EXTRA_SEMESTER_NAME, "Semester 2 HE 2016");
        startActivity(intent);
    }

    public final void onTaskComplete(List<School> result, int errorCode)
    {
        switch (errorCode)
        {
            case SchoolsTask.ERROR_NONE:
                _adapter.setItems(result);
                _lstSchools.setEmptyText(getString(R.string.no_schools_found));
                _lstSchools.setRetryVisible(false);
                break;

            case SchoolsTask.ERROR_CONNECTION:
                _adapter.setItems(new ArrayList<School>());
                _lstSchools.setEmptyText(getString(R.string.connection_error));
                _lstSchools.setRetryVisible(true);
                break;

            default:
                _adapter.setItems(new ArrayList<School>());
                _lstSchools.setEmptyText(getString(R.string.unspecified_error));
                _lstSchools.setRetryVisible(true);
                break;
        }

        _lstSchools.updateVisibility();
    }
}
