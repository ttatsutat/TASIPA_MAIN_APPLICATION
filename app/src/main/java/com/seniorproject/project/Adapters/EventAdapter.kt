package com.seniorproject.project.Adapters

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.provider.Settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.seniorproject.project.Interface.onItemClickListener
import com.seniorproject.project.Interface.onItemClickListener1
import com.seniorproject.project.R
import com.seniorproject.project.models.Events
import com.seniorproject.project.models.Restaurants
import com.squareup.picasso.Picasso
//Adapter for Event section to give data to recycler view
class EventAdapter(
    private val currentLatLng: LatLng,
    private val rssObject: MutableList<Events>,
    private val mContext: Context,
    private val listener: onItemClickListener1
) : RecyclerView.Adapter<EventAdapter.FeedViewHolders>() {
    private var filteredData = rssObject
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolders {

        val itemView = inflater.inflate(R.layout.card_event, parent, false)
        return FeedViewHolders(itemView)
    }

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    //giving data to each card
    override fun onBindViewHolder(holder: FeedViewHolders, position: Int) {

        holder.txtTitle.text = filteredData[position].Name
        holder.txtTitle1.text = filteredData[position].Location

        holder.txtTitle3.text = filteredData[position].Date
        var evelat = filteredData[position].Latitude.toString()
        var evelong = filteredData[position].Longitude.toString()
        Picasso.get().load(filteredData[position].imageURL).into(holder.img)

        val loc1 = Location("")
        loc1.setLatitude(currentLatLng.latitude)
        loc1.setLongitude(currentLatLng.longitude)

        val loc2 = Location("")
        loc2.setLatitude(evelat.toDouble())
        loc2.setLongitude(evelong.toDouble())

        val distanceInMeters: Float = loc1.distanceTo(loc2)
        var distanceInKm = String.format("%.2f", (distanceInMeters / 1000)).toFloat()

        holder.txtTitle4.text = distanceInKm.toString() + "km"


    }

    override fun getItemCount(): Int {
        return filteredData.size
    }
    //binding
    inner class FeedViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var txtTitle: TextView = itemView.findViewById(R.id.textView)
        var txtTitle1: TextView = itemView.findViewById(R.id.textView1)

        // var txtTitle2: TextView
        var txtTitle3: TextView = itemView.findViewById(R.id.eventDate)
        var txtTitle4: TextView = itemView.findViewById(R.id.eventDistance)
        var img: ImageView = itemView.findViewById(R.id.imageShow)


        init {
            itemView.setOnClickListener(this)
        }
        //to get position of user's clicked data
        override fun onClick(v: View) {
            listener.onItemClick(adapterPosition, filteredData)
        }


    }
    //this function is for searching and showing the searched data
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var st = constraint.toString()
                if (st.isEmpty()) {
                    filteredData = rssObject
                } else {
                    var lst = mutableListOf<Events>()
                    for (row in rssObject) {
                        if (row.Name.toLowerCase().contains(st.toLowerCase()))
                            lst.add(row)
                    }
                    filteredData = lst
                }

                var filterResults = FilterResults()
                filterResults.values = filteredData
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredData = results!!.values as MutableList<Events>
                notifyDataSetChanged()
            }
        }

    }

}
