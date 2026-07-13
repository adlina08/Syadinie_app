package com.example.syadinie_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(username.isEmpty() || password.isEmpty()){

                Toast.makeText(LoginActivity.this,
                        "Please enter username and password correctly",
                        Toast.LENGTH_SHORT).show();

            }else{

                boolean check = databaseHelper.checkUser(username,password);

                if(check){

                    Toast.makeText(LoginActivity.this,
                            "Login Successful",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);

                    intent.putExtra("USERNAME",username);

                    startActivity(intent);

                    finish();

                }else{

                    Toast.makeText(LoginActivity.this,
                            "Invalid Username or Password",
                            Toast.LENGTH_SHORT).show();

                }

            }

        });

    }
}