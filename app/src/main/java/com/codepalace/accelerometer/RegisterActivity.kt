package com.codepalace.accelerometer

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
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
            if (username.isEmpty() || email.isEmpty() || contact.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(baseContext, "Please complete all the fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if(!db.validUser(username)){
                    Toast.makeText(baseContext, "This username is taken", Toast.LENGTH_SHORT).show()
                }else{
                    if (!isValidEmail(email)) {
                        Toast.makeText(baseContext, "Invalid email", Toast.LENGTH_SHORT).show()
                    }else{
                        if(!isValidContact(contact.toInt())){
                            Toast.makeText(baseContext, "Invalid Phone Number", Toast.LENGTH_SHORT)
                                .show()
                        }else{
                            if (password.compareTo(confirm) == 0) {
                                if (db.login(username, password) == 0) {
                                    db.register(username, email, contact, password)
                                    Toast.makeText(baseContext, "Successful Registration", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        baseContext,
                                        "You have already an account",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                }
                            } else {
                                Toast.makeText(baseContext, "Password doesn't match", Toast.LENGTH_SHORT).show()
                            } 
                        }
                    }
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

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    fun isValidContact(contact:Int):Boolean {
        var result:Boolean=false
        var count = 0
        var num = 1234567

        while (num != 0) {
            num /= 10
            ++count
        }
        if(count==9){
            result=true
        }
        return result
    }
}