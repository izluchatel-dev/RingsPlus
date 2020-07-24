package ru.ringsplus.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.ringsplus.app.model.AppOptions;

public class UserActivity extends AppCompatActivity {

    public static final String USER_NAME_PUT = "userName";

    private EditText mUserNameEdit;
    private Button mSaveButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mUserNameEdit = findViewById(R.id.userName);
        mSaveButton = findViewById(R.id.userSaveButton);

        updateUserName();

        mUserNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkSaveButtonEnabled();
            }
        });

       mSaveButton.setOnClickListener( view -> {
           String userName = String.valueOf(mUserNameEdit.getText()).trim();
           AppOptions.getInstance().setUserName(this, userName);

           Intent intent = new Intent();
           intent.putExtra(USER_NAME_PUT, userName);
           setResult(RESULT_OK, intent);

           finish();
       });
    }

    private void checkSaveButtonEnabled() {
       if (mUserNameEdit.length() != 0) {
            mSaveButton.setEnabled(true);
            mSaveButton.setBackgroundResource(R.color.colorPrimary);
       } else {
            mSaveButton.setEnabled(false);
            mSaveButton.setBackgroundResource(R.color.disabledColor);
       }
    }

    private void updateUserName() {
        mUserNameEdit.setText(AppOptions.getInstance().getUserName(this).trim());
        mUserNameEdit.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mUserNameEdit, InputMethodManager.SHOW_IMPLICIT);
        checkSaveButtonEnabled();
    }


}