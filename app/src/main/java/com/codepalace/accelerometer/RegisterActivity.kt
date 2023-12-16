package com.codepalace.accelerometer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    var edUsername: EditText? = null
    var edEmail: EditText? = null
    var edContact: EditText? = null
    var edPassword: EditText? = null
    var edConfirm: EditText? = null
    var bttn: Button? = null
    var tv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        edUsername = findViewById(R.id.editTextRegUsername)
        edEmail = findViewById(R.id.editTextRegEmail)
        edContact = findViewById(R.id.editTextRegContact)
        edPassword = findViewById(R.id.editTextRegPassword)
        edConfirm = findViewById(R.id.editTextRegConfirm)
        bttn = findViewById(R.id.buttnReg)
        tv = findViewById(R.id.textViewExistingUser)
        bttn?.setOnClickListener(View.OnClickListener {
            val username = edUsername?.getText().toString().trim()
            val email = edEmail?.getText().toString().trim()
            val contact = edContact?.getText().toString().trim()
            val password = edPassword?.getText().toString().trim()
            val confirm = edConfirm?.getText().toString().trim()
            val db = DataBase(applicationContext, "SOSFall", null, 5)
            var emailPattern ="[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+"
            if (username.isEmpty() || email.isEmpty() ||contact.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                if(email.matches(emailPattern.toRegex())){
                    Toast.makeText(baseContext, "Please complete all the fields", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (password.compareTo(confirm) == 0) {
                    if (db.login(username,password)==0){
                        db.register(username, email,contact, password)
                        Toast.makeText(baseContext, "Successful Registration", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(baseContext, "You have already an account", Toast.LENGTH_SHORT).show()
                    }
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Password doesn't match", Toast.LENGTH_SHORT).show()
                }
            }
        })
        tv?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )
        })
    }
}