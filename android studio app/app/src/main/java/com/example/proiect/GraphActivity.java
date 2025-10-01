package com.example.proiect;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class GraphActivity extends AppCompatActivity {

    private CustomGraphView customGraphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph2);

        customGraphView = findViewById(R.id.customGraphView);

        double a = getIntent().getDoubleExtra("a", 0);
        double b = getIntent().getDoubleExtra("b", 0);
        double c = getIntent().getDoubleExtra("c", 0);
        double d = getIntent().getDoubleExtra("d", 0);

        customGraphView.setCoefficients(a, b, c, d);
    }
}
