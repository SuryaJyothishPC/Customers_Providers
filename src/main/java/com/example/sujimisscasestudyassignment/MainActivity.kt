package com.example.sujimisscasestudyassignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val radiogrp = findViewById<RadioGroup>(R.id.radiogrp);
        val button = findViewById<Button>(R.id.start);

        button.setOnClickListener{
            val selectedRadioId = radiogrp.checkedRadioButtonId
            if(selectedRadioId != -1){
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioId)
                Log.d("MainActivity", "Selected: ${selectedRadioButton.text}")
                when (selectedRadioButton.text.toString()){
                    "Customer" -> startActivity(Intent(this, customerLogin::class.java))
                    "Service Provider" -> startActivity(Intent(this, serviceLogin::class.java))
                }
            }
        }
    }
}