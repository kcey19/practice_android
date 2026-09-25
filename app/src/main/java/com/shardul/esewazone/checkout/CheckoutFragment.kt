package com.shardul.esewazone.checkout

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController

import com.google.firebase.auth.FirebaseAuth
import com.shardul.esewazone.R
import com.shardul.esewazone.database.CartDatabase
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.shipping.ShippingAddressRepository
import com.shardul.esewazone.ui.location.LocationPickerFragment
import com.shardul.esewazone.ui.location.NoAddressBottomSheet
import com.f1soft.esewapaymentsdk.EsewaPayment
import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.ui.screens.EsewaPaymentActivity


class CheckoutFragment : Fragment() {

    private val viewModel: CheckoutViewModel by viewModels {
        CheckoutViewModelFactory(
            CartRepository(
                CartDatabase
                    .getDatabase(requireContext())
                    .cartDao(),
                FirebaseAuth.getInstance()
            )
        )
    }

    private var waitingForLocationSettings = false

    private val esewaPaymentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val message = result.data?.getStringExtra(
                        EsewaPayment.EXTRA_RESULT_MESSAGE
                    )
                    Log.i("eSewa", "Payment successful: $message")
                }
                Activity.RESULT_CANCELED -> {
                    Log.i("eSewa", "Payment cancelled by user")
                }
                EsewaPayment.RESULT_EXTRAS_INVALID -> {
                    val message = result.data?.getStringExtra(
                        EsewaPayment.EXTRA_RESULT_MESSAGE
                    )
                    Log.e("eSewa", "Invalid payment extras: $message")
                }
            }
        }

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                navigateToLocationPicker()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ShippingAddressRepository(
            requireContext(),
            FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        ).default()?.let { saved ->
            viewModel.setShippingAddress(
                ShippingAddress(
                    id = saved.id,
                    name = saved.name,
                    address = saved.address,
                    city = "",
                    phone = saved.phone
                )
            )
        }

        parentFragmentManager.setFragmentResultListener(
            LocationPickerFragment.REQUEST_KEY,
            this
        ) { _, result ->
            val address = result.getString(LocationPickerFragment.RESULT_ADDRESS) ?: return@setFragmentResultListener
            val latitude = result.getDouble(LocationPickerFragment.RESULT_LATITUDE)
            val longitude = result.getDouble(LocationPickerFragment.RESULT_LONGITUDE)

            viewModel.setShippingAddress(
                ShippingAddress(
                    id = "location-$latitude-$longitude",
                    name = FirebaseAuth.getInstance().currentUser?.displayName.orEmpty(),
                    address = address,
                    city = "",
                    phone = FirebaseAuth.getInstance().currentUser?.phoneNumber.orEmpty()
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (waitingForLocationSettings) {
            if (isLocationEnabled()) {
                requestLocationPermissionOrNavigate()
            }
        }
    }

    private fun startEsewaPayment() {
        val configuration = EsewaConfiguration(
            clientId = "JB0BBQ4aD0UqIThFJwAKBgAXEUkEGQUBBAwdOgABHD4DChwUAB0R",
            secretKey = "BhwIWQQADhIYSxILExMcAgFXFhcOBwAKBgAXEQ==",
            environment = EsewaConfiguration.ENVIRONMENT_TEST
        )

        val payment = EsewaPayment(
            amount = "1",
            productName = "Test Product",
            productUniqueId = "TEST-ORDER-001",
            callbackUrl = "https://www.sinasamaki.com"
        )

        val intent = Intent(requireContext(), EsewaPaymentActivity::class.java)
        intent.putExtra(EsewaConfiguration.ESEWA_CONFIGURATION, configuration)
        intent.putExtra(EsewaPayment.ESEWA_PAYMENT, payment)

        esewaPaymentLauncher.launch(intent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    private fun beginAddressSelection() {
        if (!isLocationEnabled()) {
            waitingForLocationSettings = true
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }
        requestLocationPermissionOrNavigate()
    }

    private fun requestLocationPermissionOrNavigate() {
        if (hasLocationPermission()) {
            navigateToLocationPicker()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun navigateToLocationPicker() {
        waitingForLocationSettings = false
        findNavController().navigate(
            R.id.action_checkoutFragment_to_locationPickerFragment
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy
                    .DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                val state =
                    viewModel.uiState
                        .collectAsStateWithLifecycle()
                        .value

                fun checkAddressAndExecute(onValid: () -> Unit) {
                    val addressText = state.shippingAddress?.address.orEmpty()
                    val hasValidAddress = state.shippingAddress != null && addressText.isNotBlank()

                    if (!hasValidAddress) {
                        val bottomSheet = NoAddressBottomSheet()
                        bottomSheet.setOnSetAddressListener {
                            beginAddressSelection()
                        }
                        bottomSheet.show(parentFragmentManager, "NoAddressDialog")
                    } else {
                        onValid()
                    }
                }

                CheckoutScreen(
                    state = state,
                    onBackClick = {
                        findNavController().popBackStack()
                    },
                    onAddressClick = {
                        beginAddressSelection()
                    },
                    onPromoCodeClick = { code ->
                        viewModel.applyPromoCode(code)
                    },
                    onRemovePromoCode = {
                        viewModel.removePromoCode()
                    },
                    onPaymentMethodSelected = { method ->

                        checkAddressAndExecute {
                            viewModel.selectPaymentMethod(method)
                        }
                    },
                    onPlaceOrder = {
                        checkAddressAndExecute {
                            startEsewaPayment()
                        }
                    }
                )
            }
        }
    }
}