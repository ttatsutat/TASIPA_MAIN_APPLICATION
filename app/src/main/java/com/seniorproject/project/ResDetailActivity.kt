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
import com.seniorproject.project.models.Restaurants
import com.seniorproject.project.models.Review
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_res_detail.*


//This is detail page of Restaurant
//This page is to show detail view of user's clicked Restaurant
class ResDetailActivity : AppCompatActivity(), OnMapReadyCallback, ValueEventListener {

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
        setContentView(R.layout.activity_res_detail)
        supportActionBar?.hide()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.res_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //firebase hooks
        auth = FirebaseAuth.getInstance()
        var currentuser = auth!!.currentUser!!.uid
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode!!.getReference("favorite").child(currentuser)

        //get value
        obj= intent.getSerializableExtra("Obj") as Restaurants

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
        res_favBtn.setOnClickListener {
            if (!checked) {
                res_favBtn.setColorFilter(
                    ContextCompat.getColor(baseContext, R.color.red), PorterDuff.Mode.SRC_IN
                )
                reference!!.child(obj.id.toString()).setValue(obj)
                checked=true
            }
            else{
                res_favBtn.colorFilter = null
                checked=false

                reference!!.child(obj.id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (appleSnapshot in snapshot.children) {
                            appleSnapshot.ref.removeValue()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Error","Failed to remove")
                    }
                })
            }
        }
        //this send user's comment to database
        res_sendBtn.setOnClickListener {

            var usrCmt = res_desTxt.text.toString()
            var userrate = res_usrRate.rating.toDouble()
            reviewReference!!.child(auth!!.currentUser!!.uid)
                .setValue(Review(uname, usrCmt, userrate,upic))
            res_cmtSec.visibility = GONE
            var newRate = (((obj.Rating * obj.RatingNo) + res_usrRate.rating.toDouble()) / (obj.RatingNo + 1))
//            var newRating = String.format("%.2f",newRate).toDouble()
            //in this case the the calculation have minor diff, the number will round up to be the same number which wont change info of the db
            var newRateNo = obj.RatingNo + 1

            dataReference = FirebaseFirestore.getInstance()
            //dataReference.collection("Restaurants").document("0VkpvEZXuxViOT15MfOC").update("RatingNo", (newRateNo))

            var query= dataReference.collection("Restaurants").whereEqualTo("id",obj.id)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        docID = document.id.toString()
                        //Log.d("Painty", "Rating = "+newRating.toString())

                        dataReference.collection("Restaurants").document(docID).update("RatingNo", (newRateNo))
                        dataReference.collection("Restaurants").document(docID).update("Rating", (newRate))
                    }
                }

        }
        //showing data to app
        res_name.text = obj.Name
        res_desc.text=obj.Description
        var newRating = String.format("%.1f",obj.Rating).toFloat()
        res_rat.rating = newRating
        res_ratVal.text = newRating.toString()
        res_type.text = obj.Category
        res_phone.text = obj.Telephone

        foodmenu1.text=obj.Menu1
        foodmenu1price.text=obj.Price1
        foodmenu2.text=obj.Menu2
        foodmenu2price.text=obj.Price2
        foodmenu3.text=obj.Menu3
        foodmenu3price.text=obj.Price3

        Picasso.get().load(obj.Menu1pic).into(menuimg1)
        Picasso.get().load(obj.Menu2pic).into(menuimg2)
        Picasso.get().load(obj.Menu3pic).into(menuimg3)
        Picasso.get().load(obj.imageURL).into(res_pic)

    }
    //this function is for user to navigate between detail, review and map layout
    fun onClick(v: View) {
        res_detailLayout.visibility = GONE
        res_reviewLayout.visibility = GONE
        res_mapLayout.visibility = GONE
        resbtn_holder.visibility= GONE
        res_mapBtn.visibility = GONE
        res_favBtn.visibility = GONE
        res_button3.setBackgroundResource(R.color.white)
        res_button2.setBackgroundResource(R.color.white)
        res_button4.setBackgroundResource(R.color.white)
        when (v.id) {
            R.id.res_button2 -> {
                res_detailLayout.visibility = VISIBLE
                resbtn_holder.visibility= VISIBLE
                res_favBtn.visibility = VISIBLE
                res_button2.setBackgroundResource(R.color.secondary)
            }
            R.id.res_button4 -> {
                res_mapLayout.visibility = VISIBLE
                resbtn_holder.visibility= VISIBLE
                res_mapBtn.visibility = VISIBLE
                res_favBtn.visibility = GONE
                res_button4.setBackgroundResource(R.color.secondary)
            }
            R.id.res_button3 -> {
                res_reviewLayout.visibility = VISIBLE
                res_button3.setBackgroundResource(R.color.secondary)
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
                                res_cmtSec.visibility=GONE
                                res_aftCmt.visibility=VISIBLE
                                res_cmtUsrName.text=name
                                res_cmtUsrReview.text=rev
                                res_cmtUsrRatVal.rating=rating.toFloat()
                                imgLink=it.child(i.key.toString()).child("imageUrl").value.toString()
                                if (imgLink.isNotEmpty()){
                                    Picasso.get().load(imgLink).into(res_cmtUsrPic)
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
                            res_cmtRcy.layoutManager = linearLayoutManager
                            var adapter1= CommentAdapter(data!!,baseContext)

                            res_cmtRcy.adapter = adapter1
                        } else {
                            res_cmtRcy.visibility= INVISIBLE
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
        latLng = LatLng(obj.Latitude, obj.Longitude)
        mMap.addMarker(MarkerOptions().position(latLng).title(obj.Name))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

    }
    //change color of button on click
    override fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.value !== null) {
            res_favBtn.setColorFilter(
                ContextCompat.getColor(baseContext, R.color.red),
                android.graphics.PorterDuff.Mode.SRC_IN
            );
            checked=true
        }
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d("Error","Failed to load")
    }
}
