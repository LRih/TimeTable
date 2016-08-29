package ric.ov.TimeTable.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.UIClass;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.Test;
import ric.ov.TimeTable.Utils.Day;
import ric.ov.TimeTable.Data.SQL;
import ric.ov.TimeTable.Utils.TimeSpan;
import ric.ov.TimeTable.Utils.TimeUtils;
import ric.ov.TimeTable.Views.Dialogs.AboutDialog;
import ric.ov.TimeTable.Views.Dialogs.ClassOptionsDialog;
import ric.ov.TimeTable.Views.WeekViews.NextView;
import ric.ov.TimeTable.Views.WeekViews.OnClassClickListener;
import ric.ov.TimeTable.Views.WeekViews.WeekGridView;
import ric.ov.TimeTable.Views.WeekViews.WeekListView;

import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends AppCompatActivity implements OnClassClickListener
{
    //========================================================================= VARIABLES
    private Dialog _dialogAbout;
    private Dialog _dialogOptions;

    private DrawerLayout _layDrawer;
    private View _drawer;
    private ActionBarDrawerToggle _drawerToggle;

    private ViewPager _viewPager;
    private WeekGridView _weekGridView;
    private NextView _nextView;
    private WeekListView _weekListView;

    private View _layEmpty;

    private List<Class> _classes = new ArrayList<>();

    //========================================================================= INITIALIZE
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SQL.deleteAll(this);
//        Test.loadData(this);

        initializeDrawer();
        initializeViewPager();
        initializeEmptyText();
    }

    private void initializeDrawer()
    {
        _layDrawer = (DrawerLayout)findViewById(R.id.layDrawer);
        _drawer = findViewById(R.id.drawer);

        _drawerToggle = new ActionBarDrawerToggle(this, _layDrawer, R.string.app_name, R.string.app_name);

        _layDrawer.setDrawerListener(_drawerToggle);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initializeViewPager()
    {
        _weekGridView = new WeekGridView(this);
        _weekGridView.setOnClassClickListener(this);
        _nextView = new NextView(this);
        _nextView.setOnClassClickListener(this);
        _weekListView = new WeekListView(this);
        _weekListView.setOnClassClickListener(this);

        _viewPager = (ViewPager)findViewById(R.id.viewPager);
        _viewPager.setAdapter(new MainPagerAdapter(this, _weekGridView, _nextView, _weekListView));
        _viewPager.setCurrentItem(1);
    }
    private void initializeEmptyText()
    {
        _layEmpty = findViewById(R.id.layEmpty);
    }

    protected final void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        _drawerToggle.syncState();
    }

    //========================================================================= FUNCTIONS
    private void refreshData()
    {
        // update data
        TimeUtils.updateDayTime();
        _classes = SQL.loadClasses(this);

        // set data
        _weekGridView.setClasses(createUIClasses());

        Class nextClass = getNextClass();
        _nextView.setLastClass(getLastClass(nextClass));
        _nextView.setNextClass(nextClass);

        _weekListView.setClasses(createUIClasses());

        updateVisibility();
    }
    private void updateVisibility()
    {
        if (_classes.isEmpty())
        {
            _viewPager.setVisibility(View.GONE);
            _layEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            _viewPager.setVisibility(View.VISIBLE);
            _layEmpty.setVisibility(View.GONE);
        }
    }

    private List<UIClass> createUIClasses()
    {
        List<UIClass> uiClasses = new ArrayList<>();

        for (int i = 0; i < _classes.size(); i++)
        {
            Class c = _classes.get(i);
            uiClasses.add(new UIClass(c, isClash(i), false));
        }

        return uiClasses;
    }

    /* last/next functions assume list is properly sorted */
    private Class getLastClass(Class from)
    {
        for (int i = 0; i < _classes.size(); i++)
        {
            if (_classes.get(i) == from)
            {
                // wrap around to last class if index is zero
                int index = (i - 1 + _classes.size()) % _classes.size();
                return _classes.get(index);
            }
        }

        return null;
    }
    private Class getNextClass()
    {
        Day today = TimeUtils.today();
        TimeSpan now = TimeUtils.now();

        // find after current today/time
        for (Class c : _classes)
            if (c.day != today || c.start.compareTo(now) > 0)
                return c;

        // wrap around to first class of the day
        if (!_classes.isEmpty())
            return _classes.get(0);

        return null;
    }

    private void showOptionsDialog(final Class cls)
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
        {
            public final void onClick(DialogInterface dialog, int index)
            {
                switch (index)
                {
                    case ClassOptionsDialog.EDIT_INDEX:
                        Intent intent = new Intent(MainActivity.this, EditClassActivity.class);
                        intent.putExtra(EditClassActivity.EXTRA_CLASS_ID, cls.id);
                        startActivity(intent);
                        break;
                    case ClassOptionsDialog.DELETE_INDEX:
                        SQL.deleteClassAndEmptyCourse(MainActivity.this, cls);
                        Toast.makeText(MainActivity.this, getString(R.string.class_deleted), Toast.LENGTH_SHORT).show();
                        refreshData();
                        break;
                }
            }
        };

        _dialogOptions = ClassOptionsDialog.show(this, cls.course.name, listener);
    }

    //========================================================================= PROPERTIES
    private boolean isClash(int index)
    {
        Class c = _classes.get(index);

        // above
        if (index > 0 && c.isClash(_classes.get(index - 1)))
            return true;

        // below
        if (index < _classes.size() - 1 && c.isClash(_classes.get(index + 1)))
            return true;

        return false;
    }

    //========================================================================= EVENTS
    public final boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public final boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuNewClass:
                startActivity(new Intent(this, EditClassActivity.class));
                return true;
            default:
                return _drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    public final void onBackPressed()
    {
        if (_layDrawer.isDrawerOpen(_drawer))
            _layDrawer.closeDrawer(_drawer);
        else if (_viewPager.getCurrentItem() == MainPagerAdapter.WEEK_GRID_INDEX && _weekGridView.tableView().isClassSelected())
            _weekGridView.tableView().deselectClass();
        else
            super.onBackPressed();
    }
    protected final void onPause()
    {
        super.onPause();

        if (_dialogAbout != null && _dialogAbout.isShowing())
            _dialogAbout.dismiss();

        if (_dialogOptions != null && _dialogOptions.isShowing())
            _dialogOptions.dismiss();
    }
    protected final void onResume()
    {
        super.onResume();
        refreshData();
    }

    public final void onSTSClick(View view)
    {
        _layDrawer.closeDrawer(_drawer);
        startActivity(new Intent(this, SchoolsActivity.class));
    }
    public final void onImportClick(View view)
    {
        _layDrawer.closeDrawer(_drawer);
        startActivity(new Intent(this, ImportActivity.class));
    }
    public final void onAboutClick(View view)
    {
        _dialogAbout = AboutDialog.show(this);
    }

    public final void onClick(Class c)
    {
        if (c.isFromSTS())
        {
            Intent intent = new Intent(this, ClassesActivity.class);
            intent.putExtra(ClassesActivity.EXTRA_OFFERING_ID, c.course.offeringId);
            startActivity(intent);
        }
        else
            Toast.makeText(this, getString(R.string.no_sts_data_available), Toast.LENGTH_SHORT).show();
    }
    public final void onLongClick(Class c)
    {
        showOptionsDialog(c);
    }
}
