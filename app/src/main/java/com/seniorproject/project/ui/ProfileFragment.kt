package com.seniorproject.project.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.seniorproject.project.*
import com.seniorproject.project.R

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.proAct_img

//This is user's profile fragment section present  in bottom navigation
class ProfileFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var dbReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        return root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//we initialize firebase instance for futher use
        auth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("profile")

    }

    override fun onStart() {
        super.onStart()
        getProfile()
        //make all three button clickable and intent to desired page
        //navigation
        proAct_img.setOnClickListener {
            var intent= Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }
        signoutBtn.setOnClickListener {
            auth.signOut()
            var intent= Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        abt_us.setOnClickListener {
            var intent= Intent(activity, AboutusActivity::class.java)
            startActivity(intent)
        }

    }
    //get user's info from firebase and store it and show it
    //we get first and last name and profile pic
    private fun getProfile(){

        val user = auth.currentUser
        val userref = dbReference.child(user?.uid!!)

        userref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                profile_firstname.text = snapshot.child("firstname").value.toString()
                profile_lastname.text = snapshot.child("lastname").value.toString()
                if (snapshot.child("picurl").exists()){
                    var profilePic = snapshot.child("picurl").value.toString()
                    Picasso.get().load(profilePic).into(profile_img)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("error","error loading pic")
            }
        })
    }
}