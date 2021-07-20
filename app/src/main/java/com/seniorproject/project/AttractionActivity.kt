package com.seniorproject.project

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.seniorproject.project.Adapters.AttractionAdapter
import com.seniorproject.project.Adapters.RestaurantAdapter

import com.seniorproject.project.Interface.onItemClickListener
import com.seniorproject.project.models.Restaurants
import kotlinx.android.synthetic.main.activity_amenity.*


import kotlinx.android.synthetic.main.activity_attraction.*
import kotlinx.android.synthetic.main.activity_attraction.all_txt
import kotlinx.android.synthetic.main.activity_attraction.back_btn
import kotlinx.android.synthetic.main.activity_attraction.search_button
import kotlinx.android.synthetic.main.activity_attraction.search_view
import kotlinx.android.synthetic.main.activity_attraction.sort_button
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.activity_restaurant.*

//This is class is similiar to RestaurantActivity.kt
//Please refer to comment on that file ,in case
class AttractionActivity : AppCompatActivity(),onItemClickListener {
    lateinit var attdata:MutableList<Restaurants>
    lateinit var db: FirebaseFirestore
    lateinit var adapter: AttractionAdapter
    var flag=true
    private lateinit var dialog: Dialog

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    var currentLatLng: LatLng= LatLng(0.0,0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attraction)
        supportActionBar?.hide()
        back_btn.setOnClickListener {
            finish()
        }
        attdata= mutableListOf()
        db= FirebaseFirestore.getInstance()
        dialog = Dialog(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                //textView1.text = location.latitude.toString() + ", " + location.longitude.toString()

                if (location == null){
                    Toast.makeText(applicationContext, "Location Not Found",Toast.LENGTH_SHORT).show()
                }
                else{
                    currentLatLng = LatLng(location.latitude,location.longitude)

                }
            }

