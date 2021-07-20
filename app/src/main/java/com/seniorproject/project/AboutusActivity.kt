package com.seniorproject.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_aboutus.*
//this page is just to tell user's about us-(creator)
class AboutusActivity : AppCompatActivity() {

    var imagesArray: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
        supportActionBar?.hide()
        //add image to show in carousel view
        imagesArray.add(R.drawable.paint)
        imagesArray.add(R.drawable.jerry)
        imagesArray.add(R.drawable.tatsu)

        carouselView.pageCount = imagesArray.size
        carouselView.setImageListener(imageListener)
    }

    var imageListener = ImageListener{position, imageView -> imageView.setImageResource(imagesArray[position]) }

}