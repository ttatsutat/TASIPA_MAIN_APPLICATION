package com.seniorproject.project.ui.Home

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.seniorproject.project.*
import com.seniorproject.project.Adapters.AdverAdapter
import com.seniorproject.project.Adapters.HomePromoAdapter
import com.seniorproject.project.Interface.onItemClickListener2
import com.seniorproject.project.R
import com.seniorproject.project.models.Advertisements
import com.seniorproject.project.models.Promotions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

//This is fragment to show under home tab in bottom navigation
//This is our main page from where every page can be accessed
class HomeFragment : Fragment(), onItemClickListener2 {

//replace with the city's information for weather
    private var SalayaLat = "13.800663"
    private var SalayaLong = "100.323823"
    private var API = "XXXXXXXXXX"

    lateinit var promodata: MutableList<Promotions>
    lateinit var db: FirebaseFirestore
    lateinit var adapter: HomePromoAdapter
    lateinit var adapter2: AdverAdapter

    lateinit var auth: FirebaseAuth
    var database: FirebaseDatabase? = null
    var dbReference: DatabaseReference? = null

    var imagesArray: ArrayList<String> = ArrayList()

    //  var carouselView: CarouselView? = null
    lateinit var adverdata: MutableList<Advertisements>
    //lateinit var imageListener:ImageListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }
//    }

    override fun onStart() {
        super.onStart()

        weatherTask().execute()

        getProfile()
        //imagesArray.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885_960_720.jpg")
//This part of code is to make every page accessible though this page
        all_cat.setOnClickListener {
            var intent = Intent(activity, Allcategories::class.java)
            startActivity(intent)
        }
        all_pro.setOnClickListener {
            var intent = Intent(activity, PromtionActivity::class.java)
            startActivity(intent)
        }
        res_list.setOnClickListener {
            var intent = Intent(activity, RestaurantActivity::class.java)
            startActivity(intent)
        }
        eve_list.setOnClickListener {
            var intent = Intent(activity, EventActivity::class.java)
            startActivity(intent)
        }
        ame_list.setOnClickListener {
            var intent = Intent(activity, AmenityActivity::class.java)
            startActivity(intent)
        }
        att_list.setOnClickListener {
            var intent = Intent(activity, AttractionActivity::class.java)
            startActivity(intent)
        }
        profile_image.setOnClickListener {
            var intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        promodata = mutableListOf()
        adverdata = mutableListOf()
        //   carouselView= view?.findViewById(R.id.carouselView)
        db = FirebaseFirestore.getInstance()
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val linearLayoutManager2 =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.layoutManager = linearLayoutManager
        recycler2.layoutManager = linearLayoutManager2
//this part of code gets advertise data from firestore and show it on app
        val docRef2 = db.collection("Advertise")
        docRef2.get()//ordering ...
            .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                if (snapShot != null) {
                    adverdata!!.clear()
                    adverdata = snapShot.toObjects(Advertisements::class.java)
                    adapter2 = context?.let { AdverAdapter(adverdata, it) }!!
                    recycler2.adapter = adapter2
                }

            }//in case it fails, it will toast failed
            .addOnFailureListener { exception ->
                Log.d(
                    "FirebaseError",
                    "Fail:",
                    exception
                )//this is kind a debugger to check whether working correctly or not
                //Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

            }

        val adapter2 = activity?.let { AdverAdapter(adverdata, it) }

        recycler2.adapter = adapter2

        //imageListener = ImageListener{position, imageView -> Picasso.get().load(imagesArray[position]).into(imageView) }
//Here we get time to show it
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)

