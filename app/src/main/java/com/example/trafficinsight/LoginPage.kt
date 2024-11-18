package com.example.trafficsigns

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Intents
import android.widget.Button

class LoginPage : AppCompatActivity() {
    private lateinit var Start_btn : Button
    private lateinit var Quit_btn : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)




        Start_btn=findViewById(R.id.Start)
        Start_btn.setOnClickListener {
           val intent_to_app= Intent(this@LoginPage,MainActivity::class.java)
            startActivity(intent_to_app)
        }


        Quit_btn=findViewById(R.id.quit)
        Quit_btn.setOnClickListener {
            finishAffinity()
        }



    }
}