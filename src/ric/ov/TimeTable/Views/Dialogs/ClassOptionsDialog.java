package ric.ov.TimeTable.Views.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import ric.ov.TimeTable.R;

public final class ClassOptionsDialog
{
    public static final int EDIT_INDEX = 0;
    public static final int DELETE_INDEX = 1;

    private ClassOptionsDialog()
    {
        throw new AssertionError();
    }

    public static Dialog show(Context context, String title, DialogInterface.OnClickListener listener)
    {
        final AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(title)
            .setAdapter(ArrayAdapter.createFromResource(context, R.array.options, R.layout.item_dialog), listener)
            .create();

        dialog.show();

        return dialog;
    }
}