            override fun onProviderDisabled(provider: String) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
       requestLocation()
        val linearLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL,false)
        attList.layoutManager = linearLayoutManager

        search_button.setOnClickListener {
            if (flag){
                search_view.visibility= View.VISIBLE
                flag=false
            }
            else{
                search_view.visibility= View.GONE
                flag=true
            }

        }
        sort_button.setOnClickListener {
            dialog.setContentView(R.layout.sort_card)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

        }
        att_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                adapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        readall()
    }

    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),10)
            }
            return
        }

        locationManager.requestLocationUpdates("gps",1000,0f,locationListener)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            10 -> requestLocation()
            else -> Toast.makeText(this,"Do not nothing (becuz the requestCode != 10)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
        Log.i("GPS Status","pause")
    }

    fun dis_sorting(view: View) {
        dialog.dismiss()

        attdata.sortBy { it.CalculatedDis }
        adapter = AttractionAdapter(currentLatLng, attdata, baseContext,this)
        attList.adapter=adapter
    }
    fun rat_sorting(view: View) {
        dialog.dismiss()
        attdata.sortByDescending { it.Rating }
        adapter = AttractionAdapter(currentLatLng, attdata, baseContext,this)
        attList.adapter=adapter
    }

    override fun onItemClick(position: Int,data:MutableList<Restaurants>) {
        var intent= Intent(this,AttDetailActivity::class.java)
        intent.putExtra("attObj",data[position])
        startActivity(intent)

    }
    fun readall(){
        all_txt.setBackgroundResource(R.color.secondary)
        val docRef = db.collection("Attractions")
        docRef.get()//ordering ...
            .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                if (snapShot != null) {
                    noItemShow4.visibility = View.GONE
                    attList.visibility = View.VISIBLE
                    attdata.clear()
                    attdata = snapShot.toObjects(Restaurants::class.java)
                    calculate_Distance()
                    adapter = AttractionAdapter(currentLatLng,attdata, baseContext,this)
                    attList.adapter=adapter
                    if (snapShot.size()==0){
                        attList.visibility = View.GONE
                        noItemShow4.visibility = View.VISIBLE
                    }
                }

            }//in case it fails, it will toast failed
            .addOnFailureListener { exception ->
                Log.d(
                    "FirebaseError",
                    "Fail:",
                    exception
                )//this is kind a debugger to check whether working correctly or not
                Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

            }
    }
    fun filter_Att(view: View){
        all_txt.setBackgroundResource(R.color.white)
        Ancient_txt.setBackgroundResource(R.color.white)
        Aquarium_txt.setBackgroundResource(R.color.white)
        ArtGallery_txt.setBackgroundResource(R.color.white)
        Educational_txt.setBackgroundResource(R.color.white)
        Museum_txt.setBackgroundResource(R.color.white)
        Religious_txt.setBackgroundResource(R.color.white)
        Other_txt.setBackgroundResource(R.color.white)
        when(view.id){
            R.id.all->{
                readall()
            }
            R.id.filter_Ancient->{
                Ancient_txt.setBackgroundResource(R.color.secondary)
                db.collection("Attractions").whereEqualTo("Category","Ancient")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow4.visibility = View.GONE
                            attList.visibility = View.VISIBLE
                            attdata.clear()
                            attdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = AttractionAdapter(currentLatLng,attdata, baseContext,this)
                            attList.adapter=adapter
                            if (it.size()==0){
                                attList.visibility = View.GONE
                                noItemShow4.visibility = View.VISIBLE
                            }
                        }

                    }//in case it fails, it will toast failed
                    .addOnFailureListener { exception ->
                        Log.d(
                            "FirebaseError",
                            "Fail:",
                            exception
                        )//this is kind a debugger to check whether working correctly or not
                        Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

                    }
            }
            R.id.filter_Aquarium->{
                Aquarium_txt.setBackgroundResource(R.color.secondary)
                db.collection("Attractions").whereEqualTo("Category","Aquarium")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow4.visibility = View.GONE
                            attList.visibility = View.VISIBLE
                            attdata.clear()
                            attdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = AttractionAdapter(currentLatLng,attdata, baseContext,this)
                            attList.adapter=adapter
                            if (it.size()==0){
                                attList.visibility = View.GONE
                                noItemShow4.visibility = View.VISIBLE
                            }
                        }

                    }//in case it fails, it will toast failed
                    .addOnFailureListener { exception ->
                        Log.d(
                            "FirebaseError",
                            "Fail:",
                            exception
                        )//this is kind a debugger to check whether working correctly or not
                        Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

                    }
            }
            R.id.filter_ArtGallery->{ ArtGallery_txt.setBackgroundResource(R.color.secondary)
                db.collection("Attractions").whereEqualTo("Category","ArtGallery")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow4.visibility = View.GONE
                            attList.visibility = View.VISIBLE
                            attdata.clear()
                            attdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = AttractionAdapter(currentLatLng,attdata, baseContext,this)
                            attList.adapter=adapter
                            if (it.size()==0){
                                attList.visibility = View.GONE
                                noItemShow4.visibility = View.VISIBLE
                            }
                        }

                    }//in case it fails, it will toast failed
                    .addOnFailureListener { exception ->
                        Log.d(
                            "FirebaseError",
                            "Fail:",
                            exception
                        )//this is kind a debugger to check whether working correctly or not
                        Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

                    }}
            R.id.filter_Educational->{
                Educational_txt.setBackgroundResource(R.color.secondary)
                db.collection("Attractions").whereEqualTo("Category","Educational")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow4.visibility = View.GONE
                            attList.visibility = View.VISIBLE
                            attdata.clear()
                            attdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = AttractionAdapter(currentLatLng,attdata, baseContext,this)
                            attList.adapter=adapter
                            if (it.size()==0){
                                attList.visibility = View.GONE
                                noItemShow4.visibility = View.VISIBLE
                            }
                        }

                    }//in case it fails, it will toast failed
                    .addOnFailureListener { exception ->
                        Log.d(
                            "FirebaseError",
                            "Fail:",
                            exception
                        )//this is kind a debugger to check whether working correctly or not
                        Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

                    }
            }
            R.id.filter_Museum->{
                Museum_txt.setBackgroundResource(R.color.secondary)
                db.collection("Attractions").whereEqualTo("Category","Museum")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow4.visibility = View.GONE
                            attList.visibility = View.VISIBLE
                            attdata.clear()
                            attdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = AttractionAdapter(currentLatLng,attdata, baseContext,this)
                            attList.adapter=adapter
                            if (it.size()==0){
                                attList.visibility = View.GONE
                                noItemShow4.visibility = View.VISIBLE
                            }
                        }

                    }//in case it fails, it will toast failed
                    .addOnFailureListener { exception ->
                        Log.d(
                            "FirebaseError",
                            "Fail:",
                            exception
                        )//this is kind a debugger to check whether working correctly or not
                        Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

                    }
            }
            R.id.filter_Religious->{
                Religious_txt.setBackgroundResource(R.color.secondary)
                db.collection("Attractions").whereEqualTo("Category","Religious")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow4.visibility = View.GONE
                            attList.visibility = View.VISIBLE
                            attdata.clear()
                            attdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = AttractionAdapter(currentLatLng,attdata, baseContext,this)
                            attList.adapter=adapter
                            if (it.size()==0){
                                attList.visibility = View.GONE
                                noItemShow4.visibility = View.VISIBLE
                            }
                        }

                    }//in case it fails, it will toast failed
                    .addOnFailureListener { exception ->
                        Log.d(
                            "FirebaseError",
                            "Fail:",
                            exception
                        )//this is kind a debugger to check whether working correctly or not
                        Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

                    }
            }
            R.id.filter_Other->{
                Other_txt.setBackgroundResource(R.color.secondary)
                db.collection("Attractions").whereEqualTo("Category","Other")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow4.visibility = View.GONE
                            attList.visibility = View.VISIBLE
                            attdata.clear()
                            attdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = AttractionAdapter(currentLatLng,attdata, baseContext,this)
                            attList.adapter=adapter
                            if (it.size()==0){
                                attList.visibility = View.GONE
                                noItemShow4.visibility = View.VISIBLE
                            }
                        }

                    }//in case it fails, it will toast failed
                    .addOnFailureListener { exception ->
                        Log.d(
                            "FirebaseError",
                            "Fail:",
                            exception
                        )//this is kind a debugger to check whether working correctly or not
                        Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

                    }}
        }
    }

    fun calculate_Distance(){    var i=0
        for (ame in attdata ){

            val loc1 = Location("")
            loc1.setLatitude(currentLatLng.latitude)
            loc1.setLongitude(currentLatLng.longitude)

            val loc2 = Location("")
            loc2.setLatitude(ame.Latitude)
            loc2.setLongitude(ame.Longitude)

            val distanceInMeters: Float = loc1.distanceTo(loc2)
            var distanceInKm = String.format("%.2f", (distanceInMeters / 1000)).toFloat()
            attdata[i].CalculatedDis=distanceInKm
            i+=1
        }}

}
