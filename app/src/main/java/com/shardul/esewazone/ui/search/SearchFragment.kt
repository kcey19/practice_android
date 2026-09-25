package com.shardul.esewazone.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shardul.esewazone.R
import com.shardul.esewazone.api.RetrofitInstance
import com.shardul.esewazone.database.CartDatabase
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.repository.FavouriteRepository
import com.shardul.esewazone.repository.ProductRepository

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels {
        val context = requireContext()
        val apiService = RetrofitInstance.api
        val productRepository = ProductRepository(apiService)
        val cartDao = CartDatabase.getDatabase(context).cartDao()
        val cartRepository = CartRepository(cartDao, FirebaseAuth.getInstance())
        val favouriteRepository = FavouriteRepository(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )

        SearchViewModelFactory(productRepository, cartRepository, favouriteRepository)
    }

    private var actionSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val query = viewModel.query.collectAsStateWithLifecycle().value
                val cartItems = viewModel.cartItems.collectAsStateWithLifecycle().value
                val favouriteIds = viewModel.favouriteIds.collectAsStateWithLifecycle().value

                val cartQuantities = remember(cartItems) {
                    cartItems.associate { it.productId to it.quantity }
                }

                SearchScreen(
                    uiState = uiState,
                    query = query,
                    cartQuantities = cartQuantities,
                    favouriteIds = favouriteIds,
                    onQueryChanged = { viewModel.onQueryChanged(it) },
                    onSearchTriggered = { viewModel.performSearch(it) },
                    onClearHistory = { viewModel.clearHistory() },
                    onRemoveHistoryItem = { },
                    onBackClick = { findNavController().popBackStack() },
                    onProductClick = { product ->
                        val bundle = Bundle().apply { putInt("productId", product.id) }
                        findNavController().navigate(
                            R.id.action_searchFragment_to_productDetailsFragment,
                            bundle
                        )
                    },
                    onAddToCart = { product ->
                        viewModel.addToCart(product)
                        showSnackbarCart()
                    },
                    onIncrement = { product ->
                        viewModel.increaseQuantity(product)
                    },
                    onDecrement = { product ->
                        viewModel.decreaseQuantity(product)
                    },
                    onFavouriteClick = { product ->
                        viewModel.toggleFavourite(product)
                        showSnackbarFavourite()
                    }
                )
            }
        }
    }

    private fun showSnackbarCart() {
        actionSnackbar?.dismiss()
        actionSnackbar = Snackbar.make(
            requireView(),
            "Added to Cart Successfully",
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("Go to Cart") {
                findNavController().navigate(R.id.cartFragment)
            }
            setActionTextColor(ContextCompat.getColor(requireContext(), R.color.priceColor))
            show()
        }
    }

    private fun showSnackbarFavourite() {
        actionSnackbar?.dismiss()
        actionSnackbar = Snackbar.make(
            requireView(),
            "Added to Favourites Successfully",
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("Go to Favourites") {
                findNavController().navigate(R.id.favouritesFragment)
            }
            setActionTextColor(ContextCompat.getColor(requireContext(), R.color.priceColor))
            show()
        }
    }
}