package com.shardul.esewazone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.shardul.esewazone.api.RetrofitInstance
import com.shardul.esewazone.databinding.FragmentProductDetailsBinding
import com.shardul.esewazone.data.repository.ProductRepository
import com.shardul.esewazone.viewmodel.ProductDetailsViewModel
import com.shardul.esewazone.viewmodel.ProductDetailsViewModelFactory


class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductDetailsViewModel

    private var quantity = 1

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

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}