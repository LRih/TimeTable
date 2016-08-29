package ric.ov.TimeTable.Views.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import ric.ov.TimeTable.R;

import java.util.Calendar;

public final class AboutDialog
{
    private AboutDialog()
    {
        throw new AssertionError();
    }

    public static Dialog show(Context context)
    {
        AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(R.string.app_name)
            .setIcon(R.drawable.icon)
            .setMessage(context.getString(R.string.about_msg, getVersionName(context), createCopyrightYear(2016)))
            .create();

        dialog.show();

        return dialog;
    }

    private static String getVersionName(Context context)
    {
        String versionName = "";
        try { versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName; }
        catch (Exception ex) { ex.printStackTrace(); }
        return versionName;
    }

    private static String createCopyrightYear(int startYear)
    {
        String text = "";
        int year = Calendar.getInstance().get(Calendar.YEAR);

        if (startYear != year)
        {
            text += "-";
            if (year > startYear)
                text += year;
        }

        return startYear + text;
    }
}
