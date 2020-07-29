package ru.ringsplus.app.utils;

import android.app.Activity;
import android.app.ProgressDialog;

import ru.ringsplus.app.R;

public class ProgressDialogWait {

    private ProgressDialog mDialog;

    public ProgressDialogWait(Activity activity) {
        mDialog = new ProgressDialog(activity);
        mDialog.setCanceledOnTouchOutside(false);
    }

    public void showDialog() {
        if (mDialog != null) {
            mDialog.setMessage(mDialog.getContext().getString(R.string.wait_dialog));
            mDialog.show();
        }
    }

    public void hideDialog() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }
}
