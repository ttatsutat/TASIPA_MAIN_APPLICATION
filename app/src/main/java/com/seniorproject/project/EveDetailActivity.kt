package com.seniorproject.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.seniorproject.project.models.Events
import com.seniorproject.project.models.Restaurants
import com.squareup.picasso.Picasso


import kotlinx.android.synthetic.main.activity_eve_detail.*
import kotlinx.android.synthetic.main.activity_res_detail.*
//This is detail page of event
//This page is to show detail view of user's clicked event
class EveDetailActivity : AppCompatActivity()/*, OnMapReadyCallback*/ {
    lateinit var obj: Events
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eve_detail)
        supportActionBar?.hide()
        //initialization
       obj= intent.getSerializableExtra("eveObj") as Events

//showing value to app
        eve_name.text = obj.Name
        eve_desc.text=obj.Description
        eve_loc.text=obj.Location
        eve_type.text=obj.Category

        Picasso.get().load(obj.imageURL).into(eve_pic)
    }

}