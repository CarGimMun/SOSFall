package com.codepalace.accelerometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText edUsername, edPassword;
    Button btn;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edUsername=findViewById(R.id.editTextLogUsername);
        edPassword=findViewById(R.id.editTextLogPassword);
        btn=findViewById(R.id.buttnLog);
        tv=findViewById(R.id.textViewNewUser);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                DataBase db=new DataBase(getApplicationContext(),"SOSFall",null,1);
                if(username.length() == 0 ||password.length()==0){
                    Toast.makeText(getApplicationContext(), "Please complete in the fields!", Toast.LENGTH_SHORT).show();
                } else {
                    if(db.login(username,password)==1) {
                        Toast.makeText(getApplicationContext(), "Login Succesful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }else{
                        Toast.makeText(getApplicationContext(), "Invalid password or user!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

    }
}