package com.example.sujimisscasestudyassignment

import android.os.Bundle
import android.widget.Adapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class home : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceList : ArrayList<Services>
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        serviceList = arrayListOf()
        db = FirebaseFirestore.getInstance()
        db.collection("Services").get()
            .addOnSuccessListener {  document ->
                if(document != null){
                    for (data in document.documents){
                        val service : Services? = data.toObject(Services::class.java)
                        if (service != null){
                            serviceList.add(service)
                        }
                    }
                    recyclerView.adapter= Adapter(serviceList)
                }
            }
            .addOnFailureListener { Toast.makeText(applicationContext,"Error while loading data",Toast.LENGTH_SHORT).show() }

    }
}