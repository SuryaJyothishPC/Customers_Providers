package com.example.sujimisscasestudyassignment

import com.google.firebase.auth.FirebaseAuth
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class addservices : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_addservices)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val auth = FirebaseAuth.getInstance()

        val db = Firebase.firestore
        val service = findViewById<EditText>(R.id.srvc_name)
        val price = findViewById<EditText>(R.id.price)
        val time = findViewById<EditText>(R.id.time_srvc)
        val submit = findViewById<Button>(R.id.submit)

        submit.setOnClickListener {
            val serviceName = service.text.toString()
            val pr = price.text.toString()
            val timeSrv = time.text.toString()

//            fun getCurrentUserId() : String?{
//                val user = FirebaseAuth.getInstance().currentUser
//                return user?.uid
//            }
//
//            val userId = getCurrentUserId()
            val user = auth.currentUser
            val userId = user?.uid

            val service = hashMapOf(
                "ServiceType" to serviceName,
                "ServiceProvider" to userId,
                "Price" to pr,
                "Time" to timeSrv
            )

            if (userId != null) {
                db.collection("Services").document(userId).set(service)
                    .addOnSuccessListener {
                        Log.d(TAG,"Service added succesfully")
                        Toast.makeText(applicationContext,"Service added succesfully",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Log.d(TAG,"Error while adding")
                        Toast.makeText(applicationContext,"Error while adding",Toast.LENGTH_SHORT).show()
                    }
            }
            else{
                Log.d(TAG,"Empty userID")
            }

        }

    }
}