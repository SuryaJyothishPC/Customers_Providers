package com.example.sujimisscasestudyassignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class customerLogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_login)

        auth = Firebase.auth

        val phno = findViewById<EditText>(R.id.phn_no)
        val sendOtp = findViewById<Button>(R.id.send_otp)
        val otp = findViewById<EditText>(R.id.otp)
        val verifyOtp = findViewById<Button>(R.id.verify_otp)
        val register = findViewById<TextView>(R.id.register)

        register.setOnClickListener {
            startActivity(Intent(this, customerRegister::class.java))
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

        sendOtp.setOnClickListener {
            val phoneNumber = phno.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                phno.error = "Enter phone number"
            } else {
                val formattedNumber = formatPhoneNumber(phoneNumber)
                sendOTP(formattedNumber)
            }
        }

        verifyOtp.setOnClickListener {
            val otpCode = otp.text.toString().trim()
            if (otpCode.isEmpty()) {
                otp.error = "Enter OTP"
            } else {
                verifyOTP(otpCode)
            }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"
    }

    private fun sendOTP(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOTP(otp: String) {
        if (storedVerificationId == null) {
            Toast.makeText(applicationContext, "Verification ID is missing. Please resend OTP.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "signInWithCredential: success")
                    Toast.makeText(applicationContext, "OTP Verified", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, home::class.java))
                    finish()
                } else {
                    Log.w("FirebaseAuth", "signInWithCredential: failure", task.exception)
                    val errorMsg = if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        "Invalid OTP"
                    } else {
                        "OTP Verification failed"
                    }
                    Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
    }
}