package com.seniorproject.project.models

import java.io.Serializable
//Model class for reading Events data from firebase
data class Events(
    val id: Int, val Name: String,
    val imageURL:String, val Category: String, val Date: String,
    var Description:String, val distance:Double,
    val Latitude:Double,
    val Longitude:Double,
    val Location:String,
    val Telephone: String
): Serializable {
    constructor(): this(0,"","","","","",0.0,0.0,0.0,"","N/A")
}