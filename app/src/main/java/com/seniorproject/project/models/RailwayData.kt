package com.seniorproject.project.models
//Model class for reading railway data from firebase
class RailwayData (val TrainNo: Int, val Type: String, val DepStation: String, val DepTime:String,
                   val DestStation: String,val DestTime: String){
    constructor(): this(0,"","","","","")
}
