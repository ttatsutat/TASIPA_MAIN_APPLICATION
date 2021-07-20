package com.seniorproject.project


import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_ame_detail.*
import kotlinx.android.synthetic.main.activity_ame_detail.send_btn
import kotlinx.android.synthetic.main.activity_ame_detail.user_rate
import kotlinx.android.synthetic.main.activity_att_detail.*
import kotlinx.android.synthetic.main.activity_res_detail.*

//This is detail page of amenity
//This page is to show detail view of user's clicked amenity

class AmeDetailActivity : AppCompatActivity(), OnMapReadyCallback, ValueEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var latLng: LatLng

    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    var reviewReference: DatabaseReference?=null
    var userReference:DatabaseReference?=null
    lateinit var obj: Restaurants

    lateinit var dataReference: FirebaseFirestore
    lateinit var docID:String

    var auth: FirebaseAuth? = null
    var checked: Boolean=false
    var data: ArrayList<Review>? = ArrayList()
     var uname:String=""
    var upic:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ame_detail)
        supportActionBar?.hide()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.ame_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //firebase hooks
        auth = FirebaseAuth.getInstance()
        var currentuser = auth!!.currentUser!!.uid
        rootNode = FirebaseDatabase.getInstance()
        obj= intent.getSerializableExtra("ameObj") as Restaurants
        reference = rootNode!!.getReference("favorite").child(currentuser)
        userReference=rootNode!!.getReference("profile").child(currentuser)
        reviewReference = rootNode!!.getReference("review").child(obj.id.toString())
        reference!!.child(obj.id.toString()).addListenerForSingleValueEvent(this)


        //get username
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
        ame_favBtn.setOnClickListener {
            if (!checked) {
                ame_favBtn.setColorFilter(
                    ContextCompat.getColor(baseContext, R.color.red), PorterDuff.Mode.SRC_IN
                )
               // reference!!.child(pic).setValue()
                reference!!.child(obj.id.toString()).setValue(obj)
                checked=true
            }
            else{
                ame_favBtn.colorFilter = null
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
        send_btn.setOnClickListener {
            var usrCmt= desTxt.text.toString()
            var userrate= user_rate.rating.toDouble()
             reviewReference!!.child(auth!!.currentUser!!.uid).setValue(Review(uname,usrCmt,userrate,upic))
            cmtSec.visibility=View.GONE
            var newRate = (((obj.Rating * obj.RatingNo) + user_rate.rating.toDouble()) / (obj.RatingNo + 1))

            //in this case the the calculation have minor diff, the number will round up to be the same number which wont change info of the db
            var newRateNo = obj.RatingNo + 1
            dataReference = FirebaseFirestore.getInstance()
             dataReference.collection("Amenities").whereEqualTo("id",obj.id)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        docID = document.id.toString()
                        //Log.d("Painty", "Rating = "+newRating.toString())
                        dataReference.collection("Amenities").document(docID).update("RatingNo", (newRateNo))
                        dataReference.collection("Amenities").document(docID).update("Rating", (newRate))
                    }
                }

        }

        //showing data to app
        ame_name.text = obj.Name
        ame_desc.text=obj.Description
        var newRating = String.format("%.1f",obj.Rating).toFloat()
        ame_rat.rating = newRating
        ame_ratVal.text=newRating.toString()
        ame_type.text=obj.Category
        ame_loc.text=obj.Location
        Picasso.get().load(obj.imageURL).into(ame_pic)
    }
    //change color of button on click
    override fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.value !== null) {
            ame_favBtn.setColorFilter(
                ContextCompat.getColor(baseContext, R.color.red),
                android.graphics.PorterDuff.Mode.SRC_IN
            );
            checked=true
        }
    }

    //this function is for user to navigate between detail, review and map layout
    fun onClick(v: View) {
        ame_detailLayout.visibility = View.GONE
        ame_reviewLayout.visibility = View.GONE
        ame_mapLayout.visibility = View.GONE
        ame_mapBtn.visibility = View.GONE
        ameBtn_holder.visibility = View.GONE
        ame_favBtn.visibility = View.GONE
        ame_button3.setBackgroundResource(R.color.white)
        ame_button2.setBackgroundResource(R.color.white)
        ame_button4.setBackgroundResource(R.color.white)
        when (v.id) {
            R.id.ame_button2 -> {
                ame_detailLayout.visibility = View.VISIBLE
                ameBtn_holder.visibility = View.VISIBLE
                ame_favBtn.visibility = View.VISIBLE
                ame_button2.setBackgroundResource(R.color.secondary)
            }
            R.id.ame_button4 -> {
                ame_mapLayout.visibility = View.VISIBLE
                ameBtn_holder.visibility = View.VISIBLE
                ame_mapBtn.visibility = View.VISIBLE
                ame_button4.setBackgroundResource(R.color.secondary)
            }
            R.id.ame_button3 -> {
                ame_reviewLayout.visibility = View.VISIBLE
                ame_button3.setBackgroundResource(R.color.secondary)
                //this part of code is to get other user comment and show it through adapter
                reviewReference!!.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(it: DataSnapshot) {
                        data?.clear()
                    it.children?.forEach { i ->
                        var name = it.child(i.key.toString()).child("username").value.toString()
                        var pic = it.child(i.key.toString()).child("comment").value.toString()
                        var rating = it.child(i.key.toString()).child("rating").value.toString().toDouble()
                        var imgLink=""
                        if (i.key.toString().equals(auth!!.currentUser!!.uid)){
                            cmtSec.visibility=View.GONE
                            aft_cmtSec.visibility=View.VISIBLE
                            cmt_usrName.text=name
                            cmt_usrReview.text=pic
                            cmt_usrRatVal.rating=rating.toFloat()
                            imgLink=it.child(i.key.toString()).child("imageUrl").value.toString()
                            if (imgLink.isNotEmpty()){
                                Picasso.get().load(imgLink).into(cmt_usrPic)
                            }
                        }
                        else{
                            imgLink=it.child(i.key.toString()).child("imageUrl").value.toString()
                            data?.add(Review(name,pic,rating,imgLink))
                        }

                    }
                    if (data != null) {
                        val linearLayoutManager =
                            LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
                        cmt_rcy.layoutManager = linearLayoutManager
                        var adapter1= CommentAdapter(data!!,baseContext)

                        cmt_rcy.adapter = adapter1
                    } else {
                        cmt_rcy.visibility= View.INVISIBLE
                    }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("error", "Comment  Error")
                    }
                })
            }
        }
    }
    override fun onCancelled(error: DatabaseError) {
        Log.d("Error","Failed to load")
    }
//this function is to show map
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        latLng = LatLng(obj.Latitude,obj.Longitude)
        mMap.addMarker(MarkerOptions().position(latLng).title(obj.Name))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }
}



