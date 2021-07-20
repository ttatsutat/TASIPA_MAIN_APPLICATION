package com.seniorproject.project

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*

import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var dialog:ProgressDialog
    lateinit var auth: FirebaseAuth
    var database: FirebaseDatabase? = null
    var dbReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database?.reference!!.child("profile")

        dialog= ProgressDialog(this)
        supportActionBar!!.hide()

        nextBtn.setOnClickListener {

            //check all the possible conditions of user errors
            if(firstnameText.text.toString().isEmpty())
            {
                firstnameText.error = "Please input the firstname"
                return@setOnClickListener
            }
            if(lastnameText.text.toString().isEmpty())
            {
                lastnameText.error = "Please input the lastname"
                return@setOnClickListener
            }
            if(usernameText.text.toString().isEmpty())
            {
                usernameText.error = "Please input the username"
                return@setOnClickListener
            }
            if(usernameText.text.toString().length > 20)
            {
                usernameText.error = "Username must not exceed 20 characters!"
                return@setOnClickListener
            }
            if(phoneText.text.toString().isEmpty())
            {
                phoneText.error = "Please input the phone number"
                return@setOnClickListener
            }
            if(phoneText.text.toString().length > 10)
            {
                phoneText.error = "Phone number must not exceed 10 characters!"
                return@setOnClickListener
            }
//switch between first layout and second layout
            else {
                first.visibility = INVISIBLE
                second.visibility = VISIBLE
            }
        }
//call the register function
        SignupBtn.setOnClickListener {
            register()
        }
    }

    private fun register() {
        var email = emailSignupText.text.toString() //store the input
        var pass = passSignupText.text.toString() //store the input
        var cpass = repassSignupText.text.toString() //store the input

        //check all the possible conditions of user errors
        if (email.isEmpty()) {
            emailSignupText.error = "Please enter your Email"
            return
        }
        if (pass.isEmpty()) {
            passSignupText.error = "Please enter a password"
            return
        }
        if (cpass.isEmpty()) {
            repassSignupText.error = "Please confirm your password"
            return
        }
        if (cpass != pass) {
            Toast.makeText(this, "Password is Not Matched", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailSignupText.error = "Invalid Email Format"
            return
        }
        if (pass.length < 5) {
            passSignupText.error = "Password should be greater than 5 Characters"
            return
        }

        //check whether the checkBox is tick or not, if not user won't be able to register
        if(!checkBox.isChecked){
            Toast.makeText(this, "Agree & Read TOS", Toast.LENGTH_SHORT).show()
            return
        }
//set the dialog running to the user when it creating account to the firebase authentication
        dialog.setMessage("..Creating Account..")
        dialog.show()
        dialog.setCanceledOnTouchOutside(true)
//create the account by passing the values we collected inside the account
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {

                    val currentUser = auth.currentUser
                    val currentUserDB = dbReference?.child(currentUser?.uid!!)
                    currentUserDB?.child("firstname")?.setValue(firstnameText.text.toString())
                    currentUserDB?.child("lastname")?.setValue(lastnameText.text.toString())
                    currentUserDB?.child("username")?.setValue(usernameText.text.toString())
                    currentUserDB?.child("email")?.setValue(emailSignupText.text.toString())
                    currentUserDB?.child("phone")?.setValue(phoneText.text.toString())

                    //stop the dialog running when created account in the authentication successfully
                    dialog.cancel()
                    Toast.makeText(this, "Account Created", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)//back to the login page
                    startActivity(intent)
                    finish()
                } else {
                    dialog.cancel()
                    Toast.makeText(this, "Failed to create Account", Toast.LENGTH_LONG).show()
                }
            })
    }
}
