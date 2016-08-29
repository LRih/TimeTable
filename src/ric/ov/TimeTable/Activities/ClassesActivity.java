package ric.ov.TimeTable.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.Classes;
import ric.ov.TimeTable.Data.SQL;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.STS.ClassesTask;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.Utils.Task;
import ric.ov.TimeTable.Views.LoadingListView;

import java.util.ArrayList;
import java.util.List;

public final class ClassesActivity extends BackActivity implements View.OnClickListener, AdapterView.OnItemClickListener, Task.OnTaskCompleteListener<Classes>
{
    //========================================================================= VARIABLES
    public static final String EXTRA_OFFERING_ID = "offeringId";

    private LoadingListView _lstClasses;
    private ClassesAdapter _adapter;

    private Classes _classes;
    private ClassesTask _task;

    //========================================================================= INITIALIZE
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState, R.layout.activity_classes);

        initializeList();

        startLoadingTask();
    }

    private void initializeList()
    {
        _adapter = new ClassesAdapter();
        _lstClasses = (LoadingListView)findViewById(R.id.lstClasses);
        _lstClasses.setAdapter(_adapter);
        _lstClasses.setOnItemClickListener(this);
        _lstClasses.setOnRetryClickListener(this);
    }

    //========================================================================= FUNCTIONS
    private void startLoadingTask()
    {
        if (!getIntent().hasExtra(EXTRA_OFFERING_ID))
            return;

        if (_task != null)
        {
            _task.cancel(true);
            _task = null;
        }

        long offeringId = getIntent().getLongExtra(EXTRA_OFFERING_ID, 0);
        _task = new ClassesTask(offeringId);
        _task.setOnTaskCompleteListener(this);
        _task.executeOnThreadPool();
    }

    private void updateTitle()
    {
        setTitle(_classes.course.name);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(_classes.course.code);
    }

    private List<UIClass> createUIClasses()
    {
        List<UIClass> uiClasses = new ArrayList<>();
        List<Class> storedClasses = SQL.loadClasses(this);

        for (Class c : _classes.items)
        {
            Class storedClass = getStoredClass(c.stsId, storedClasses);

            // use stored class if exists (for reference to id when deleting)
            if (storedClass != null)
                uiClasses.add(new UIClass(storedClass, isClash(storedClass, storedClasses), true));
            else
                uiClasses.add(new UIClass(c, isClash(c, storedClasses), false));
        }

        return uiClasses;
    }
    private boolean isClash(Class cls, List<Class> storedClasses)
    {
        // don't show clash with itself
        for (Class storedClass : storedClasses)
            if (cls.isClash(storedClass) && !cls.stsId.equals(storedClass.stsId))
                return true;
        return false;
    }
    private Class getStoredClass(String stsId, List<Class> storedClasses)
    {
        for (Class storedClass : storedClasses)
            if (storedClass.stsId.equals(stsId))
                return storedClass;
        return null;
    }

    //========================================================================= PROPERTIES
    private void setClasses(Classes classes)
    {
        _classes = classes;
        _adapter.setClasses(createUIClasses());

        updateTitle();
    }

    //========================================================================= EVENTS
    public final void onClick(View v)
    {
        _adapter.clearItems();
        _lstClasses.updateVisibility();

        startLoadingTask();
    }

    public final void onItemClick(AdapterView<?> parent, View view, int index, long id)
    {
        UIClass cls = (UIClass)_adapter.getItem(index);

        if (cls.isSelected)
            SQL.deleteClassAndEmptyCourse(this, cls.cls);
        else
        {
            try
            {
                SQL.insertClassIfAbsent(this, cls.cls);
            }
            catch (Exception ex)
            {
                Toast.makeText(this, getString(R.string.class_added_failure), Toast.LENGTH_SHORT).show();
            }
        }

        _adapter.setClasses(createUIClasses());
    }

    public final void onTaskComplete(Classes result, int errorCode)
    {
        switch (errorCode)
        {
            case ClassesTask.ERROR_NONE:
                setClasses(result);
                _lstClasses.setEmptyText(getString(R.string.no_classes_found));
                _lstClasses.setRetryVisible(false);
                break;

            case ClassesTask.ERROR_CONNECTION:
                _adapter.setClasses(new ArrayList<UIClass>());
                _lstClasses.setEmptyText(getString(R.string.connection_error));
                _lstClasses.setRetryVisible(true);
                break;

            default:
                _adapter.setClasses(new ArrayList<UIClass>());
                _lstClasses.setEmptyText(getString(R.string.unspecified_error));
                _lstClasses.setRetryVisible(true);
                break;
        }

        _lstClasses.updateVisibility();
    }
}
