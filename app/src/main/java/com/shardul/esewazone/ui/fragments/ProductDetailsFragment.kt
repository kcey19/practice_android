package com.shardul.esewazone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.esewazone.adapters.ReviewAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shardul.esewazone.R
import com.shardul.esewazone.api.RetrofitInstance
import com.shardul.esewazone.data.model.ReviewModel
import com.shardul.esewazone.databinding.FragmentProductDetailsBinding
import com.shardul.esewazone.data.repository.ProductRepository
import com.shardul.esewazone.database.CartDatabase
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.repository.FavouriteRepository
import com.shardul.esewazone.viewmodel.CartViewModel
import com.shardul.esewazone.viewmodel.CartViewModelFactory
import com.shardul.esewazone.viewmodel.FavouriteViewModel
import com.shardul.esewazone.viewmodel.FavouriteViewModelFactory
import com.shardul.esewazone.viewmodel.ProductDetailsViewModel
import com.shardul.esewazone.viewmodel.ProductDetailsViewModelFactory

class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductDetailsViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var reviewAdapter: ReviewAdapter
    private var addToCartSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getInt("productId") ?: -1
        val repository = ProductRepository(RetrofitInstance.api)
        val factory = ProductDetailsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProductDetailsViewModel::class.java]

        val cartDao = CartDatabase.getDatabase(requireContext()).cartDao()
        val cartRepository = CartRepository(cartDao, FirebaseAuth.getInstance())
        val favouriteRepository = FavouriteRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

        val cartFactory = CartViewModelFactory(cartRepository)
        val favouriteFactory = FavouriteViewModelFactory(favouriteRepository)

        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]
        favouriteViewModel = ViewModelProvider(this, favouriteFactory)[FavouriteViewModel::class.java]

        viewModel.fetchProduct(productId)
        observeProduct()
        setupReviewsRecyclerView(productId.toString())
    }

    private fun observeProduct() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            binding.imgProduct.load(product.image)
            binding.txtTitle.text = product.title
            binding.txtCategory.text = product.category
            binding.txtPrice.text = "Rs. %.2f".format(product.price)
            binding.txtBottomPrice.text = "Rs. %.2f".format(product.price)
            binding.txtDescription.text = product.description

            binding.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            binding.btnAddToCart.setOnClickListener {
                cartViewModel.addToCart(product)
                showSnackbarCart()
            }

            binding.btnFavourite.setOnClickListener {
                favouriteViewModel.addToFavourites(product)
                showSnackbarFavourite()
            }
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
            bottomNav.selectedItemId = R.id.cartFragment
        }
        addToCartSnackbar?.setActionTextColor(
            ContextCompat.getColor(requireContext(), R.color.priceColor)
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
            ContextCompat.getColor(requireContext(), R.color.priceColor)
        )
        addToCartSnackbar?.show()
    }

    private fun setupReviewsRecyclerView(productId: String) {
        reviewAdapter = ReviewAdapter(emptyList())
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewAdapter
        }

        binding.btnWriteReview.setOnClickListener {
            showWriteReviewDialog(productId)
        }

        FirebaseFirestore.getInstance()
            .collection("products")
            .document(productId)
            .collection("reviews")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val reviewList = mutableListOf<ReviewModel>()
                var totalRatingScore = 0f

                snapshot?.forEach { doc ->
                    val review = doc.toObject(ReviewModel::class.java)
                    reviewList.add(review)
                    totalRatingScore += review.rating
                }

                reviewAdapter.updateReviews(reviewList)
                binding.txtReviewsHeading.text = "Reviews (${reviewList.size})"

                if (reviewList.isNotEmpty()) {
                    val average = totalRatingScore / reviewList.size
                    binding.txtAverageRating.text = "%.1f".format(average)
                    binding.txtTotalReviewsCount.text = "Based on ${reviewList.size} reviews"
                    binding.ratingBarSummary.rating = average // Lights up corresponding summary stars
                } else {
                    binding.txtAverageRating.text = "0.0"
                    binding.txtTotalReviewsCount.text = "No reviews yet"
                    binding.ratingBarSummary.rating = 0f // Resets summary stars to empty
                }
            }
    }

    private fun showWriteReviewDialog(productId: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_write_review, null)

        val ratingBar = dialogView.findViewById<RatingBar>(R.id.dialogRatingBar)
        val edtComment = dialogView.findViewById<EditText>(R.id.dialogEdtComment)
        val btnPublish = dialogView.findViewById<Button>(R.id.btnPublishFeedback)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnPublish.setOnClickListener {
            val rating = ratingBar.rating
            val comment = edtComment.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(requireContext(), "Please select a star rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitReviewToFirestore(productId, rating, comment)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun submitReviewToFirestore(productId: String, rating: Float, comment: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please login to write a review", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val reviewsRef = db.collection("products").document(productId).collection("reviews")
        val reviewId = reviewsRef.document().id

        val reviewMap = mapOf(
            "reviewId" to reviewId,
            "userId" to currentUser.uid,
            "userName" to (currentUser.displayName ?: "Anonymous User"),
            "userImage" to (currentUser.photoUrl?.toString() ?: ""),
            "rating" to rating,
            "comment" to comment,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        reviewsRef.document(reviewId).set(reviewMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to submit review: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}