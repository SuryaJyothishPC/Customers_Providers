package com.example.sujimisscasestudyassignment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class customerRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        val name = findViewById<EditText>(R.id.name_reg)
        val phno = findViewById<EditText>(R.id.reg_phn)
        val send = findViewById<Button>(R.id.reg_send)
        val otp = findViewById<EditText>(R.id.otp_reg)
        val verify = findViewById<Button>(R.id.verify_reg)

        send.setOnClickListener {
            val phoneNumber = phno.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                phno.error = "Enter phone number"
            } else {
                val formattedNumber = formatPhoneNumber(phoneNumber)
                sendOTPReg(formattedNumber)
            }
        }

        verify.setOnClickListener {
            val otpCode = otp.text.toString().trim()
            if (otpCode.isEmpty()) {
                otp.error = "Enter OTP"
            } else {
                verifyOTPReg(otpCode)
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("FirebaseAuth", "onVerificationCompleted: $credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("FirebaseAuth", "onVerificationFailed", e)
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format"
                    is FirebaseTooManyRequestsException -> "SMS quota exceeded"
                    is FirebaseAuthMissingActivityForRecaptchaException -> "reCAPTCHA verification required"
                    else -> "Verification failed"
                }
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("FirebaseAuth", "onCodeSent: $verificationId")
                storedVerificationId = verificationId
                Toast.makeText(applicationContext, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("+")) {
            phoneNumber
        } else {
            "+91$phoneNumber"
        }
    }

    private fun sendOTPReg(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOTPReg(otp: String) {
        if (storedVerificationId == null) {
            Toast.makeText(applicationContext, "Verification ID is missing. Please resend OTP.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "signInWithCredential:success")
                    val user = task.result?.user
                    val userId = user?.uid

                    if (userId != null) {
                        saveUserData(userId)
                    }
                } else {
                    Log.w("FirebaseAuth", "signInWithCredential:failure", task.exception)
                    val errorMsg = if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        "Invalid OTP"
                    } else {
                        "OTP Verification failed"
                    }
                    Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(userId: String) {
        val name = findViewById<EditText>(R.id.name_reg).text.toString().trim()
        val phoneNumber = findViewById<EditText>(R.id.reg_phn).text.toString().trim()
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val userData = hashMapOf(
            "Name" to name,
            "PhoneNumber" to formatPhoneNumber(phoneNumber),
            "UserType" to "Customer",
            "RegistrationTimestamp" to formattedDate
        )

        val db = Firebase.firestore
        db.collection("Users").document(userId).set(userData)
            .addOnSuccessListener {
                Log.d("FirebaseFirestore", "User registered successfully")
                Toast.makeText(applicationContext, "Registration Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, home::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseFirestore", "Error saving user data", e)
                Toast.makeText(applicationContext, "Failed to register", Toast.LENGTH_SHORT).show()
            }
    }
}
