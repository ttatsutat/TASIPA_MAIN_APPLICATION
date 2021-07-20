package com.seniorproject.project

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.seniorproject.project.Adapters.CommentAdapter
import com.seniorproject.project.R.color.*
import com.seniorproject.project.models.Restaurants
import com.seniorproject.project.models.Review
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_att_detail.*

//This is detail page of attraction
//This page is to show detail view of user's clicked attraction
class AttDetailActivity : AppCompatActivity(), OnMapReadyCallback, ValueEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var latLng: LatLng

    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    var reviewReference: DatabaseReference?=null
    var userReference:DatabaseReference?=null
    var auth: FirebaseAuth? = null

    lateinit var dataReference: FirebaseFirestore
    lateinit var docID:String

    var data: ArrayList<Review>? = ArrayList()
    var checked: Boolean = false
    lateinit var obj: Restaurants
    var uname:String=""
    var upic:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_att_detail)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.att_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //firebase hooks
        auth = FirebaseAuth.getInstance()
        var currentuser = auth!!.currentUser!!.uid
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode!!.getReference("favorite").child(currentuser)
        obj= intent.getSerializableExtra("attObj") as Restaurants
        //intent value
        userReference=rootNode!!.getReference("profile").child(currentuser)
        reviewReference = rootNode!!.getReference("review").child(obj.id.toString())

        //calls onDataChanged()
        reference!!.child(obj.id.toString()).addListenerForSingleValueEvent(this)

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                uname = snapshot.child("username").value.toString()
                if (snapshot.child("picurl").exists()){
                    upic = snapshot.child("picurl").value.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("error", "username Error")
            }
        })
        //favorite button
        // on click add data to real time database
        //User's can un-fav from this button
        att_favBtn.setOnClickListener {
            if (!checked) {
                att_favBtn.setColorFilter(
                    ContextCompat.getColor(baseContext, red), PorterDuff.Mode.SRC_IN
                )
//                reference!!.child(pic).setValue(Favorite(name,pic, type, rating = 4.5, distance = 0.0,
//                    id = "Attraction"
//                ))
                reference!!.child(obj.id.toString()).setValue(obj)
                checked = true
            } else {
                att_favBtn.colorFilter = null
                checked = false
                reference!!.child(obj.id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (appleSnapshot in snapshot.children) {
                            appleSnapshot.ref.removeValue()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Error", "Failed to remove")
                    }
                })
            }
        }
        //this send user's comment to database
        att_sendBtn.setOnClickListener {

            var usrCmt= att_desTxt.text.toString()
            var userrate= att_usrRate.rating.toDouble()
            reviewReference!!.child(auth!!.currentUser!!.uid).setValue(Review(uname,usrCmt,userrate,upic))
            att_cmtSec.visibility=GONE

            var newRate = (((obj.Rating * obj.RatingNo) + att_usrRate.rating.toDouble()) / (obj.RatingNo + 1))
            var newRateNo = obj.RatingNo + 1

            dataReference = FirebaseFirestore.getInstance()
            //dataReference.collection("Restaurants").document("0VkpvEZXuxViOT15MfOC").update("RatingNo", (newRateNo))

            var query= dataReference.collection("Attractions")!!.whereEqualTo("id",obj.id)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        docID = document.id.toString()
                        //Log.d("Painty", "Rating = "+newRating.toString())

                        dataReference.collection("Attractions").document(docID).update("RatingNo", (newRateNo))
                        dataReference.collection("Attractions").document(docID).update("Rating", (newRate))
                    }
                }


        }

        //showing data to app
        att_name.text = obj.Name
        att_desc.text=obj.Description
        var newRating = String.format("%.1f",obj.Rating).toFloat()
        att_rat.rating = newRating
        att_ratVal.text = newRating.toString()
        att_type.text=obj.Category
        att_loc.text=obj.Location

        Picasso.get().load(obj.imageURL).into(att_pic)
    }
    //this function is for user to navigate between detail, review and map layout
    fun onClick(v: View) {
        attdetailLayout.visibility = GONE
        att_reviewLayout.visibility = GONE
        attmapLayout.visibility = GONE
        att_mapBtn.visibility = GONE
        attBtn_holder.visibility =GONE
        att_favBtn.visibility = GONE
        attbutton3.setBackgroundResource(white)
        attbutton2.setBackgroundResource(white)
        attbutton4.setBackgroundResource(white)
        when (v.id) {
            R.id.attbutton2 -> {
                attdetailLayout.visibility = VISIBLE
                attBtn_holder.visibility = VISIBLE
                att_favBtn.visibility = VISIBLE
                attbutton2.setBackgroundResource(secondary)
            }
            R.id.attbutton4 -> {
                attmapLayout.visibility = VISIBLE
                attBtn_holder.visibility = VISIBLE
                att_mapBtn.visibility = VISIBLE
                attbutton4.setBackgroundResource(secondary)
            }
            R.id.attbutton3 -> {
                att_reviewLayout.visibility = VISIBLE
                attbutton3.setBackgroundResource(secondary)
                //this part of code is to get other user comment and show it through adapter
                reviewReference!!.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(it: DataSnapshot) {
                        data?.clear()
                        it.children?.forEach { i ->

                            var name = it.child(i.key.toString()).child("username").value.toString()
                            var rev = it.child(i.key.toString()).child("comment").value.toString()
                            var rating = it.child(i.key.toString()).child("rating").value.toString().toDouble()
                            var imgLink=""
                            if (i.key.toString().equals(auth!!.currentUser!!.uid)){
                                att_cmtSec.visibility=GONE
                                att_aftCmt.visibility=VISIBLE
                                att_cmtUsrName.text=name
                                att_cmtUsrReview.text=rev
                                att_cmtUsrRatVal.rating=rating.toFloat()
                                imgLink=it.child(i.key.toString()).child("imageUrl").value.toString()
                                if (imgLink.isNotEmpty()){
                                    Picasso.get().load(imgLink).into(att_cmtUsrPic)
                                }

                            }
                            else{
                                imgLink=it.child(i.key.toString()).child("imageUrl").value.toString()
                                data?.add(Review(name,rev,rating,imgLink))
                            }

                        }
                        if (data != null) {
                            val linearLayoutManager =
                                LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
                            att_cmtRcy.layoutManager = linearLayoutManager
                            var adapter1= CommentAdapter(data!!,baseContext)

                            att_cmtRcy.adapter = adapter1
                        } else {
                            att_cmtRcy.visibility= INVISIBLE
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("error", "Comment  Error")
                    }
                })
            }

        }
    }
    //this function is to show map
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        latLng = LatLng(obj.Latitude,obj.Longitude)
        // Add a marker in Sydney and move the camera
        mMap.addMarker(MarkerOptions().position(latLng).title(obj.Name))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }
    //change color of button on click
    override fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.value !== null) {
            att_favBtn.setColorFilter(
                ContextCompat.getColor(baseContext, red),
                android.graphics.PorterDuff.Mode.SRC_IN
            );
            checked = true
        }
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d("Error", "Failed to load")
    }
}