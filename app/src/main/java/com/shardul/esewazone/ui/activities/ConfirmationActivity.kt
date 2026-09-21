package com.shardul.esewazone.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.EsewaPayment
import com.f1soft.esewapaymentsdk.ui.screens.EsewaPaymentActivity
import com.google.firebase.auth.FirebaseAuth
import com.shardul.esewazone.checkout.ConfirmationScreen
import com.shardul.esewazone.data.model.CheckoutItem
import com.shardul.esewazone.checkout.CheckoutFragment
import com.shardul.esewazone.database.CartDatabase
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.viewmodel.CartViewModel
import com.shardul.esewazone.viewmodel.CartViewModelFactory


class ConfirmationActivity : ComponentActivity() {

    private lateinit var viewModel: CartViewModel
    private var totalAmount: String = "0.00"
    private var paymentOption: String = ""

    private var productNames: Array<String> = emptyArray()

    private val esewaPaymentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    finalizeOrder(statusMessage="Pay with eSewa")
                    val message = result.data?.getStringExtra(
                            EsewaPayment.EXTRA_RESULT_MESSAGE
                        )
                    Log.i(
                        "eSewa",
                        "Payment successful: $message"
                    )

                }
                Activity.RESULT_CANCELED -> {
                    Log.i(
                        "eSewa",
                        "Payment cancelled by user"
                    )
                }
                EsewaPayment.RESULT_EXTRAS_INVALID -> {

                    val message =
                        result.data?.getStringExtra(
                            EsewaPayment.EXTRA_RESULT_MESSAGE
                        )

                    Log.e(
                        "eSewa",
                        "Invalid payment extras: $message"
                    )
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database =
            CartDatabase.getDatabase(this)
        val repository =
            CartRepository(database.cartDao(), FirebaseAuth.getInstance())
        val factory =
            CartViewModelFactory(repository)
        viewModel =
            ViewModelProvider(
                this,
                factory
            )[CartViewModel::class.java]

        paymentOption = intent.getStringExtra("PAYMENT_OPTION") ?: ""
        val deliveryAddress = intent.getStringExtra("DELIVERY_ADDRESS") ?: "Pulchowk,Lalitpur"
        val vehicleNumber = intent.getStringExtra("VEHICLE_NUMBER") ?: "BA 19 PA 2026"
        val deliveryCharge = intent.getStringExtra("DELIVERY_CHARGE") ?: "0.00"
        totalAmount = intent.getStringExtra("TOTAL_AMOUNT") ?: "0.00"

        val cartItems: ArrayList<CheckoutItem> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra( "CART_ITEMS", CheckoutItem::class.java ) ?: arrayListOf() } else
            { @Suppress("DEPRECATION") intent.getParcelableArrayListExtra<CheckoutItem>( "CART_ITEMS" ) ?: arrayListOf() }

         productNames = cartItems.map { it.name }.toTypedArray()
        val productPrices = cartItems.map { it.price.toString() }.toTypedArray()


        setContent {
            ConfirmationScreen(
                productNames = productNames,
                productPrices = productPrices,
                deliveryAddress = deliveryAddress,
                paymentOption = paymentOption,
                vehicleNumber = vehicleNumber,
                deliveryCharge = deliveryCharge,
                totalAmount = totalAmount,
                onBackClick = { finish() },
                onConfirmClick = { handleConfirmationAction() }
            )
        }
    }

    private fun handleConfirmationAction() {
        if (paymentOption == "Pay with eSewa") {
            startEsewaPayment()
        } else {
            finalizeOrder(statusMessage = "Order Placed (Cash on Delivery)")
        }
    }

    private fun startEsewaPayment() {
        val configuration =
            EsewaConfiguration(
                clientId = "JB0BBQ4aD0UqIThFJwAKBgAXEUkEGQUBBAwdOgABHD4DChwUAB0R",
                secretKey = "BhwIWQQADhIYSxILExMcAgFXFhcOBwAKBgAXEQ==",
                environment = EsewaConfiguration.ENVIRONMENT_TEST
            )

        val payment = EsewaPayment(
            amount = totalAmount,
            productName = "KceyProduct",
            productUniqueId = "TEST-ORDER-001",
            callbackUrl = "https://www.sinasamaki.com"
        )

        val intent =
            Intent(
                this,
                EsewaPaymentActivity::class.java
            )

        intent.putExtra(
            EsewaConfiguration.ESEWA_CONFIGURATION,
            configuration
        )

        intent.putExtra(
            EsewaPayment.ESEWA_PAYMENT,
            payment
        )
        esewaPaymentLauncher.launch(intent)
    }

    private fun finalizeOrder(statusMessage: String) {

        viewModel.clearUserCart()

        val intent = Intent(this, OrderSuccessActivity::class.java).apply {

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            putExtra("TOTAL_AMOUNT", totalAmount)
            putExtra("PAYMENT_OPTION", statusMessage)
        }

        startActivity(intent)
        finish()
    }
}