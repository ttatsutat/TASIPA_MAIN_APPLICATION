package com.seniorproject.project.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seniorproject.project.R
import com.seniorproject.project.models.RailwayData
//this class is show train data section
class RailwayAdapter(
    private val rssObject: MutableList<RailwayData>,
    private val mContext: Context
) : RecyclerView.Adapter<RailwayAdapter.RailwayViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RailwayViewHolder {

        val itemView = inflater.inflate(R.layout.card_railway, parent, false)
        return RailwayViewHolder(itemView)
    }


    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(mContext)
    }
//giving data
    override fun onBindViewHolder(holder: RailwayViewHolder, position: Int) {
        holder.TrainNo.text = rssObject[position].TrainNo.toString()
        holder.TrainDetail.text = rssObject[position].Type
        holder.depStation.text = rssObject[position].DepStation
        holder.depTime.text = rssObject[position].DepTime
        holder.desStation.text = rssObject[position].DestStation
        holder.desTime.text = rssObject[position].DestTime
    }

    override fun getItemCount(): Int {
        return rssObject.size
    }
//binding
    class RailwayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {

        var TrainNo: TextView
        var TrainDetail: TextView
        var depStation: TextView
        var desStation: TextView
        var desTime: TextView
        var depTime: TextView

        var img: ImageView
        var desFlag: ImageView
        var depFlag: ImageView


        init {

            TrainNo = itemView.findViewById(R.id.showTrainNumber)
            TrainDetail = itemView.findViewById(R.id.traindetail)
            depStation = itemView.findViewById(R.id.departstationname)
            desStation = itemView.findViewById(R.id.destinationstationname)
            depTime = itemView.findViewById(R.id.departuretime)
            desTime = itemView.findViewById(R.id.destarrivaltime)
            img = itemView.findViewById(R.id.imageView)
            depFlag = itemView.findViewById(R.id.depa)
            desFlag = itemView.findViewById(R.id.dest)

        }

        override fun onClick(v: View) {
            //itemClickListener!!.onClick(v, adapterPosition,false)
        }

        override fun onLongClick(v: View): Boolean {
            //itemClickListener!!.onClick(v,adapterPosition,true)
            return true
        }

    }
}

