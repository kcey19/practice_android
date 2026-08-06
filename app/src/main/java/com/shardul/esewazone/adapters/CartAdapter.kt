package com.shardul.esewazone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shardul.esewazone.database.CartEntity
import com.shardul.esewazone.databinding.ItemCartBinding

class CartAdapter(
    private var cartItems: List<CartEntity>,
    private val listener: CartItemListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(
        val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {

        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val item = cartItems[position]
        holder.binding.imgProduct.load(item.image)
        holder.binding.txtTitle.text = item.title
        holder.binding.txtPrice.text =
            "NPR %.2f".format(item.price)

        holder.binding.txtQuantity.text =
            item.quantity.toString()

        holder.binding.btnIncrease.setOnClickListener {
            listener.onIncrease(item)
        }

        holder.binding.btnDecrease.setOnClickListener {
            listener.onDecrease(item)
        }

    }

    fun updateCart(newItems: List<CartEntity>) {

        cartItems = newItems

        notifyDataSetChanged()

    }

    interface CartItemListener {

        fun onDelete(item: CartEntity)
        fun onIncrease(item: CartEntity)
        fun onDecrease(item: CartEntity)

    }

}