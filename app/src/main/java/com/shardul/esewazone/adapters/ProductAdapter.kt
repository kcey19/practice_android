package com.shardul.esewazone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shardul.esewazone.R
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.databinding.ItemProductBinding

class ProductAdapter(
    private val displayMode: ProductDisplayMode,
    private val onProductClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit,
    private val onIncreaseQuantity: (Product) -> Unit,
    private val onDecreaseQuantity: (Product) -> Unit,
    private val onFavouriteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val products = mutableListOf<Product>()
    private val cartQuantities = mutableMapOf<Int, Int>()

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        if(displayMode== ProductDisplayMode.POPULAR){
            binding.root.layoutParams=
                binding.root.layoutParams.apply{
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                }
        }
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    fun submitList(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun submitCartQuantities(quantities: Map<Int, Int>) {
        cartQuantities.clear()
        cartQuantities.putAll(quantities)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val quantity = cartQuantities[product.id] ?: 0

        holder.binding.apply {
            txtPrice.text = "Rs. %.2f".format(product.price)
            txtCategory.text = product.category.replaceFirstChar { it.uppercase() }
            txtProductName.text = product.title
            imgProduct.load(product.image) {
                crossfade(true)
            }

            root.setOnClickListener { onProductClick(product) }

            if (quantity <= 0) {
                showAddButton()
            } else {
                showQuantityControls(quantity)
            }

            imgAdd.setOnClickListener { onAddToCart(product) }
            imgIncrease.setOnClickListener { onIncreaseQuantity(product) }
            imgDecrease.setOnClickListener { onDecreaseQuantity(product) }
            btnFavourite.setOnClickListener { onFavouriteClick(product) }
        }
    }

    private fun ItemProductBinding.showAddButton() {
        imgAdd.visibility = View.VISIBLE
        txtQuantity.visibility = View.GONE
        imgIncrease.visibility = View.GONE
        imgDecrease.visibility = View.GONE

        cartContainer.setBackgroundResource(R.drawable.bg_cart_button)
        cartContainer.layoutParams = cartContainer.layoutParams.apply {
            height = dpToPx(44)
        }
        cartContainer.requestLayout()
    }

    private fun ItemProductBinding.showQuantityControls(quantity: Int) {
        imgAdd.visibility = View.GONE
        txtQuantity.visibility = View.VISIBLE
        imgIncrease.visibility = View.VISIBLE
        imgDecrease.visibility = View.VISIBLE

        txtQuantity.text = String.format("%02d", quantity)

        cartContainer.setBackgroundResource(R.drawable.bg_cart_stepper_button)
        cartContainer.layoutParams = cartContainer.layoutParams.apply {
            height = dpToPx(104)
        }
        cartContainer.requestLayout()
    }

    private fun ItemProductBinding.dpToPx(dp: Int): Int {
        return (dp * this.root.resources.displayMetrics.density).toInt()
    }
}