package com.seniorproject.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forget_pass.*
import kotlinx.android.synthetic.main.activity_forget_pass.emailText
import kotlinx.android.synthetic.main.activity_login.*

class ForgetPassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_forget_pass)
//click on SubmitBtn button to send the link url to the email as user filled in the emailText text-input to set up a new password
        SubmitBtn.setOnClickListener {
            val email = emailText.text.toString()
            if (email.isEmpty()){

                Toast.makeText(this@ForgetPassActivity,"Please enter your email address",Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(this@ForgetPassActivity,"The email is sent to your account",Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(this@ForgetPassActivity,task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()

                        }
                    }
            }
        }

    }
}