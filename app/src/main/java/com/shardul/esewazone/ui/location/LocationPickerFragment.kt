package com.shardul.esewazone.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.tasks.CancellationTokenSource

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

import com.shardul.esewazone.BuildConfig
import com.shardul.esewazone.data.model.SelectedLocation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

import java.util.Locale
import kotlin.coroutines.resume

class LocationPickerFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    private var cameraTarget by mutableStateOf<LatLng?>(null)
    private var selectedLocation by mutableStateOf<SelectedLocation?>(null)

    private var searchQuery by mutableStateOf("")

    private var suggestions by mutableStateOf<List<PlaceSuggestion>>(emptyList())

    private var geocodeJob: Job? = null

    private var predictionJob: Job? = null

    private var autocompleteSessionToken =
        AutocompleteSessionToken.newInstance()

    private var searchRequestVersion = 0L


    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationGranted || coarseLocationGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required to use your current location.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private var isInitialLocationResolved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(
                requireContext().applicationContext,
                BuildConfig.MAPS_API_KEY
            )
        }

        placesClient = Places.createClient(requireContext())

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (hasLocationPermission()) {
            getCurrentLocation()
        } else {
            isInitialLocationResolved = true
        }

    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor =
            Color.rgb(32, 195, 0)
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {

        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        setContent {

            LocationPickerScreen(
                cameraTarget = cameraTarget,
                selectedLocation = selectedLocation,
                hasLocationPermission = hasLocationPermission(),

                searchQuery = searchQuery,
                suggestions = suggestions,

                onSearchQueryChanged = ::searchPlaces,
                onSuggestionClick = ::fetchSelectedPlace,

                onMyLocationClick = ::handleMyLocationClick,

                onCameraIdle = ::handleCameraIdle,
                onConfirmLocation = ::confirmLocation,
                onPoiClick = ::fetchSelectedPoi,
                onCameraTargetConsumed = {
                    cameraTarget = null
                }
            )
        }
    }

    private fun hasLocationPermission(): Boolean {

        val fineLocation =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    private fun handleMyLocationClick() {

        if (hasLocationPermission()) {
            getCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            )
            .addOnSuccessListener { location ->

                if (location == null) {
                    isInitialLocationResolved=true
                    Toast.makeText(
                        requireContext(),
                        "Unable to determine your current location.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val latLng = LatLng(
                    location.latitude,
                    location.longitude
                )
                selectedLocation =
                    SelectedLocation(
                        name = "Current location",
                        address = "Fetching address...",
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                cameraTarget = latLng
                isInitialLocationResolved=true
                geocodeSelectedPlace(
                    location = latLng,
                    fallbackAddress = null
                )
            }

            .addOnFailureListener {
                isInitialLocationResolved=true
                Toast.makeText(
                    requireContext(),
                    "Could not get your current location.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun searchPlaces(query: String) {

        searchQuery = query
        predictionJob?.cancel()
        searchRequestVersion++

        val currentRequestVersion = searchRequestVersion
        if (query.trim().length < 2) {
            suggestions = emptyList()
            return
        }

        predictionJob =
            viewLifecycleOwner.lifecycleScope.launch {
                delay(300)

                val trimmedQuery = query.trim()

                val request =
                    FindAutocompletePredictionsRequest
                        .builder()
                        .setQuery(trimmedQuery)
                        .setSessionToken(autocompleteSessionToken)
                        .build()

                placesClient
                    .findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->

                        if (currentRequestVersion != searchRequestVersion) {
                            return@addOnSuccessListener
                        }

                        if (searchQuery.trim() != trimmedQuery) {
                            return@addOnSuccessListener
                        }

                        suggestions =
                            response.autocompletePredictions.map { prediction ->

                                PlaceSuggestion(
                                    placeId = prediction.placeId,

                                    primaryText =
                                        prediction
                                            .getPrimaryText(null)
                                            .toString(),

                                    secondaryText =
                                        prediction
                                            .getSecondaryText(null)
                                            .toString()
                                )
                            }
                    }
                    .addOnFailureListener { exception ->

                        android.util.Log.e(
                            "LocationSearch",
                            "Places autocomplete failed",
                            exception
                        )

                        Toast.makeText(
                            requireContext(),
                            "Search failed: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        suggestions = emptyList()
                    }
            }
    }

    private fun fetchSelectedPlace(
        suggestion: PlaceSuggestion
    ) {

        suggestions = emptyList()
        searchQuery = suggestion.primaryText

        predictionJob?.cancel()
        searchRequestVersion++

        val placeFields =
            listOf(
                Place.Field.ID,
                Place.Field.LOCATION,
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS
            )

        val request =
            FetchPlaceRequest
                .builder(
                    suggestion.placeId,
                    placeFields
                )
                .setSessionToken(
                    autocompleteSessionToken
                )
                .build()

        placesClient
            .fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val location = place.location
                if (location == null) {
                    Toast.makeText(
                        requireContext(),
                        "This place does not have a valid location.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                selectedLocation =
                    SelectedLocation(
                        name =
                            place.displayName
                                ?.toString()
                                ?: suggestion.primaryText,

                        address = null,
                        latitude = location.latitude,
                        longitude = location.longitude,

                        placeId = place.id
                    )

                cameraTarget = location
                geocodeSelectedPlace(
                    location = location,
                    fallbackAddress =
                        place.formattedAddress
                )

                autocompleteSessionToken =
                    AutocompleteSessionToken.newInstance()
            }

            .addOnFailureListener { exception ->

                android.util.Log.e(
                    "LocationSearch",
                    "Unable to fetch selected place",
                    exception
                )

                Toast.makeText(
                    requireContext(),
                    "Unable to find that place.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun handleCameraIdle(
        location: LatLng
    ) {
        if (!isInitialLocationResolved) {
            return
        }

        geocodeJob?.cancel()
        val current =
            selectedLocation

        if (
            current != null &&
            isSameLocation(
                current.latitude,
                current.longitude,
                location
            )
        ) {
            return
        }
        selectedLocation =
            SelectedLocation(
                name = "Selected location",
                address = "Fetching address...",
                latitude = location.latitude,
                longitude = location.longitude
            )

        geocodeJob =
            viewLifecycleOwner.lifecycleScope.launch {

                delay(300)

                val address =
                    reverseGeocode(location)

                val currentSelection =
                    selectedLocation

                if (
                    currentSelection != null &&
                    isSameLocation(
                        currentSelection.latitude,
                        currentSelection.longitude,
                        location
                    )
                ) {

                    selectedLocation =
                        currentSelection.copy(
                            address =
                                address
                                    ?: "Address not found"
                        )
                }
            }
    }

    private suspend fun reverseGeocode(
        location: LatLng
    ): String? =
        withContext(Dispatchers.IO) {

            try {
                val geocoder = Geocoder(
                    requireContext(),
                    Locale.getDefault()
                )

                val address = if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ) {

                    suspendCancellableCoroutine<Address?> { continuation ->

                        geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1,
                            object : Geocoder.GeocodeListener {

                                override fun onGeocode(
                                    addresses: MutableList<Address>
                                ) {
                                    continuation.resume(
                                        addresses.firstOrNull()
                                    )
                                }

                                override fun onError(
                                    errorMessage: String?
                                ) {
                                    continuation.resume(null)
                                }
                            }
                        )
                    }

                } else {

                    @Suppress("DEPRECATION")
                    geocoder
                        .getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )
                        ?.firstOrNull()
                }

                address?.let(::buildReadableAddress)

            } catch (e: Exception) {

                android.util.Log.e(
                    "LocationSearch",
                    "Reverse geocoding failed",
                    e
                )

                null
            }
        }

    private fun buildReadableAddress(
        address: Address
    ): String {

        val parts = mutableListOf<String>()

        address.subLocality
            ?.takeIf { it.isNotBlank() }
            ?.let {
                parts.add(it)
            }

        address.locality
            ?.takeIf { it.isNotBlank() }
            ?.let {
                if (it !in parts) {
                    parts.add(it)
                }
            }

        address.subAdminArea
            ?.takeIf { it.isNotBlank() }
            ?.let {
                if (it !in parts) {
                    parts.add(it)
                }
            }

        address.adminArea
            ?.takeIf { it.isNotBlank() }
            ?.let {
                if (it !in parts) {
                    parts.add(it)
                }
            }

        address.postalCode
            ?.takeIf { it.isNotBlank() }
            ?.let {
                parts.add(it)
            }

        return parts.joinToString(", ")
    }

    private fun cleanFormattedAddress(
        address: String?
    ): String? {

        if (address.isNullOrBlank()) {
            return null
        }
        if (address.contains("+")) {
            return null
        }

        return address
    }

    private fun isSameLocation(
        latitude: Double,
        longitude: Double,
        location: LatLng
    ): Boolean {

        val latitudeDifference =
            kotlin.math.abs(
                latitude - location.latitude
            )

        val longitudeDifference =
            kotlin.math.abs(
                longitude - location.longitude
            )

        return latitudeDifference < 0.0001 &&
                longitudeDifference < 0.0001
    }



    private fun confirmLocation(
        latLng: LatLng
    ) {

        val currentSelected =
            selectedLocation

        val displayAddress =
            currentSelected?.address
                ?.takeIf {
                    it.isNotBlank() &&
                            it != "Fetching address..."
                }
                ?: currentSelected?.name
                ?: "${latLng.latitude}, ${latLng.longitude}"

        parentFragmentManager.setFragmentResult(

            REQUEST_KEY,
            Bundle().apply {

                putDouble(
                    RESULT_LATITUDE,
                    latLng.latitude
                )

                putDouble(
                    RESULT_LONGITUDE,
                    latLng.longitude
                )

                putString(
                    RESULT_ADDRESS,
                    displayAddress
                )

                putString(
                    RESULT_PLACE_NAME,
                    currentSelected?.name
                        ?: "Selected Location"
                )
            }
        )

        findNavController()
            .popBackStack()
    }
    private fun fetchSelectedPoi(
        poi: PointOfInterest
    ) {

        suggestions = emptyList()

        predictionJob?.cancel()

        val placeFields =
            listOf(
                Place.Field.ID,
                Place.Field.LOCATION,
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS
            )

        val request =
            FetchPlaceRequest
                .builder(
                    poi.placeId,
                    placeFields
                )
                .build()

        placesClient
            .fetchPlace(request)

            .addOnSuccessListener { response ->

                val place =
                    response.place

                val location =
                    place.location

                if (location == null) {

                    Toast.makeText(
                        requireContext(),
                        "This place does not have a valid location.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                selectedLocation = SelectedLocation(
                        name =
                            place.displayName
                                ?.toString()
                                ?: poi.name,

                        address = null,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        placeId = place.id
                    )

                cameraTarget =
                    location

                geocodeSelectedPlace(
                    location = location,
                    fallbackAddress =
                        place.formattedAddress
                )
            }

            .addOnFailureListener { exception ->
                android.util.Log.e(
                    "LocationSearch",
                    "Unable to fetch POI",
                    exception
                )

                Toast.makeText(
                    requireContext(),
                    "Unable to load this place.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun geocodeSelectedPlace(
        location: LatLng,
        fallbackAddress: String?
    ) {
        geocodeJob?.cancel()
        geocodeJob =
            viewLifecycleOwner.lifecycleScope.launch {
                val readableAddress =
                    reverseGeocode(location)
                        ?: cleanFormattedAddress(fallbackAddress)
                selectedLocation = selectedLocation?.copy(
                    address = readableAddress
                )
            }
    }

    override fun onDestroyView() {
        geocodeJob?.cancel()
        predictionJob?.cancel()
        super.onDestroyView()
    }

    companion object {
        const val REQUEST_KEY = "location_picker_result"
        const val RESULT_LATITUDE = "latitude"
        const val RESULT_LONGITUDE = "longitude"
        const val RESULT_ADDRESS = "address"
        const val RESULT_PLACE_NAME = "place_name"

    }
}

data class PlaceSuggestion(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String
)