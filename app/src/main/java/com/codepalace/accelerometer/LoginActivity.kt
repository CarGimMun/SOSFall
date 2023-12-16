package com.codepalace.accelerometer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
            val username = edUsername?.text.toString().trim()
            val password = edPassword?.text.toString().trim()
            val db = DataBase(applicationContext, "SOSFall", null, 5)
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please complete in the fields!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (db.login(username, password) == 1) {
                    Toast.makeText(applicationContext, "Login Succesful!", Toast.LENGTH_SHORT).show()
                    /* val sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().apply {
                        putString("username", username)
                        putString("password",password)
                    }*/
                    val intent =  Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("username",username)
                    intent.putExtra("password",password)
                    startActivity(intent)
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