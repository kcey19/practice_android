package com.shardul.esewazone.ui.fragments


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.shardul.esewazone.R
import com.shardul.esewazone.adapters.CartAdapter
import com.shardul.esewazone.database.CartDatabase
import com.shardul.esewazone.database.CartEntity
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.databinding.FragmentCartBinding
import com.shardul.esewazone.ui.bottomsheet.DeleteFromBottomSheet
import com.shardul.esewazone.viewmodel.CartViewModel
import com.shardul.esewazone.viewmodel.CartViewModelFactory
import kotlinx.coroutines.launch

class CartFragment : Fragment(), CartAdapter.CartItemListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CartViewModel

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCartBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        val navController = findNavController()

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }


        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        observeCart()

        binding.btnDelete.setOnClickListener {

            val bottomSheet = DeleteFromBottomSheet()
            bottomSheet.setOnDeleteListener {
                viewModel.clearCart()
                Snackbar.make(
                    binding.root,
                    "Cart cleared successfully",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            bottomSheet.show(parentFragmentManager, "DeleteCart")
        }
    }

    private fun setupViewModel() {

        val database =
            CartDatabase.getDatabase(requireContext())

        val repository =
            CartRepository(database.cartDao())

        val factory =
            CartViewModelFactory(repository)

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[CartViewModel::class.java]

    }

    private fun setupRecyclerView() {

        cartAdapter =
            CartAdapter(
                emptyList(),
                this
            )

        binding.recyclerCart.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = cartAdapter

        }

    }

    private fun observeCart() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.cartItems.collect { cartItems ->

                    cartAdapter.updateCart(cartItems)

                    updateTotal(cartItems)

                }

            }

        }

    }

    private fun updateTotal(
        cartItems: List<CartEntity>
    ) {
        binding.txtItemsCount.text =
            "Items (${cartItems.size})"


        val total =
            cartItems.sumOf {
                it.price * it.quantity
            }

        binding.txtTotalPrice.text =
            "Rs. %.2f".format(total)

    }



    override fun onIncrease(item: CartEntity) {
        viewModel.increaseQuantity(item)

    }

    override fun onDecrease(item: CartEntity) {
        viewModel.decreaseQuantity(item)
    }

    override fun onDelete(item: CartEntity) {
        viewModel.removeItem(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

    }

}