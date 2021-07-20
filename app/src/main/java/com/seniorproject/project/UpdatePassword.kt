package com.seniorproject.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_promtion.*
import kotlinx.android.synthetic.main.activity_update_password.*

class UpdatePassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        auth = FirebaseAuth.getInstance()
        supportActionBar!!.hide()

        Back_btn.setOnClickListener {
            finish()
        }
        set_new_password_btn.setOnClickListener {

            changePassword()
        }
    }
//check whether the user input the old password correctly or not
//if yes, then we ask for the new password and confirm new password
    private fun changePassword() {

        var OldPass = old_pass.text.toString()
        var NewPass = new_pass.text.toString()
        var ConfirmPass = confirm_pass.text.toString()

        if(OldPass.isNotEmpty() && NewPass.isNotEmpty() && ConfirmPass.isNotEmpty()){

            if (NewPass.equals(ConfirmPass)){

                val user = auth.currentUser
                if (user!=null && user.email != null){

                    val credential = EmailAuthProvider.getCredential(user.email!!,OldPass)
                    user?.reauthenticate(credential)?.addOnCompleteListener{

                        if (it.isSuccessful){

                            Toast.makeText(this@UpdatePassword,"Re-Authentication Success !!!", Toast.LENGTH_SHORT).show()
                            user?.updatePassword(NewPass)?.addOnCompleteListener {
                                task -> if (task.isSuccessful){
                                    Toast.makeText(this@UpdatePassword,"Password Changed Successfully", Toast.LENGTH_SHORT).show()
                                    auth.signOut()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                                }
                            }

                        }else{
                            Toast.makeText(this@UpdatePassword,"Re-Authentication Failed !!!", Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }else{

                Toast.makeText(this@UpdatePassword,"Password mismatching", Toast.LENGTH_SHORT).show()
            }

        }else{

            Toast.makeText(this@UpdatePassword,"Please fill in all boxes", Toast.LENGTH_SHORT).show()

        }

    }
}