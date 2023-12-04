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
    var tv: TextView? = null
    var btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        edUsername = findViewById(R.id.editTextRegUsername)
        edEmail = findViewById(R.id.editTextRegEmail)
        edContact = findViewById(R.id.editTextRegContact)
        edPassword = findViewById(R.id.editTextRegPassword)
        edConfirm = findViewById(R.id.editTextRegConfirm)
        btn = findViewById(R.id.buttnReg)
        tv = findViewById(R.id.textViewExistingUser)
        btn?.setOnClickListener(View.OnClickListener {
            val username = edUsername?.getText().toString()
            val email = edEmail?.getText().toString()
            val password = edPassword?.getText().toString()
            val confirm = edConfirm?.getText().toString()
            val contactString = edContact?.getText().toString()
            val contact = contactString.toInt()
            Toast.makeText(baseContext, "Antes del if", Toast.LENGTH_SHORT).show()
            if (username.isEmpty()){
                Toast.makeText(baseContext, "Después del if", Toast.LENGTH_SHORT).show()
            }
            val db = DataBase(applicationContext, "SOSFall", null, 1)
            if (username.isEmpty() || email.isEmpty()|| contactString.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(baseContext, "Please complete all the fields", Toast.LENGTH_SHORT).show()
            } else {
                if (password.compareTo(confirm) == 0) {
                    db.register(username, email, contact, password)
                    Toast.makeText(baseContext, "Successful Registration", Toast.LENGTH_SHORT)
                        .show()
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