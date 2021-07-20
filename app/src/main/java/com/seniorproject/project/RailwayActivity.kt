package com.seniorproject.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.seniorproject.project.Adapters.RailwayAdapter
import com.seniorproject.project.models.RailwayData
import kotlinx.android.synthetic.main.activity_attraction.*
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_category.back_btn
import kotlinx.android.synthetic.main.activity_railway.RailList

//This class is for displaying train data
//This class uses recycler view to show list of data after getting from firebase
class RailwayActivity : AppCompatActivity() {

    lateinit var traindata:MutableList<RailwayData>
    lateinit var db: FirebaseFirestore
    lateinit var adapter: RailwayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_railway)
        supportActionBar?.hide()
//binding and navigation
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)//back to the login page
            startActivity(intent)
        }

        back_btn.setOnClickListener {
            finish()
        }
//initialization
        traindata= mutableListOf()
        db= FirebaseFirestore.getInstance()
        val linearLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL,false)
        RailList.layoutManager = linearLayoutManager
//getting data from firebase on basis of dept time
        val docRef = db.collection("Train")
        docRef.orderBy("DepTime").get()//ordering ...
            .addOnSuccessListener { snapShot ->//this means if read is successful then this data will be loaded to snapshot
                if (snapShot != null) {
                    traindata!!.clear()
                    traindata = snapShot.toObjects(RailwayData::class.java)
                    adapter = RailwayAdapter(traindata, baseContext)
                    RailList.adapter=adapter
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
        //passing value to adapter for showing data in card view on recycler view
        val adapter = RailwayAdapter(traindata,baseContext)

        RailList.adapter = adapter
    }
}