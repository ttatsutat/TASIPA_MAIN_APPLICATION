package com.seniorproject.project.models

import java.io.Serializable
//Model class for reading restaurant data from firebase
data class Restaurants(
    val id: Int, val Name: String,
    val imageURL:String, val Category: String, val Rating: Double,
    var Description:String, val distance:Double,
    val Latitude:Double=0.0,
    val Longitude:Double=0.0,
    val Location:String,
    val Telephone: String="",
    val RatingNo: Int,
    val Menu1:String="",
    val Menu1pic: String="",
    val Price1:String="",
    val Menu2:String="",
    val Menu2pic: String="",
    val Price2:String="",
    val Menu3:String="",
    val Menu3pic: String="",
    val Price3:String="",
    var CalculatedDis:Float=0.0f
): Serializable {
    constructor(): this(0,"","","",0.0,"",0.0,0.0,0.0,"","",0,"","","","","","","","","",0.0f)
}