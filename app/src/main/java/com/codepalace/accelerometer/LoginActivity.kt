package com.codepalace.accelerometer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    var edUsername: EditText? = null
    var edPassword: EditText? = null
    var btn: Button? = null
    var tv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        edUsername = findViewById(R.id.editTextLogUsername)
        edPassword = findViewById(R.id.editTextLogPassword)
        btn = findViewById(R.id.buttnLog)
        tv = findViewById(R.id.textViewNewUser)
        btn?.setOnClickListener(View.OnClickListener {
            val username = edUsername?.text.toString()
            val password = edPassword?.text.toString()
            val db = DataBase(applicationContext, "SOSFall", null, 1)
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please complete in the fields!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (db.login(username, password) == 1) {
                    Toast.makeText(applicationContext, "Login Succesful!", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Invalid password or user!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        tv?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        })
    }
}