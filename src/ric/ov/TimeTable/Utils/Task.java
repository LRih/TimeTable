package ric.ov.TimeTable.Utils;

import android.os.AsyncTask;
import android.os.Build;

import java.io.IOException;

public abstract class Task<T> extends AsyncTask<Void, Void, T>
{
    //========================================================================= VARIABLES
    public static final int ERROR_NONE = 0;
    public static final int ERROR_CONNECTION = 1;
    public static final int ERROR_UNSPECIFIED = 2;

    private int _errorCode = ERROR_NONE;

    private OnTaskCompleteListener<T> _listener;

    //========================================================================= FUNCTIONS
    protected T doInBackground(Void... params)
    {
        T result = null;

        try
        {
            result = doInBackground();
        }
        catch (IOException ex)
        {
            _errorCode = ERROR_CONNECTION;
            ex.printStackTrace();
        }
        catch (Exception ex)
        {
            _errorCode = ERROR_UNSPECIFIED;
            ex.printStackTrace();
        }

        return result;
    }

    protected abstract T doInBackground() throws Exception;

    public final void executeOnThreadPool()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            execute();
        else
            executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    //========================================================================= PROPERTIES
    public final void setOnTaskCompleteListener(OnTaskCompleteListener<T> listener)
    {
        _listener = listener;
    }

    //========================================================================= EVENTS
    protected final void onPostExecute(T result)
    {
        if (_listener != null)
            _listener.onTaskComplete(result, _errorCode);
    }

    //========================================================================= CLASSES
    public interface OnTaskCompleteListener<T>
    {
        void onTaskComplete(T result, int errorCode);
    }
}
