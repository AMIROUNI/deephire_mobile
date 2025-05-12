package com.example.deephire_android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deephire_android.R;

public class MainActivity extends AppCompatActivity {



    Button btnSignIn;
    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        this.btnSignIn=(Button)  findViewById(R.id.btnSignIn);
        this.btnGetStarted= (Button)  findViewById(R.id.btnGetStarted);

        btnSignIn.setOnClickListener(v -> goToSignIn(v));
        btnGetStarted.setOnClickListener(v -> goToGetStarted(v));



    }

    public  void  goToSignIn(View view){
        Intent intent = new Intent(MainActivity.this, SigInActivity.class);
        startActivity(intent);
    }


    public  void  goToGetStarted(View view ){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }



}