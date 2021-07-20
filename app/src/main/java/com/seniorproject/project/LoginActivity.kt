package com.seniorproject.project

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
//This class is for login user to our app
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth //Declare the connection to firebase authentication
    private lateinit var dialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()
//firebase hooks
        auth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(this)
        val currentUser = auth.currentUser
        //checks
        if(currentUser != null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
       //binding and navigation
        LoginBtn.setOnClickListener {
            login()
        }
        registerBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        forgetBtn.setOnClickListener {
            val intent2 = Intent(this, ForgetPassActivity::class.java)
            startActivity(intent2)
        }
    }
//check email and pass for letting user to enter our app
    private fun login() { //Login page
        var email = emailText.text.toString() //put the input data to email variable
        var pass = passText.text.toString() //put the input data to pass variable

        //check the possible conditions of user errors
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.error = "Please enter a VALID Email format"
            return
        } else if (pass.length < 5) {
            passText.error = "Password should be greater than 5 characters"
            return
        } else if (pass.isEmpty()) {
            passText.error = "Please enter a password!"
            return
        } else {
            //when it is not error, it will set dialog once user press log in while comparing user's data
            dialog.setMessage("Please Wait..")
            dialog.show()
            dialog.setCanceledOnTouchOutside(false)
            //checks whether user's enter pass and email is correct
            // proceed them to home page if it they are valid user
            auth.signInWithEmailAndPassword(email, pass)//pass value to firebase authentication
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {//check if it contains this user in the authentication data
                        dialog.cancel()
                        Toast.makeText(this, "Successfully Login", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)//move to the restaurant interface : our kt name not matched!
                        finish()
                    } else {
                        dialog.cancel()
                        Toast.makeText(this, "Failed to Login", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}

