package com.wokesolutions.ignes.ignes;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RandomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random);

        final EditText nif = findViewById(R.id.nif_edit);
        final EditText report = findViewById(R.id.report_edit);
        Button aceitar = findViewById(R.id.aceitar);
        final Context context = this;

            aceitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestsVolley.reportAcceptApplicationRequest(report.getText().toString(), nif.getText().toString(), context);
                }
            });



    }


}
