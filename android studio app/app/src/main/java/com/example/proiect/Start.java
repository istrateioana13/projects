package com.example.proiect;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        EditText editTextA = findViewById(R.id.editTextA);
        EditText editTextB = findViewById(R.id.editTextB);
        EditText editTextC = findViewById(R.id.editTextC);
        EditText editTextD = findViewById(R.id.editTextD);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double a = Double.parseDouble(editTextA.getText().toString());
                double b = Double.parseDouble(editTextB.getText().toString());
                double c = Double.parseDouble(editTextC.getText().toString());
                double d = Double.parseDouble(editTextD.getText().toString());

                Bundle bundle = new Bundle();
                bundle.putDouble("a", a);
                bundle.putDouble("b", b);
                bundle.putDouble("c", c);
                bundle.putDouble("d", d);

                Result resultFragment = new Result();
                resultFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, resultFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
