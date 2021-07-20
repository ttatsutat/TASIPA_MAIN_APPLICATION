package com.seniorproject.project.Interface

import com.seniorproject.project.models.Events
import com.seniorproject.project.models.Promotions
import com.seniorproject.project.models.Restaurants
//these are all the interface which we used for respective category
interface onItemClickListener {
    fun onItemClick(position:Int,data:MutableList<Restaurants>)
}
interface onItemClickListener1 {
    fun onItemClick(position:Int,data:MutableList<Events>)
}
interface onItemClickListener2 {
    fun onItemClick(position:Int,data:MutableList<Promotions>)
}
