package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        String nextActivity = getIntent().getStringExtra("nextActivity");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if ("GraphActivity".equals(nextActivity)) {
                    double a = getIntent().getDoubleExtra("a", 0);
                    double b = getIntent().getDoubleExtra("b", 0);
                    double c = getIntent().getDoubleExtra("c", 0);
                    intent = new Intent(LoadingActivity.this, GraphActivity.class);
                    intent.putExtra("a", a);
                    intent.putExtra("b", b);
                    intent.putExtra("c", c);
                } else if ("Start".equals(nextActivity)) {
                    intent = new Intent(LoadingActivity.this, Start.class);
                } else {
                    intent = new Intent(LoadingActivity.this, Start.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
