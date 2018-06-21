package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private Button mChangePasswordButton;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mContext = this;

        mChangePasswordButton = findViewById(R.id.changepassword_button);

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setTitle("Change Password");
                mBuilder.setIcon(R.drawable.settingsicon);

                LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                final View mView = inflater.inflate(R.layout.change_password, null);
                mBuilder.setView(mView);
                final AlertDialog alert = mBuilder.create();

                alert.show();

                final EditText oldPassword = mView.findViewById(R.id.current_password);
                final EditText newPassword = mView.findViewById(R.id.new_password);
                EditText confirmNewPassword = mView.findViewById(R.id.confirm_new_password);

                Button changePassButton = mView.findViewById(R.id.submit_pass_change);
                changePassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldPass = oldPassword.getText().toString();
                        String newPass = newPassword.getText().toString();

                        System.err.println("OLD: " + oldPass + " NEW: "+newPass);

                        RequestsVolley.changePasswordRequest(oldPass, newPass, mContext);
                    }
                });
            }
        });

    }
}
