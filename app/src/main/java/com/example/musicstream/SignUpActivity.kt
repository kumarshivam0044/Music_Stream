package com.example.musicstream

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.musicstream.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {
    lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.parseColor("#010B28")

        binding.AlreadyHaveAcc.setOnClickListener {//login
           finish()
        }
        binding.createAccount.setOnClickListener {
            val email = binding.textEmailaddress.text.toString()
            val password = binding.textEmailPassword.text.toString()
            val confirmPassword = binding.textEmailConfirmPassword.text.toString()
            // for validation we have use pattern
          if(! Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(),email))
          {
              binding.textEmailaddress.setError("Invalid email")            // validate email
              return@setOnClickListener

          }
            if (password.length<6)
            {
                binding.textEmailPassword.setError("Length should be 6 char")
                return@setOnClickListener
            }
            if (!password.equals(confirmPassword))
            {
                binding.textEmailConfirmPassword.setError("Password not matched")
                return@setOnClickListener
            }
            createAccountWithFirebase(email,password)


        }



    }

    fun createAccountWithFirebase(email: String, password: String) {
      setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                setInProgress(false)
                Toast.makeText(applicationContext,"User created succesfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()

            }.addOnFailureListener {
                setInProgress(false)
                Toast.makeText(applicationContext,"Create account failed",Toast.LENGTH_SHORT).show()
            }
    }
    fun setInProgress(inProgress:Boolean) {
        if (inProgress) {
            binding.createAccount.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }
        else{
            binding.createAccount.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }


}