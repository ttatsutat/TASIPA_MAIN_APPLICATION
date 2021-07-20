package com.seniorproject.project.ui.Favourites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.seniorproject.project.*
import com.seniorproject.project.Adapters.FavoriteAdapter
import com.seniorproject.project.Interface.onItemClickListener
import com.seniorproject.project.R
import com.seniorproject.project.models.Restaurants
import kotlinx.android.synthetic.main.fragment_favorite.*

//This is favorite fragment present in bottom navigation
//Under this we show user's favorite restaurants, amenity, etc.
class FavouriteFragment : Fragment(),onItemClickListener {

    lateinit var data: MutableList<Restaurants>
    var rootNode: FirebaseDatabase? = null
    var user: FirebaseAuth? = null
    var reference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //creating view and initializing firebase instance for further use
        val root = inflater.inflate(R.layout.fragment_favorite, container, false)
        rootNode = FirebaseDatabase.getInstance()
        user = FirebaseAuth.getInstance()
        var currentuser = user!!.currentUser?.uid
        reference = rootNode!!.getReference("favorite").child(currentuser!!)
        data= mutableListOf()
        return root
    }
//this is main function in this frag
    //All process are done under this section
    override fun onResume() {
        super.onResume()

    //Here we get data from realtime database
        reference!!.get().addOnSuccessListener {
            data?.clear()
            it.children?.forEach { i ->
                var category = it.child(i.key.toString()).child("category").value.toString()
                var description= it.child(i.key.toString()).child("description").value.toString()
                var img = it.child(i.key.toString()).child("imageURL").value.toString()
                var rating =
                    it.child(i.key.toString()).child("rating").value.toString().toDouble()
                var distance =
                    it.child(i.key.toString()).child("distance").value.toString()
                        .toDouble()
                var id =
                    it.child(i.key.toString()).child("id").value.toString().toInt()
                   var lat= it.child(i.key.toString()).child("latitude").value.toString().toDouble()
                   var long= it.child(i.key.toString()).child("longitude").value.toString().toDouble()
                   var name= it.child(i.key.toString()).child("name").value.toString()
                   var tel= it.child(i.key.toString()).child("telephone").value.toString()
                   var loc= it.child(i.key.toString()).child("location").value.toString()
                   var ratNo= it.child(i.key.toString()).child("ratingNo").value.toString().toInt()
                   var m1= it.child(i.key.toString()).child("menu1").value.toString()
                   var m2= it.child(i.key.toString()).child("menu2").value.toString()
                   var m3= it.child(i.key.toString()).child("menu3").value.toString()
                   var mpic1= it.child(i.key.toString()).child("menu1pic").value.toString()
                   var mpic2= it.child(i.key.toString()).child("menu2pic").value.toString()
                   var mpic3= it.child(i.key.toString()).child("menu3pic").value.toString()
                   var pr1= it.child(i.key.toString()).child("price1").value.toString()
                   var pr2= it.child(i.key.toString()).child("price2").value.toString()
                   var pr3= it.child(i.key.toString()).child("price3").value.toString()
                //add these data to mutable list for sending it to adapter class to show data on screen
                data.add(Restaurants(id,name,img,category,rating,description,distance,lat,long,loc,tel,ratNo,m1,mpic1,pr1,m2,mpic2,pr2,m3,mpic3,pr3))
            }
            //check if data is not null to send list to adapter
            if (data != null) {
                val linearLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                favList.layoutManager = linearLayoutManager
                var adapter1 = context?.let {
                    FavoriteAdapter(
                        data,
                        it, this
                    )
                }!!

                favList.adapter = adapter1
            }
            //if data is null we hide the recycler view
            else {
                favList.visibility= INVISIBLE
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
    //this function get user's clicked data position and move to particular page accordingly
    override fun onItemClick(position: Int,data:MutableList<Restaurants>) {
       when(data!![position].id/1000){
           1 -> {var intent=Intent(activity, ResDetailActivity::class.java)
               intent.putExtra("Obj",data[position])
                startActivity(intent)}
           4 -> {var intent=Intent(activity, AmeDetailActivity::class.java)
               intent.putExtra("ameObj",data[position])
               startActivity(intent)}
           else -> {var intent=Intent(activity, AttDetailActivity::class.java)
               intent.putExtra("attObj",data[position])
               startActivity(intent)}
       }

    }

}