package com.seniorproject.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.seniorproject.project.models.Promotions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_promo_detail.*

class PromoDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var latLng: LatLng
    var userReference: DatabaseReference?=null
    var auth: FirebaseAuth? = null
    lateinit var obj: Promotions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_detail)
        supportActionBar!!.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.promo_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //firebase hooks
        auth = FirebaseAuth.getInstance()
        var currentuser = auth!!.currentUser!!.uid

        obj= intent.getSerializableExtra("Obj") as Promotions

        //showing data to app
        shop_name.text = obj.ShopName
        product_name.text=obj.ProductName

        discountShow.text=obj.Discount + " DISCOUNT!!!"
        validShow.text=obj.ValidTo
        InitPrice.text=obj.Ini_Price.toString()+" THB"
        FinalPrice.text=obj.Discount_price.toString()+" THB"
        Picasso.get().load(obj.imageURL).into(product_pic)
        if (obj.shopURL.isNotEmpty()){
            Picasso.get().load(obj.shopURL).into(shop_pic)
            //Log.d("Painty","Url = "+obj.shopURL)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        latLng = LatLng(obj.Latitude,obj.Longitude)
        mMap.addMarker(MarkerOptions().position(latLng).title(obj.ShopName))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }
}