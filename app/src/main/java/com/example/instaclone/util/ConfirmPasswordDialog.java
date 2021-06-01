package com.example.instaclone.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.example.instaclone.AccountSettings.EditProfileFragment;
import com.example.instaclone.R;
import java.util.Arrays;
import java.util.Objects;

public class ConfirmPasswordDialog extends DialogFragment {
    private static final String TAG = "ConfirmPasswordDialog";
    private TextView cancel;
    private TextView confirm;
    OnConfirmPasswordListener onConfirmPasswordListener;
    private EditText password;

    public interface OnConfirmPasswordListener {
        void confirmPassword(String str);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starting dialog");
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        this.cancel = (TextView) view.findViewById(R.id.cancel);
        this.confirm = (TextView) view.findViewById(R.id.confirm);
        this.password = (EditText) view.findViewById(R.id.textPassword);
        this.cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(ConfirmPasswordDialog.TAG, "onClick: closing dialog");
                ((Dialog) Objects.requireNonNull(ConfirmPasswordDialog.this.getDialog())).dismiss();
            }
        });
        this.confirm.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(ConfirmPasswordDialog.TAG, "onClick: confirming password");
                EditProfileFragment.progressBar.setVisibility(View.VISIBLE);
                ConfirmPasswordDialog.this.onConfirmPasswordListener.confirmPassword(ConfirmPasswordDialog.this.password.getText().toString());
                ((Dialog) Objects.requireNonNull(ConfirmPasswordDialog.this.getDialog())).dismiss();
            }
        });
        return view;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.onConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        } catch (ClassCastException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onAttach: ClassCastException");
            stringBuilder.append(Arrays.toString(e.getStackTrace()));
            Log.d(TAG, stringBuilder.toString());
        }
    }
}
