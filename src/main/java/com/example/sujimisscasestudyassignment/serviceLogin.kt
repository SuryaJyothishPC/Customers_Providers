package com.example.sujimisscasestudyassignment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class serviceLogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_service_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val phno = findViewById<EditText>(R.id.srv_phn)
        val sendOtp = findViewById<Button>(R.id.send_srv)
        val otp = findViewById<EditText>(R.id.otp2_srv)
        val verifyOtp = findViewById<Button>(R.id.verify_srv)
        val register = findViewById<TextView>(R.id.register2)

        register.setOnClickListener {
            startActivity(Intent(this, serviceRegister::class.java))
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

    private fun sendOTP(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Toast.makeText(applicationContext, "OTP sent", Toast.LENGTH_SHORT).show()
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
                    Log.d("FirebaseAuth", "signInWithCredential:success")
                    Toast.makeText(applicationContext, "OTP Verified", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SPhomepage::class.java))
                    finish()
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
}
