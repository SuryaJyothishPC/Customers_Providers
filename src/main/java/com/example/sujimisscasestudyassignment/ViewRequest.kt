package com.example.sujimisscasestudyassignment


import android.app.VoiceInteractor
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.copy
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.ArrayList

class ViewRequest : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestList: ArrayList<Request>
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_request)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recycler2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        requestList = arrayListOf()
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        db = FirebaseFirestore.getInstance()
        db.collection("Requests").whereEqualTo("ProviderID",userId).get()
            .addOnSuccessListener {  document ->
                if(document != null){
                    for (data in document.documents){
                        val request: Request? = data.toObject(Request::class.java)
                        if (request != null) {
                            val updatedRequest = request.copy(RequestID = data.id) // Ensure field names match
                            requestList.add(updatedRequest)
                        }

                    }
                    recyclerView.adapter= AdapterView(requestList)
                }
                Log.d("Firestore", "Documents found: ${document.documents.size}")
            }

            .addOnFailureListener {
                Log.d("Firestore", "Error fetching requests")
                Toast.makeText(applicationContext,"Error while loading data",
                    Toast.LENGTH_SHORT).show() }

    }
}