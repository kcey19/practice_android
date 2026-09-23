package com.example.esewazone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shardul.esewazone.R
import com.shardul.esewazone.data.model.ReviewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(
    private var reviews: List<ReviewModel>,
    private val currentUserId: String?,
    private val onDeleteClick: (ReviewModel) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtComment: TextView = itemView.findViewById(R.id.txtComment)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBarItem)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteReview) // Assuming you have an ImageButton for delete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.txtUserName.text = review.userName
        holder.txtComment.text = review.comment
        holder.ratingBar.rating = review.rating

        // Only show delete button if the review belongs to the currently logged-in user
        if (!currentUserId.isNullOrEmpty() && review.userId == currentUserId) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener {
                onDeleteClick(review)
            }
        } else {
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = reviews.size

    fun updateReviews(newReviews: List<ReviewModel>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}