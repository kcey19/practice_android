package com.shardul.esewazone.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.shardul.esewazone.data.model.FavouriteItem
import com.shardul.esewazone.data.model.Product
import kotlinx.coroutines.tasks.await

class FavouriteRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun addToFavourites(
        product: Product
    ): Result<Unit> {

        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(
                    Exception("User is not authenticated")
                )

            val favouriteItem = FavouriteItem(
                productId = product.id,
                title = product.title,
                price = product.price,
                category = product.category,
                image = product.image
            )
            firestore
                .collection("users")
                .document(currentUser.uid)
                .collection("favourites")
                .document(product.id.toString())
                .set(favouriteItem)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getFavourites(): Result<List<FavouriteItem>> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(
                    Exception("User is not authenticated")
                )

            val snapshot = firestore
                .collection("users")
                .document(currentUser.uid)
                .collection("favourites")
                .get()
                .await()

            val favourites = snapshot.documents.mapNotNull { document ->
                document.toObject<FavouriteItem>()
            }

            Result.success(favourites)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}