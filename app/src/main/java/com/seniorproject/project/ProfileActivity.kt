package com.seniorproject.project
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var dbReference: DatabaseReference
    private lateinit var dialog: ProgressDialog

    private val PICK_IMAGE_REQUEST = 1234
    private var filePath: Uri? = null
    var imageURL: String? = null
    var imageURL2: String? = null
    var ID:String?=null

    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar!!.hide()

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        dialog = ProgressDialog(this)

        ID = UUID.randomUUID().toString()

        auth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("profile")
        cancel.setOnClickListener {
            finish()
        }
        ChangePassBtn.setOnClickListener {
            val intent = Intent(this, UpdatePassword::class.java)
            startActivity(intent)
        }
        ChangePicBtn.setOnClickListener {
            showPhoto()
        }
        proAct_img.setOnClickListener {
            showPhoto()
        }
//read all the values from the provided text-input and update the values to the Google Firebase Realtime Database
        DoneBtn.setOnClickListener {
            if (Firstname.text.toString().isEmpty() || Lastname.text.toString().isEmpty() || username.text.toString().isEmpty() || phoneNum.text.toString().isEmpty()){
                Toast.makeText(this,"Please do not leave the information empty",Toast.LENGTH_SHORT).show()
            }
            else{
                val currentUserDB = dbReference.child(auth.currentUser!!.uid!!)
                currentUserDB.child("firstname").setValue(Firstname.text.toString())
                currentUserDB.child("lastname").setValue(Lastname.text.toString())
                currentUserDB.child("username").setValue(username.text.toString())
                currentUserDB.child("phone").setValue(phoneNum.text.toString())
                if (imageURL2 != null ){
                    val currentUserDB = dbReference.child(auth.currentUser!!.uid!!)
                    currentUserDB.child("picurl").setValue(imageURL2)
                }
                finish()
            }

        }

        loadProfile()
    }
//load the new profile picture to the imageView
    private fun loadProfile(){
        
        val user = auth.currentUser
        val userref = dbReference.child(user?.uid!!)
        
        userref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                Firstname.setText(snapshot.child("firstname").value.toString())
                Lastname.setText(snapshot.child("lastname").value.toString())
                username.setText(snapshot.child("username").value.toString())
                phoneNum.setText(snapshot.child("phone").value.toString())
                if (snapshot.child("picurl").exists()){
                    var profilePic = snapshot.child("picurl").value.toString()
                    Picasso.get().load(profilePic).into(proAct_img)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
//upload the picture from the user's device to the Google Firebase Storage
    private fun uploadFile() {
        if (filePath != null){//so that know the picture location
            val imageRef = storageReference!!.child("profileimages/"+ ID)

            imageRef.putFile(filePath!!)//upload file function!! "filepath" is the picture location
                .addOnSuccessListener {
                    dialog.dismiss()
                    imageRef.downloadUrl.addOnSuccessListener {
                        imageURL2 = it.toString()

                    }
                }
                .addOnFailureListener{
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    dialog.setMessage("Uploading...")
                    dialog.show()
                    dialog.setCanceledOnTouchOutside(false)
                }
        }
    }
//show all the pictures in user's device
    private fun showPhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),PICK_IMAGE_REQUEST)
    }
//check the filepath and call the uploadFile function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//picture return as the 'data'
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data!=null && data.data!=null)
            filePath = data.data
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath) //to get bitmap
            proAct_img.setImageBitmap(bitmap) //xml
            uploadFile()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
}