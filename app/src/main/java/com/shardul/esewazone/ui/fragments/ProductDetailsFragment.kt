package com.shardul.esewazone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.shardul.esewazone.R
import com.shardul.esewazone.api.RetrofitInstance
import com.shardul.esewazone.databinding.FragmentProductDetailsBinding
import com.shardul.esewazone.data.repository.ProductRepository
import com.shardul.esewazone.database.CartDatabase
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.viewmodel.CartViewModel
import com.shardul.esewazone.viewmodel.CartViewModelFactory
import com.shardul.esewazone.viewmodel.ProductDetailsViewModel
import com.shardul.esewazone.viewmodel.ProductDetailsViewModelFactory


class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductDetailsViewModel
    private lateinit var cartViewModel: CartViewModel
    private var quantity = 1
    private var addToCartSnackbar: Snackbar? = null
    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentProductDetailsBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val productId =
            arguments?.getInt("productId") ?: -1
        val repository =
            ProductRepository(RetrofitInstance.api)
        val factory =
            ProductDetailsViewModelFactory(repository)
        viewModel =
            ViewModelProvider(this, factory)[ProductDetailsViewModel::class.java]

        val cartDao =
            CartDatabase.getDatabase(requireContext()).cartDao()
        val cartRepository =
            CartRepository(cartDao)
        val cartFactory =
            CartViewModelFactory(cartRepository)

        cartViewModel = ViewModelProvider(this,cartFactory)[CartViewModel::class.java]

        viewModel.fetchProduct(productId)
        observeProduct()
    }

    private fun observeProduct() {

        viewModel.product.observe(viewLifecycleOwner) { product ->

            binding.imgProduct.load(product.image)
            binding.txtTitle.text = product.title
            binding.txtCategory.text = product.category
            binding.txtBottomPrice.text =
                "NPR %.2f".format(product.price)
            binding.txtBottomPrice.text =
                "NPR %.2f".format(product.price)
            binding.txtDescription.text =
                product.description
            binding.txtRating.text =
                "${product.rating.rate} (${product.rating.count})"
            binding.btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            binding.btnAddToCart.setOnClickListener {
                cartViewModel.addToCart(product)
                showSnackbarCart()
            }

            binding.btnFavourite.setOnClickListener {
                   Snackbar.make(
                    binding.root,
                    "Added to Favourites",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }
    private fun showSnackbarCart(){
        addToCartSnackbar?.dismiss()
        addToCartSnackbar = Snackbar.make(
            binding.root,
            "Added to Cart Successfully",
            Snackbar.LENGTH_LONG
        )
        addToCartSnackbar?.setAction("Go to Cart"){
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.selectedItemId = R.id.cartFragment
        }
        addToCartSnackbar?.setActionTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.priceColor
            )
        )

        addToCartSnackbar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}