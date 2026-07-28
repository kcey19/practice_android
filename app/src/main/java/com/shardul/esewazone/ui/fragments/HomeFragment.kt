package com.shardul.esewazone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.shardul.esewazone.R
import com.shardul.esewazone.adapters.BannerAdapter
import com.shardul.esewazone.adapters.CategoryAdapter
import com.shardul.esewazone.data.model.Category
import com.shardul.esewazone.databinding.FragmentHomeBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.shardul.esewazone.adapters.ProductAdapter
import com.shardul.esewazone.viewmodel.HomeViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setupBanner()
        setupCategoryRecyclerView()
        setupProductRecyclerView()
        observeProducts()
    }

    private fun setupBanner(){
             val banners = listOf(
                R.drawable.banner,
                R.drawable.banner,
                R.drawable.banner
            )
        val bannerAdapter = BannerAdapter(banners)
        binding.bannerLayout.bannerViewPager.adapter = bannerAdapter
        binding.bannerLayout.bannerIndicator.setViewPager(binding.bannerLayout.bannerViewPager)
    }

    private fun setupCategoryRecyclerView() {

        val categoryList = listOf(

            Category(R.drawable.ic_mobile, "Mobile"),
            Category(R.drawable.ic_laptop, "Electronics"),
            Category(R.drawable.ic_fashion, "Fashion"),
            Category(R.drawable.ic_grocery, "Groceries"),
            Category(R.drawable.ic_home, "Home")
        )
        val adapter = CategoryAdapter(categoryList)
        binding.rvCategories.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvCategories.adapter = adapter
    }

    private fun setupProductRecyclerView(){
        productAdapter = ProductAdapter(
            onProductClick = { product ->

                val bundle = Bundle()
                bundle.putInt("productId", product.id)
                findNavController().navigate(
                    R.id.action_homeFragment_to_productDetailsFragment,
                    bundle
                )

            },
            onCartClick = {
                Snackbar.make(
                    binding.root,
                    "Added to Cart",
                    Snackbar.LENGTH_SHORT
                ).show()
            },
            onFavouriteClick = {
                Snackbar.make(
                    binding.root,
                    "Added to Favourite",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        )
       binding.rvProducts.apply{
           adapter = productAdapter
           layoutManager = LinearLayoutManager(
               requireContext(),
               LinearLayoutManager.HORIZONTAL,
               false
           )
           setHasFixedSize(true)
       }
    }

    private fun observeProducts() {

        viewModel.products.observe(viewLifecycleOwner) {
            productAdapter.submitList(it)

        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->

        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Snackbar.make(
                binding.root,
                message,
                Snackbar.LENGTH_LONG
            ).show()

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


