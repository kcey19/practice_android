package com.shardul.esewazone.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shardul.esewazone.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CheckoutState()
    )
    val uiState: StateFlow<CheckoutState> =
        _uiState.asStateFlow()

    init {
        observeCart()
    }
    private fun observeCart() {
        viewModelScope.launch {
            cartRepository
                .getActiveUserCart()
                .collect { cartItems ->
                    val checkoutItems = cartItems.map { item ->
                        CheckoutCartItem(
                            productId = item.productId,
                            title = item.title,
                            imageUrl = item.image,
                            price = item.price,
                            quantity = item.quantity
                        )
                    }
                    _uiState.update {
                        it.copy(
                            cartItems = checkoutItems,
                            isLoading = false
                        )
                    }
                }
        }
    }
    fun selectPaymentMethod(
        method: PaymentMethod
    ) {
        _uiState.update {
            it.copy(
                selectedPaymentMethod = method
            )
        }
    }
    fun setShippingAddress(
        address: ShippingAddress
    ) {
        _uiState.update {
            it.copy(
                shippingAddress = address
            )
        }
    }

    fun applyPromoCode(
        code: String
    ) {
        val discount = when (code.trim().uppercase()) {
            "SAVE10" ->
                _uiState.value.subtotal * 0.10
            "WELCOME" ->
                100.0
            else ->
                0.0
        }

        _uiState.update {
            it.copy(
                promoCode = code,
                promoDiscount = discount
            )
        }
    }

    fun removePromoCode() {
        _uiState.update {
            it.copy(
                promoCode = "",
                promoDiscount = 0.0
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                error = null
            )
        }
    }


    fun placeOrder(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.cartItems.isEmpty()) {
                _uiState.update {
                    it.copy(
                        error = "Your cart is empty"
                    )
                }
                return@launch
            }
            if (state.shippingAddress == null) {
                _uiState.update {
                    it.copy(
                        error = "Please add a shipping address"
                    )
                }
                return@launch
            }
            onSuccess()
        }
    }
}