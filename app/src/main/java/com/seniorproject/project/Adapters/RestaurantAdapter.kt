package com.seniorproject.project.Adapters

import android.content.Context
import android.location.Location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter


import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.seniorproject.project.Interface.onItemClickListener
import com.seniorproject.project.R
import com.seniorproject.project.models.Restaurants
import com.squareup.picasso.Picasso
//Adapter to show Restaurant data on recycler view
class RestaurantAdapter(
    private val currentLatLng: LatLng,
    private val rssObject: MutableList<Restaurants>,
    private val mContext: Context,
    private val listener: onItemClickListener
) : RecyclerView.Adapter<RestaurantAdapter.FeedViewHolders>() {
    private var filteredData = rssObject
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolders {

        val itemView = inflater.inflate(R.layout.card_restaurant, parent, false)
        return FeedViewHolders(itemView)
    }

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
//giving data to card
    override fun onBindViewHolder(holder: FeedViewHolders, position: Int) {
        holder.txtTitle.text = filteredData[position].Name
        holder.txtTitle1.text = filteredData[position].Category
        var newRating = String.format("%.1f", filteredData[position].Rating).toFloat()
        holder.txtTitle3.text = newRating.toString()
        holder.txtTitle2.text = filteredData[position].CalculatedDis.toString() + " km"
        Picasso.get().load(filteredData[position].imageURL).into(holder.img)
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }
//binding
    inner class FeedViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var txtTitle: TextView = itemView.findViewById(R.id.textView)
        var txtTitle1: TextView = itemView.findViewById(R.id.textView1)
        var txtTitle2: TextView = itemView.findViewById(R.id.resDistance)
        var txtTitle3: TextView = itemView.findViewById(R.id.vv12)

        //  var rate: RatingBar
        var img: ImageView = itemView.findViewById(R.id.imageShow)


        init {

            itemView.setOnClickListener(this)
            //itemView.setOnLongClickListener(this)

        }
//get users's clicked data position
        override fun onClick(v: View) {
            listener.onItemClick(adapterPosition, filteredData)
        }


    }
//search user's searched keyword in restaurant
    //filter
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var st = constraint.toString()
                if (st.isEmpty()) {
                    filteredData = rssObject
                } else {
                    var lst = mutableListOf<Restaurants>()
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
                filteredData = results!!.values as MutableList<Restaurants>
                notifyDataSetChanged()
            }
        }

    }

}
