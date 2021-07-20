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
import android.view.View.GONE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.seniorproject.project.Adapters.RestaurantAdapter
import com.seniorproject.project.Interface.onItemClickListener
import com.seniorproject.project.models.Restaurants
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.activity_restaurant.*
import kotlinx.android.synthetic.main.activity_restaurant.all_txt
import kotlinx.android.synthetic.main.activity_restaurant.back_btn
import kotlinx.android.synthetic.main.activity_restaurant.res_txt
import kotlinx.android.synthetic.main.activity_restaurant.search_button
import kotlinx.android.synthetic.main.activity_restaurant.search_view
//This class is for showing list of restaurant
//This class reads data from firebase and display it on recycler view
class RestaurantActivity : AppCompatActivity(),onItemClickListener {
    lateinit var resdata: MutableList<Restaurants>

   // private lateinit var mDatabase:DatabaseReference
    lateinit var db:FirebaseFirestore
    lateinit var adapter:RestaurantAdapter
    var flag=true
    private lateinit var dialog: Dialog

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private  var currentLatLng: LatLng= LatLng(0.0,0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        supportActionBar?.hide()
        back_btn.setOnClickListener {
            finish()
        }
        //Initialization
        resdata= mutableListOf()
        db= FirebaseFirestore.getInstance()
        dialog = Dialog(this)
       // mDatabase = FirebaseDatabase.getInstance().reference;
        val linearLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL,false)
        resList.layoutManager = linearLayoutManager

        readAll()
        //dropdown search field to let user type and search in realtime
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
        //this part of code sends user's typed data to adapter to filter out according to user's searched character
        res_search.addTextChangedListener(object : TextWatcher{
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
        //this part of code is for showing available sorting and sort accordingly
        sort_button.setOnClickListener {
            dialog.setContentView(R.layout.sort_card)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

        }
        //this part of code gets user's current location
        //It ask user's permission to access location
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
//            }
//        }
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
//Intent to detail page for detail view on user's clicked data
    override fun onItemClick(position: Int,data:MutableList<Restaurants>) {
        var intent= Intent(this,ResDetailActivity::class.java)
        intent.putExtra("Obj",data[position])
        startActivity(intent)
    }
//for sorting by distance
    fun dis_sorting(view: View) {
        dialog.dismiss()
        resdata.sortBy { it.CalculatedDis }
        adapter = RestaurantAdapter(currentLatLng, resdata, baseContext,this)
        resList.adapter=adapter
    }
    //for sorting by rating
    fun rat_sorting(view: View) {
        dialog.dismiss()
        resdata.sortByDescending { it.Rating }
        adapter = RestaurantAdapter(currentLatLng, resdata, baseContext,this)
        resList.adapter=adapter

    }
    //runs on start
    //shows all data after reading from firestore
    //no filter applied
    fun readAll() {
        all_txt.setBackgroundResource(R.color.secondary)
         db.collection("Restaurants")
            .get()//ordering ...
            .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                if (snapShot != null) {
                    noItemShow.visibility = GONE
                    resList.visibility = View.VISIBLE
                    resdata.clear()
                    resdata = snapShot.toObjects(Restaurants::class.java)
                    calculate_Distance()
                    adapter = RestaurantAdapter(currentLatLng, resdata, baseContext,this)
                    resList.adapter=adapter
                    if (snapShot.size()==0){
                        resList.visibility = View.GONE
                        noItemShow.visibility = View.VISIBLE
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
    //this function is used for filtering by category of data
    //various filter available
    fun filter(view: View) {
        all_txt.setBackgroundResource(R.color.white)
        cafe_txt.setBackgroundResource(R.color.white)
        res_txt.setBackgroundResource(R.color.white)
        des_txt.setBackgroundResource(R.color.white)
        when(view.id){
            R.id.all ->{readAll()}
            R.id.filter_cafe ->{
                cafe_txt.setBackgroundResource(R.color.secondary)
                //filter by cafe
                db.collection("Restaurants").whereEqualTo("Category","Cafe")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow.visibility = GONE
                            resList.visibility = View.VISIBLE
                            resdata.clear()
                            resdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = RestaurantAdapter(currentLatLng, resdata, baseContext,this)
                            resList.adapter=adapter
                            if (it.size()==0){
                                resList.visibility = View.GONE
                                noItemShow.visibility = View.VISIBLE
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
            R.id.filter_dessert ->{
                des_txt.setBackgroundResource(R.color.secondary)
                //filter by dessert
                db.collection("Restaurants").whereEqualTo("Category","Dessert")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow.visibility = GONE
                            resList.visibility = View.VISIBLE
                            resdata.clear()
                            resdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = RestaurantAdapter(currentLatLng, resdata, baseContext,this)
                            resList.adapter=adapter
                            if (it.size()==0){
                                resList.visibility = View.GONE
                                noItemShow.visibility = View.VISIBLE
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
            R.id.filter_res ->{
                //filter by restaurant
                res_txt.setBackgroundResource(R.color.secondary)
                db.collection("Restaurants").whereEqualTo("Category","Restaurant")
                    .get()
                    .addOnSuccessListener {
                        if (it != null) {
                            noItemShow.visibility = GONE
                            resList.visibility = View.VISIBLE
                            resdata.clear()
                            resdata = it.toObjects(Restaurants::class.java)
                            calculate_Distance()
                            adapter = RestaurantAdapter(currentLatLng, resdata, baseContext,this)
                            resList.adapter=adapter
                            if (it.size()==0){
                                resList.visibility = View.GONE
                                noItemShow.visibility = View.VISIBLE
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
    //this function calculate distance on basis of user's location
    fun calculate_Distance(){
        var i=0
        for (ame in resdata ){

            val loc1 = Location("")
            loc1.setLatitude(currentLatLng.latitude)
            loc1.setLongitude(currentLatLng.longitude)

            val loc2 = Location("")
            loc2.setLatitude(ame.Latitude)
            loc2.setLongitude(ame.Longitude)

            val distanceInMeters: Float = loc1.distanceTo(loc2)
            var distanceInKm = String.format("%.2f", (distanceInMeters / 1000)).toFloat()
            resdata[i].CalculatedDis=distanceInKm
            i+=1
        }
    }


}
