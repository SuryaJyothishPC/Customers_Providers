package com.example.sujimisscasestudyassignment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdapterView(private val requestList : ArrayList<Request>) : RecyclerView.Adapter<AdapterView.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val serviceName: TextView = itemView.findViewById(R.id.serviceName2)
        val providerId: TextView = itemView.findViewById(R.id.providerID2)
        val time: TextView = itemView.findViewById(R.id.timeList2)
        val price: TextView = itemView.findViewById(R.id.listPrice2)
        val dateInput: TextView = itemView.findViewById(R.id.date)
        val timeInput: TextView = itemView.findViewById(R.id.Time3)
        val timeInput2: TextView = itemView.findViewById(R.id.Time4)
        val Status: TextView = itemView.findViewById(R.id.requestStatus2)
        val requestButton: Button = itemView.findViewById(R.id.requestButton2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.listrequest, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val service = requestList[position]

        // Setting values in the list item
        holder.serviceName.text = service.ServiceType
        holder.time.text = service.ServiceTime
        holder.price.text = service.ServicePrice
        holder.providerId.text = service.ProviderID
        holder.Status.text = service.RequestStatus
        holder.timeInput.text = service.ScheduledTimeFrom
        holder.timeInput2.text = service.ScheduledTimeTo
        holder.dateInput.text = service.ScheduledDate
        holder.Status.text = service.RequestStatus

        val db = FirebaseFirestore.getInstance()
        val Id = service.RequestID

        holder.requestButton.setOnClickListener {

            db.collection("Requests") // Replace with your actual Firestore collection name
                .document(Id)
                .update("RequestStatus", "Accepted")
                .addOnSuccessListener {
                    Log.d("Firestore", "Status updated to Accepted")
                    holder.Status.text = "Accepted" // Update UI after Firestore update
                    holder.requestButton.text = "Accepted"
                    holder.requestButton.isEnabled = false
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error updating status", e)
                }
        }
    }
}