package com.example.esewazone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shardul.esewazone.R
import com.shardul.esewazone.data.model.ReviewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(private var reviewList: List<ReviewModel>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUser: CircleImageView = itemView.findViewById(R.id.imgUser)
        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val ratingBarItem: RatingBar = itemView.findViewById(R.id.ratingBarItem)
        val txtTimestamp: TextView = itemView.findViewById(R.id.txtTimestamp)
        val txtComment: TextView = itemView.findViewById(R.id.txtComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]
        holder.txtUserName.text = review.userName
        holder.ratingBarItem.rating = review.rating
        holder.txtComment.text = review.comment

        if (review.timestamp != null) {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            holder.txtTimestamp.text = sdf.format(review.timestamp.toDate())
        } else {
            holder.txtTimestamp.text = "Just now"
        }

        if (!review.userImage.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(review.userImage)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.imgUser)
        } else {
            holder.imgUser.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    override fun getItemCount(): Int = reviewList.size

    fun updateReviews(newReviews: List<ReviewModel>) {
        this.reviewList = newReviews
        notifyDataSetChanged()
    }
}