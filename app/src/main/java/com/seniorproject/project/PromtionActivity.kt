package com.seniorproject.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.seniorproject.project.Adapters.PromoAdapter
import com.seniorproject.project.Interface.onItemClickListener
import com.seniorproject.project.Interface.onItemClickListener2
import com.seniorproject.project.models.Promotions
import kotlinx.android.synthetic.main.activity_attraction.*
import kotlinx.android.synthetic.main.activity_promtion.*
import kotlinx.android.synthetic.main.activity_promtion.back_btn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PromtionActivity : AppCompatActivity(), onItemClickListener2 {

    lateinit var promodata:MutableList<Promotions>
    lateinit var db: FirebaseFirestore
    lateinit var adapter: PromoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promtion)
        supportActionBar?.hide()

        back_btn.setOnClickListener {
            finish()
        }
        //firebase hooks
        promodata= mutableListOf()
        db= FirebaseFirestore.getInstance()
        val linearLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL,false)
        promoList.layoutManager = linearLayoutManager

        //read current date and time and set them as our desired format
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        //later it will be used to filter out the promotion that has passed the due already
        //showing data to app as an adapter
        val docRef = db.collection("Promotion")
        docRef.whereGreaterThan("ValidTo",formatted).get()//ordering ...
            .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                if (snapShot != null) {
                    noItemShow5.visibility = View.GONE
                    promoList.visibility = View.VISIBLE
                    promodata!!.clear()
                    promodata = snapShot.toObjects(Promotions::class.java)
                    Log.d("PAINTY",formatted)
                    adapter = PromoAdapter(promodata, baseContext,this)
                    promoList.adapter=adapter
                    if (snapShot.size()==0){
                        promoList.visibility = View.GONE
                        noItemShow5.visibility = View.VISIBLE
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

        val adapter = PromoAdapter(promodata,baseContext,this)

        promoList.adapter = adapter

    }
//set on each items click listener to go to the detail page of the clicked item
    override fun onItemClick(position: Int, data: MutableList<Promotions>) {
        var intent= Intent(this,PromoDetailActivity::class.java)
        intent.putExtra("Obj",data[position])
        startActivity(intent)
    }
}