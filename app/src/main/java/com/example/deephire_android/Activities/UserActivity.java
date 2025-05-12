package com.example.deephire_android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deephire_android.R;

public class UserActivity extends AppCompatActivity {

    TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        this.tvEmail = (TextView) findViewById(R.id.tvEmail);


        Intent intent = getIntent();

        String email = intent.getStringExtra("email");
        if (email != null){
            tvEmail.setText(email);
        }

    }
}