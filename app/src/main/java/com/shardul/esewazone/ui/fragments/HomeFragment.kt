package com.shardul.esewazone.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shardul.esewazone.R
import com.shardul.esewazone.adapters.BannerAdapter
import com.shardul.esewazone.adapters.CategoryAdapter
import com.shardul.esewazone.adapters.ProductAdapter
import com.shardul.esewazone.adapters.ProductDisplayMode
import com.shardul.esewazone.data.model.Category
import com.shardul.esewazone.database.CartDatabase
import com.shardul.esewazone.databinding.FragmentHomeBinding
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.repository.FavouriteRepository
import com.shardul.esewazone.viewmodel.HomeViewModel
import com.shardul.esewazone.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var scrollYPosition = 0

    private lateinit var featuredAdapter: ProductAdapter
    private lateinit var hotDealsAdapter: ProductAdapter
    private lateinit var popularBrandAdapter: ProductAdapter

    private var addToCartSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Home_lifecycle", "onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewModel()
        setupAdapters()
        setupBanner()
        setupCategoryRecyclerView()
        setupProductRecyclerViews()
        setupChipGroupListeners()
        observeProducts()
        observeCart()
        setUpSearch()

        if (scrollYPosition != 0) {
            binding.scrollView.post {
                binding.scrollView.scrollTo(0, scrollYPosition)
            }
        }
    }

    private fun setupAdapters() {
        fun createProductAdapter(displayMode: ProductDisplayMode) = ProductAdapter(
            onProductClick = { product ->
                val bundle = Bundle().apply {
                    putInt("productId", product.id)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_productDetailsFragment,
                    bundle
                )
            },
            onAddToCart = { product ->
                viewModel.addToCart(product)
                showSnackbarCart()
            },
            onIncreaseQuantity = { product ->
                viewModel.increaseQuantity(product)
            },
            onDecreaseQuantity = { product ->
                viewModel.decreaseQuantity(product)
            },
            onFavouriteClick = { product ->
                viewModel.toggleFavourite(product)
                showSnackbarFavourite()
            },
            displayMode = displayMode
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        featuredAdapter = createProductAdapter(ProductDisplayMode.FEATURED)
        hotDealsAdapter = createProductAdapter(ProductDisplayMode.FEATURED)
        popularBrandAdapter = createProductAdapter(ProductDisplayMode.POPULAR)
    }

    private fun setupBanner() {
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
        binding.rvCategories.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvCategories.adapter = adapter
    }

    private fun setupProductRecyclerViews() {
        binding.rvProducts.apply {
            adapter = featuredAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
        }

        binding.rvHotDeals.apply {
            adapter = hotDealsAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
        }

        binding.rvPopularBrand.apply {
            adapter = popularBrandAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupChipGroupListeners() {
        binding.chipGroupPopular.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds.first()
            }
        }

        binding.btnHotDealsMore.setOnClickListener {
        }

        binding.btnPopularBrandMore.setOnClickListener {
        }
    }

    private fun showSnackbarCart() {
        addToCartSnackbar?.dismiss()
        addToCartSnackbar = Snackbar.make(
            binding.root,
            "Added to Cart Successfully",
            Snackbar.LENGTH_LONG
        )
        addToCartSnackbar?.setAction("Go to Cart") {
           val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.selectedItemId=R.id.cartFragment
        }
        addToCartSnackbar?.setActionTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.priceColor
            )
        )
        addToCartSnackbar?.show()
    }


    private fun showSnackbarFavourite() {
        addToCartSnackbar?.dismiss()
        addToCartSnackbar = Snackbar.make(
            binding.root,
            "Added to Favourites Successfully",
            Snackbar.LENGTH_LONG
        )
        addToCartSnackbar?.setAction("Go to Favourites") {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.selectedItemId = R.id.favouritesFragment
        }
        addToCartSnackbar?.setActionTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.priceColor
            )
        )
        addToCartSnackbar?.show()
    }

    private fun observeProducts() {
        viewModel.featuredProducts.observe(viewLifecycleOwner) { products ->
            featuredAdapter.submitList(products)
        }

        viewModel.hotDeals.observe(viewLifecycleOwner) { products ->
            hotDealsAdapter.submitList(products)
        }

        viewModel.popularBrands.observe(viewLifecycleOwner) { products ->
            popularBrandAdapter.submitList(products)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(
                    binding.root,
                    it,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun observeCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.cartItems.collect { items ->
                    val quantities = items.associate {
                        it.productId to it.quantity
                    }
                    featuredAdapter.submitCartQuantities(quantities)
                    hotDealsAdapter.submitCartQuantities(quantities)
                    popularBrandAdapter.submitCartQuantities(quantities)
                }
            }
        }
    }

    private fun setUpViewModel() {
        val cartDao = CartDatabase
            .getDatabase(requireContext())
            .cartDao()

        val cartRepository = CartRepository(
            cartDao,
            FirebaseAuth.getInstance()
        )

        val favouriteRepository = FavouriteRepository(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )

        val factory = HomeViewModelFactory(
            cartRepository,
            favouriteRepository
        )

        viewModel = ViewModelProvider(
            this,
            factory
        )[HomeViewModel::class.java]
    }

    private fun setUpSearch(){
        binding.searchLayout.cardSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    override fun onDestroyView() {
        Log.d("Home_lifecycle", "onDestroyView")
        _binding?.let {
            scrollYPosition = it.scrollView.scrollY
        }
        super.onDestroyView()
        _binding = null
    }
}