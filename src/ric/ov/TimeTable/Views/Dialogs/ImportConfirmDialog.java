package ric.ov.TimeTable.Views.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ric.ov.TimeTable.Data.Class;
import ric.ov.TimeTable.R;

import java.util.List;

public final class ImportConfirmDialog
{
    private ImportConfirmDialog()
    {
        throw new AssertionError();
    }

    public static Dialog show(Context context, List<Class> classes, OnConfirmListener confirmListener, DialogInterface.OnDismissListener dismissListener)
    {
        AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(R.string.classes)
            .setPositiveButton(R.string.confirm, createOKListener(confirmListener))
            .setNegativeButton(R.string.cancel, null)
            .setOnDismissListener(dismissListener)
            .create();

        // inflate
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_import_confirm, null, false);

        addClasses((ViewGroup)view.findViewById(R.id.layClasses), classes);

        dialog.setView(view);
        dialog.show();

        return dialog;
    }

    private static void addClasses(ViewGroup layout, List<Class> classes)
    {
        LayoutInflater inflater = (LayoutInflater)layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (Class cls : classes)
        {
            View classView = inflater.inflate(R.layout.item_import_class, layout, false);

            TextView lblStart = (TextView)classView.findViewById(R.id.lblStart);
            TextView lblDay = (TextView)classView.findViewById(R.id.lblDay);
            TextView lblCourse = (TextView)classView.findViewById(R.id.lblCourse);
            TextView lblType = (TextView)classView.findViewById(R.id.lblType);

            lblStart.setText(cls.start.toTimeString());
            lblDay.setText(cls.day.abbreviation());
            lblCourse.setText(cls.course.name);
            lblType.setText(cls.type);

            layout.addView(classView);
        }

        if (classes.isEmpty())
            layout.findViewById(R.id.lblEmpty).setVisibility(View.VISIBLE);
    }

    private static DialogInterface.OnClickListener createOKListener(final OnConfirmListener listener)
    {
        return new DialogInterface.OnClickListener()
        {
            public final void onClick(DialogInterface dialog, int which)
            {
                if (listener != null)
                    listener.onConfirm();
            }
        };
    }

    public interface OnConfirmListener
    {
        void onConfirm();
    }
}
