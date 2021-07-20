package com.seniorproject.project

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.view.View.GONE
import android.widget.Toast
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.seniorproject.project.Adapters.*
import com.seniorproject.project.Interface.onItemClickListener1
import com.seniorproject.project.models.Events
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.activity_event.all_txt
import kotlinx.android.synthetic.main.activity_event.back_btn
import kotlinx.android.synthetic.main.activity_event.res_txt
import kotlinx.android.synthetic.main.activity_event.search_button
import kotlinx.android.synthetic.main.activity_event.search_view
import kotlinx.android.synthetic.main.activity_restaurant.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


//This is class is similiar to RestaurantActivity.kt
//Please refer to comment on that file ,in case
class EventActivity : AppCompatActivity(), onItemClickListener1 {
    lateinit var evedata:MutableList<Events>
    var flag=true
    lateinit var db: FirebaseFirestore
    lateinit var adapter:EventAdapter
    lateinit var dataReference: FirebaseFirestore
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private  var currentLatLng: LatLng= LatLng(0.0,0.0)
    lateinit var docID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        supportActionBar?.hide()
        back_btn.setOnClickListener {
            finish()
        }
        db= FirebaseFirestore.getInstance()
        //dataReference = FirebaseFirestore.getInstance()
        evedata= mutableListOf()
        val linearLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL,false)
        eveList.layoutManager = linearLayoutManager
        //replace with event adapter
        readALL()
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
        eve_search.addTextChangedListener(object : TextWatcher {
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

        //currentLatLng = LatLng(0.0,0.0)

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

    override fun onItemClick(position: Int, data: MutableList<Events>) {
        var intent= Intent(this,EveDetailActivity::class.java)
        intent.putExtra("eveObj",data[position])
        //intent.putExtra("rating",res[position].rating.toString())
        startActivity(intent)
    }
    fun readALL(){
        all_txt.setBackgroundResource(R.color.secondary)
        val docRef = db.collection("Events")
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        docRef.whereGreaterThan("Date",formatted)
            .get()//ordering ...
            .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                if (snapShot != null) {
                    noItemShow2.visibility = GONE
                    eveList.visibility = View.VISIBLE
                    evedata.clear()
                    evedata = snapShot.toObjects(Events::class.java)

                    adapter = EventAdapter(currentLatLng,evedata, baseContext,this)
                    eveList.adapter=adapter
                    if (snapShot.size()==0){
                        eveList.visibility = View.GONE
                        noItemShow2.visibility = View.VISIBLE
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

    fun filter_Eve(view: View){
        all_txt.setBackgroundResource(R.color.white)
        official_txt.setBackgroundResource(R.color.white)
        res_txt.setBackgroundResource(R.color.white)
        music_txt.setBackgroundResource(R.color.white)
        festival_txt.setBackgroundResource(R.color.white)
        other_txt.setBackgroundResource(R.color.white)
        when(view.id){
            R.id.all->{
                readALL()
            }
            R.id.filter_official->{
                official_txt.setBackgroundResource(R.color.secondary)
                val docRef = db.collection("Events")
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter).toString()
                docRef.whereEqualTo("Category","Official")
                    .get()//ordering ...
                    .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                        if (snapShot != null) {
                            noItemShow2.visibility = GONE
                            eveList.visibility = View.VISIBLE
                            evedata.clear()
                            evedata = snapShot.toObjects(Events::class.java)

                            adapter = EventAdapter(currentLatLng,evedata, baseContext,this)
                            eveList.adapter=adapter
                            if (snapShot.size()==0){
                                eveList.visibility = View.GONE
                                noItemShow2.visibility = View.VISIBLE
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
            R.id.filter_religious->{
                res_txt.setBackgroundResource(R.color.secondary)
                val docRef = db.collection("Events")
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                docRef.whereEqualTo("Category","Religious")
                    .get()//ordering ...
                    .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                        if (snapShot != null) {
                            noItemShow2.visibility = GONE
                            eveList.visibility = View.VISIBLE
                            evedata.clear()
                            evedata = snapShot.toObjects(Events::class.java)

                            adapter = EventAdapter(currentLatLng,evedata, baseContext,this)
                            eveList.adapter=adapter
                            if (snapShot.size()==0){
                                eveList.visibility = View.GONE
                                noItemShow2.visibility = View.VISIBLE
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
            R.id.filter_music->{
                music_txt.setBackgroundResource(R.color.secondary)
                val docRef = db.collection("Events")
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                docRef.whereEqualTo("Category","Music")
                    .get()//ordering ...
                    .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                        if (snapShot != null) {
                            noItemShow2.visibility = GONE
                            eveList.visibility = View.VISIBLE
                            evedata.clear()
                            evedata = snapShot.toObjects(Events::class.java)

                            adapter = EventAdapter(currentLatLng,evedata, baseContext,this)
                            eveList.adapter=adapter
                            if (snapShot.size()==0){
                                eveList.visibility = View.GONE
                                noItemShow2.visibility = View.VISIBLE
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
            R.id.filter_festival->{
                festival_txt.setBackgroundResource(R.color.secondary)
                val docRef = db.collection("Events")
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                docRef.whereEqualTo("Category","Festival")
                    .get()//ordering ...
                    .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                        if (snapShot != null) {
                            noItemShow2.visibility = GONE
                            eveList.visibility = View.VISIBLE
                            evedata.clear()
                            evedata = snapShot.toObjects(Events::class.java)

                            adapter = EventAdapter(currentLatLng,evedata, baseContext,this)
                            eveList.adapter=adapter
                            if (snapShot.size()==0){
                                eveList.visibility = View.GONE
                                noItemShow2.visibility = View.VISIBLE
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
            R.id.filter_other->{
                other_txt.setBackgroundResource(R.color.secondary)
                val docRef = db.collection("Events")
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                docRef.whereEqualTo("Category","Other")
                    .get()//ordering ...
                    .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                        if (snapShot != null) {
                            noItemShow2.visibility = GONE
                            eveList.visibility = View.VISIBLE
                            evedata.clear()
                            evedata = snapShot.toObjects(Events::class.java)

                            adapter = EventAdapter(currentLatLng,evedata, baseContext,this)
                            eveList.adapter=adapter
                            if (snapShot.size()==0){
                                eveList.visibility = View.GONE
                                noItemShow2.visibility = View.VISIBLE
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
        }
    }
}
