package com.example.musicstream

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.musicstream.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Thread.sleep(1000)
        window.statusBarColor=Color.parseColor("#010B28")
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.DoesnthaveanAccount.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }


        binding.LoginBtn.setOnClickListener {
            val email = binding.textEmailaddress.text.toString()
            val password = binding.textEmailPassword.text.toString()
            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                binding.textEmailaddress.setError("Invalid email")
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.textEmailPassword.setError("Invalid Password")
            }
            loginWithFirebase(email,password)

        }

    }

     fun loginWithFirebase(email: String, password: String) {
         setInProgress(true)
         FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
             .addOnSuccessListener {
                 setInProgress(false)
                 Toast.makeText(applicationContext,"Login  successfully", Toast.LENGTH_SHORT).show()
                 startActivity(Intent(applicationContext,MainActivity::class.java))
                 finish()


             }.addOnFailureListener {
                 setInProgress(false)
                 Toast.makeText(applicationContext,"Login Failed", Toast.LENGTH_SHORT).show()
             }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().currentUser?.apply {
            startActivity(Intent(applicationContext,MainActivity::class.java))
            finish()
        }
    }

    fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.LoginBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.LoginBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}