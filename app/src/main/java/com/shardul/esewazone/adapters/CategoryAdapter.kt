package com.shardul.esewazone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shardul.esewazone.data.model.Category
import com.shardul.esewazone.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(
        val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {

        val category = categories[position]
        holder.binding.imgCategory.setImageResource(category.icon)
        holder.binding.txtCategory.text = category.title

    }

    override fun getItemCount(): Int {
        return categories.size
    }
}





//override fun getItemCount():Int {
// return categories.size
// }
// override onCreateViewHolder()
// override onBindViewHolder()
// class userAdapter()