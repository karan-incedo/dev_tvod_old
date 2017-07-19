package com.viewlift.views.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.viewlift.R;

public class AppCMSHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toast.makeText(this, "History Activity!", Toast.LENGTH_SHORT).show();
    }
}
