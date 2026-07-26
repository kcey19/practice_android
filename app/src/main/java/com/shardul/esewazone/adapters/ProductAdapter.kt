package com.shardul.esewazone.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shardul.esewazone.R
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.databinding.ItemProductBinding

class ProductAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onCartClick: (Product) -> Unit,
    private val onFavouriteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val products = mutableListOf<Product>()
    inner class ProductViewHolder(
        val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size
    fun submitList(newProducts: List<Product>) {

        products.clear()

        products.addAll(newProducts)

        notifyDataSetChanged()

    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val product = products[position]

        holder.binding.apply {

            txtPrice.text = "NPR %.2f".format(product.price)

            txtRating.text =
                "%.1f (%d)".format(
                    product.rating.rate,
                    product.rating.count
                )

            txtCategory.text =
                product.category.replaceFirstChar {
                    it.uppercase()
                }

            txtProductName.text =
                if (product.title.length > 42)
                    product.title.take(42) + "..."
                else
                    product.title

            imgProduct.load(product.image) {
                crossfade(true)
                placeholder(R.drawable.image)
                error(R.drawable.image)
            }

            root.setOnClickListener {
                onProductClick(product)
            }

            btnAddCart.setOnClickListener {
                onCartClick(product)
            }

            btnFavourite.setOnClickListener {
                onFavouriteClick(product)
            }
        }
    }
}