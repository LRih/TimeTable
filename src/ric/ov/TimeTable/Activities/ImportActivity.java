package ric.ov.TimeTable.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.Data.SQL;
import ric.ov.TimeTable.R;
import ric.ov.TimeTable.STS.ImportTask;
import ric.ov.TimeTable.STS.STSManager;
import ric.ov.TimeTable.Utils.Task;
import ric.ov.TimeTable.Views.Dialogs.ImportConfirmDialog;

import java.util.List;

public final class ImportActivity extends BackActivity implements Task.OnTaskCompleteListener<List<Class>>, ImportConfirmDialog.OnConfirmListener, DialogInterface.OnDismissListener
{
    //========================================================================= VARIABLES
    private Dialog _dialog;

    private EditText _txtInput;
    private View _layButtons;
    private View _layProgress;

    private List<Class> _classes;
    private ImportTask _task;

    private State _state;

    //========================================================================= INITIALIZE
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState, R.layout.activity_import);

        initializeViews();

        restoreState();
    }

    private void initializeViews()
    {
        _txtInput = (EditText)findViewById(R.id.txtInput);

        _layButtons = findViewById(R.id.layButtons);
        _layProgress = findViewById(R.id.layProgress);
    }

    private void restoreState()
    {
        if (getLastCustomNonConfigurationInstance() != null)
        {
            Object[] state = (Object[])getLastCustomNonConfigurationInstance();

            _state = (State)state[0];

            switch (_state)
            {
                case Parsing:
                    disableUI();
                    _task = (ImportTask)state[1];
                    _task.setOnTaskCompleteListener(this);
                    break;

                case Parsed:
                    disableUI();
                    _layProgress.setVisibility(View.GONE);
                    showConfirmDialog((List<Class>)state[2]);
                    break;
            }
        }
        else
            _state = State.Idle;
    }

    //========================================================================= FUNCTIONS
    private void startLoadingTask()
    {
        _state = State.Parsing;

        if (_task != null)
        {
            _task.cancel(true);
            _task = null;
        }

        _task = new ImportTask(_txtInput.getText().toString());
        _task.setOnTaskCompleteListener(this);
        _task.executeOnThreadPool();
    }

    private void enableUI()
    {
        _txtInput.setEnabled(true);
        _layButtons.setVisibility(View.VISIBLE);
        _layProgress.setVisibility(View.GONE);
    }
    private void disableUI()
    {
        _txtInput.setEnabled(false);
        _layButtons.setVisibility(View.INVISIBLE);
        _layProgress.setVisibility(View.VISIBLE);
    }

    private void showConfirmDialog(List<Class> classes)
    {
        _classes = classes;
        _dialog = ImportConfirmDialog.show(this, _classes, this, this);
    }

    //========================================================================= EVENTS
    public final Object onRetainCustomNonConfigurationInstance()
    {
        if (_task != null)
            _task.setOnTaskCompleteListener(null);

        return new Object[] { _state, _task, _classes };
    }
    protected final void onPause()
    {
        super.onPause();

        if (_dialog != null && _dialog.isShowing())
            _dialog.dismiss();
    }

    public final void onSTSClick(View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(STSManager.URL_WEEKLY_VIEW));
        startActivity(intent);
    }
    public final void onOKClick(View view)
    {
        disableUI();

        startLoadingTask();
    }

    public final void onTaskComplete(List<Class> result, int errorCode)
    {
        switch (errorCode)
        {
            case ImportTask.ERROR_NONE:
                _state = State.Parsed;

                _layProgress.setVisibility(View.GONE);
                showConfirmDialog(result);
                break;

            case ImportTask.ERROR_CONNECTION:
                _state = State.Idle;

                enableUI();
                Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                break;

            default:
                _state = State.Idle;

                enableUI();
                Toast.makeText(this, getString(R.string.unspecified_error), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public final void onConfirm()
    {
        boolean partialFailure = false;

        for (Class cls : _classes)
        {
            try
            {
                SQL.insertClassIfAbsent(this, cls);
            }
            catch (Exception ex)
            {
                partialFailure = true;
            }
        }

        Toast.makeText(this, getString(partialFailure ? R.string.some_classes_imported : R.string.classes_imported), Toast.LENGTH_SHORT).show();
        finish();
    }
    public final void onDismiss(DialogInterface dialog)
    {
        _state = State.Idle;
        enableUI();
    }

    //========================================================================= CLASSES
    private enum State
    {
        Idle, Parsing, Parsed
    }
}
