package com.codepalace.accelerometer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/*class ContactsActivity : AppCompatActivity() {
    var edContact: EditText? = null
    var edNumber: EditText? = null
    var tv: TextView? = null
    var btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        edContact = findViewById(R.id.editTextContactName)
        edNumber = findViewById(R.id.editTextContactNum)
        btn = findViewById(R.id.buttnContact)
        tv = findViewById(R.id.textViewContactRegistred)
        btn?.setOnClickListener(View.OnClickListener {
            val contact = edContact?.text.toString()
            val number = edNumber?.text.toString()
            val db = DataBase(applicationContext, "Contacts", null, 1)
            if (contact.isEmpty() || number.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please complete in the fields!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                db.register(contact)
                Toast.makeText(applicationContext, "Emergency number saved!", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@ContactsActivity, LoginActivity::class.java))

                }
            })

  }
}*/