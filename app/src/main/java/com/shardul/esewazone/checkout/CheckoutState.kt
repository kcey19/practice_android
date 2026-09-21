package com.shardul.esewazone.checkout

data class CheckoutState(
    val cartItems: List<CheckoutCartItem> = emptyList(),
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val shippingAddress: ShippingAddress? = null,
    val promoCode: String = "",
    val promoDiscount: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val subtotal: Double
        get() = cartItems.sumOf {
            it.price * it.quantity
        }
    val tax: Double
        get() = 0.0
    val shippingCharge: Double
        get() = if (cartItems.isEmpty()) {
            0.0
        } else {
            50.0
        }
    val grandTotal: Double
        get() = (
                subtotal +
                        tax +
                        shippingCharge -
                        promoDiscount
                ).coerceAtLeast(0.0)
}

data class CheckoutCartItem(
    val productId: Int,
    val title: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int
)


data class ShippingAddress(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val phone: String
)

enum class PaymentMethod {
    CASH_ON_DELIVERY,
    ESEWA
}