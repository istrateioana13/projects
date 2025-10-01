package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Signup extends AppCompatActivity {

    private EditText editTextEmail, editTextUsername, editTextPassword, editTextConfirmPassword;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(Signup.this);
                    boolean inserted = dbHelper.insertUser(email, username, password);
                    if (inserted) {
                        Toast.makeText(Signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signup.this, LoadingActivity.class);
                        intent.putExtra("nextActivity", "Start");
                        startActivity(intent);
                    } else {
                        Toast.makeText(Signup.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
