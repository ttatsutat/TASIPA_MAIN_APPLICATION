package com.seniorproject.project.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seniorproject.project.R
import com.seniorproject.project.models.Review
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_res_detail.*

//This class is show other users comment
class CommentAdapter(private val rssObject: ArrayList<Review>, private val mContext: Context) :
    RecyclerView.Adapter<CommentAdapter.FeedViewHolders>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolders {

        val itemView = inflater.inflate(R.layout.card_comment, parent, false)
        return FeedViewHolders(itemView)
    }

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
//giving data to card
    override fun onBindViewHolder(holder: FeedViewHolders, position: Int) {
        holder.txtTitle.text = rssObject[position].username
        holder.txtTitle1.text = rssObject[position].comment
        holder.rate.rating = rssObject[position].rating.toFloat()
        Picasso.get().load(rssObject[position].imageUrl).into(holder.img)
    }

    override fun getItemCount(): Int {
        return rssObject.size
    }
//binding
    inner class FeedViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle: TextView = itemView.findViewById(R.id.cmt_username)
        var txtTitle1: TextView = itemView.findViewById(R.id.cmt_usrDes)
        var rate: RatingBar = itemView.findViewById(R.id.cmt_usrRat)
        var img: ImageView = itemView.findViewById(R.id.cmt_proPic)


    }


}