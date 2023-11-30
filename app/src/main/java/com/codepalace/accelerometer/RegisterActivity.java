package com.codepalace.accelerometer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText edUsername, edEmail, edPassword, edConfirm;
    TextView tv;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edUsername=findViewById(R.id.editTextRegUsername);
        edEmail=findViewById(R.id.editTextRegEmail);
        edPassword=findViewById(R.id.editTextRegPassword);
        edConfirm =findViewById(R.id.editTextRegConfirm);
        btn=findViewById(R.id.buttnReg);
        tv=findViewById(R.id.textViewExistingUser);


    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String username = edUsername.getText().toString();
            String email = edEmail.getText().toString();
            String password = edPassword.getText().toString();
            String confirm = edConfirm.getText().toString();
            DataBase db=new DataBase(getApplicationContext(),"SOSFall",null,1);
            if(username.length()==0||email.length()==0||password.length()==0||confirm.length()==0) {
                Toast.makeText(getBaseContext(), "Please complete all the fields", Toast.LENGTH_SHORT).show();

            }
            else {
                if (password.compareTo(confirm)==0) {
                    db.register(username,email,password);
                    Toast.makeText(getBaseContext(), "Successful Registration", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                }else{
                    Toast.makeText(getBaseContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                }

        }
    }
    });

    tv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        }
    });
    }
}