        //this part of code gets promotion data from database and show it on app
        val docRef = db.collection("Promotion")
        docRef.whereGreaterThan("ValidTo", formatted).limit(5).get() //ordering ...
            .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                if (snapShot != null) {
                    promodata!!.clear()
                    promodata = snapShot.toObjects(Promotions::class.java)
                    adapter = context?.let { HomePromoAdapter(promodata, it, this) }!!
                    recycler.adapter = adapter
                }

            }//in case it fails, it will toast failed
            .addOnFailureListener { exception ->
                Log.d(
                    "FirebaseError",
                    "Fail:",
                    exception
                )//this is kind a debugger to check whether working correctly or not
                //Toast.makeText(baseContext,"Fail to read database", Toast.LENGTH_SHORT).show()

            }

        val adapter = activity?.let { HomePromoAdapter(promodata, it, this) }

        recycler.adapter = adapter

    }

    //This inner class is for retrieving weather data and showing it to card
    inner class weatherTask() : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        //gets data from url
        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/onecall?lat=$SalayaLat&lon=$SalayaLong&units=metric&exclude=alert,minutely,&appid=$API")
                        .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        // extract data and show it to user in readable manner
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val current = jsonObj.getJSONObject("current")
                val weather = current.getJSONArray("weather").getJSONObject(0)
                val picNow = weather.getString("icon")
                Picasso.get().load("https://openweathermap.org/img/w/$picNow.png").into(icon)
                val conditionNow = weather.getString("description")
                val dateTime = current.getLong("dt")

                val dateTimeText =
                    SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date(dateTime * 1000))
                val tempNow = current.getString("temp") + "°C"

                val next1hr = jsonObj.getJSONArray("hourly").getJSONObject(0)
                val weather1hr = next1hr.getJSONArray("weather").getJSONObject(0)
                val pic1hr = weather1hr.getString("icon")
                Picasso.get().load("https://openweathermap.org/img/w/$pic1hr.png").into(icon1)
                val dateTime1 = next1hr.getLong("dt")
                val dateTimeText1 =
                    SimpleDateFormat("hh a", Locale.ENGLISH).format(Date(dateTime1 * 1000))
                val tempNext1hr = next1hr.getString("temp") + "°C "

                val next2hr = jsonObj.getJSONArray("hourly").getJSONObject(1)
                val weather2hr = next2hr.getJSONArray("weather").getJSONObject(0)
                val pic2hr = weather2hr.getString("icon")
                Picasso.get().load("https://openweathermap.org/img/w/$pic2hr.png").into(icon2)
                val dateTime2 = next2hr.getLong("dt")
                val dateTimeText2 =
                    SimpleDateFormat("hh a", Locale.ENGLISH).format(Date(dateTime2 * 1000))
                val tempNext2hr = next2hr.getString("temp") + "°C "

                val next3hr = jsonObj.getJSONArray("hourly").getJSONObject(2)
                val weather3hr = next1hr.getJSONArray("weather").getJSONObject(0)
                val pic3hr = weather1hr.getString("icon")
                Picasso.get().load("https://openweathermap.org/img/w/$pic3hr.png").into(icon3)
                val dateTime3 = next3hr.getLong("dt")
                val dateTimeText3 =
                    SimpleDateFormat("hh a", Locale.ENGLISH).format(Date(dateTime3 * 1000))
                val tempNext3hr = next3hr.getString("temp") + "°C "

                val next4hr = jsonObj.getJSONArray("hourly").getJSONObject(3)
                val weather4hr = next4hr.getJSONArray("weather").getJSONObject(0)
                val pic4hr = weather4hr.getString("icon")
                Picasso.get().load("https://openweathermap.org/img/w/$pic4hr.png").into(icon4)
                val dateTime4 = next4hr.getLong("dt")
                val dateTimeText4 =
                    SimpleDateFormat("hh a", Locale.ENGLISH).format(Date(dateTime4 * 1000))
                val tempNext4hr = next4hr.getString("temp") + "°C "

                val next5hr = jsonObj.getJSONArray("hourly").getJSONObject(4)
                val weather5hr = next5hr.getJSONArray("weather").getJSONObject(0)
                val pic5hr = weather5hr.getString("icon")
                Picasso.get().load("https://openweathermap.org/img/w/$pic5hr.png").into(icon5)
                val dateTime5 = next5hr.getLong("dt")
                val dateTimeText5 =
                    SimpleDateFormat("hh a", Locale.ENGLISH).format(Date(dateTime5 * 1000))
                val tempNext5hr = next5hr.getString("temp") + "°C "

                tempNowShow.text = tempNow
                conditionNowShow.text = conditionNow

                next1tempShow.text = tempNext1hr
                next1timeShow.text = dateTimeText1

                next2tempShow.text = tempNext2hr
                next2timeShow.text = dateTimeText2

                next3tempShow.text = tempNext3hr
                next3timeShow.text = dateTimeText3

                next4tempShow.text = tempNext4hr
                next4timeShow.text = dateTimeText4

                next5tempShow.text = tempNext5hr
                next5timeShow.text = dateTimeText5

            } catch (e: Exception) {

            }
        }

    }

    //this function is to reterive current user's info
    //We get username and profile pic for showing
    private fun getProfile() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database?.reference!!.child("profile")

        val user = auth.currentUser
        val userref = dbReference?.child(user?.uid!!)

        userref?.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                usernameShow.text = snapshot.child("username").value.toString()

                if (snapshot.child("picurl").exists()) {
                    var profilePic = snapshot.child("picurl").value.toString()
                    Picasso.get().load(profilePic).into(profile_image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //This function is to move to detail on user's click on particular promotion data
    override fun onItemClick(position: Int, data: MutableList<Promotions>) {
        //Log.d("Clickyy","Clicked1")
        var intent = Intent(activity, PromoDetailActivity::class.java)
        intent.putExtra("Obj", data[position])
        startActivity(intent)
    }

}