package com.seniorproject.project.models

import java.io.Serializable
//Model class for reading promotion data from firebase
public class Promotions (val AddressWord: String ,val Category: String, val Discount: String, val Discount_price: Int, val Ini_Price: Int , val Latitude: Double, val Longitude: Double, val ProductName:String,
                         val ShopName: String, val ValidTo: String,val distance: Int,val id: Int,val imageURL: String, val shopURL: String):
    Serializable {
    constructor(): this("","","",0,0,0.0,0.0,"","","",0,0,"","")
}