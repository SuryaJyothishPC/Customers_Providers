package com.example.sujimisscasestudyassignment

import android.util.Log
import android.content.ContentValues.TAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Adapter(private val serviceList: ArrayList<Services>) :
    RecyclerView.Adapter<Adapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceName: TextView = itemView.findViewById(R.id.serviceName)
        val providerId: TextView = itemView.findViewById(R.id.providerID)
        val time: TextView = itemView.findViewById(R.id.timeList)
        val price: TextView = itemView.findViewById(R.id.listPrice)
        val dateInput: EditText = itemView.findViewById(R.id.date)
        val timeInput: EditText = itemView.findViewById(R.id.time)
        val timeInput2: EditText = itemView.findViewById(R.id.time2)
        val Status: TextView = itemView.findViewById(R.id.requestStatus)
        val requestButton: Button = itemView.findViewById(R.id.requestButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.listitem, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val service = serviceList[position]

        // Setting values in the list item
        holder.serviceName.text = service.ServiceType
        holder.time.text = service.Time
        holder.price.text = service.Price
        holder.providerId.text = service.ServiceProvider



        holder.requestButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid
            val enteredDate = holder.dateInput.text.toString().trim()
            val enteredTime = holder.timeInput.text.toString().trim()
            val enteredTimeTo = holder.timeInput2.text.toString().trim()
            val crntStatus = holder.Status.text.toString().trim()

            if (userId != null && enteredDate.isNotEmpty() && enteredTime.isNotEmpty()) {
                val request = hashMapOf(
                    "CustomerId" to userId,
                    "ServiceType" to service.ServiceType,
                    "ProviderID" to service.ServiceProvider,
                    "ServicePrice" to service.Price,
                    "ServiceTime" to service.Time,
                    "ScheduledDate" to enteredDate,
                    "ScheduledTimeFrom" to enteredTime,
                    "ScheduledTimeTo" to enteredTimeTo,
                    "RequestStatus" to "requested"
                )

                FirebaseFirestore.getInstance().collection("Requests").add(request)
                    .addOnSuccessListener {
                        Log.d(TAG, "Service requested successfully")
                        Toast.makeText(holder.itemView.context, "Request Sent", Toast.LENGTH_SHORT).show()
                        holder.Status.text="Requested"
                        holder.requestButton.isEnabled = false
                        holder.requestButton.text = "Requested"
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Request failed")
                        Toast.makeText(holder.itemView.context, "Failed to send request", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(holder.itemView.context, "Please enter date and time", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
