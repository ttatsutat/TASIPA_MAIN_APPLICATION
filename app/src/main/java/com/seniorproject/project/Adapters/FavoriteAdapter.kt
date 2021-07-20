package com.seniorproject.project.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.seniorproject.project.Interface.onItemClickListener
import com.seniorproject.project.R
import com.seniorproject.project.models.Restaurants
import com.squareup.picasso.Picasso

//Adapter for favorite section to give data to recycler view
class FavoriteAdapter(
    private val rssObject: MutableList<Restaurants>,
    private val mContext: Context,
    private val listener: onItemClickListener
) : RecyclerView.Adapter<FavoriteAdapter.FeedViewHolders>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolders {

        val itemView = inflater.inflate(R.layout.card_restaurant, parent, false)
        return FeedViewHolders(itemView)
    }

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(mContext)
    }
//giving data to card
    override fun onBindViewHolder(holder: FeedViewHolders, position: Int) {
        holder.txtTitle.text = rssObject[position].Name
        holder.txtTitle1.text = rssObject[position].Category
        var newRating = String.format("%.1f", rssObject[position].Rating).toFloat()
        holder.txtTitle2.text = newRating.toString()
        Picasso.get().load(rssObject[position].imageURL).into(holder.img)


    }

    override fun getItemCount(): Int {
        return rssObject.size
    }
//binding
    inner class FeedViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var txtTitle: TextView
        var txtTitle1: TextView
        var txtTitle2: TextView
        var txtTitle3: TextView
        var img: ImageView


        init {

            txtTitle = itemView.findViewById(R.id.textView)
            txtTitle1 = itemView.findViewById(R.id.textView1)
            txtTitle2 = itemView.findViewById(R.id.vv12)
            txtTitle3 = itemView.findViewById(R.id.resDistance)
            img = itemView.findViewById(R.id.imageShow)
            txtTitle3.visibility = INVISIBLE
            txtTitle2.marginTop
            itemView.setOnClickListener(this)

        }
        //to get position of user's clicked data
        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition, rssObject)
        }

    }
}
