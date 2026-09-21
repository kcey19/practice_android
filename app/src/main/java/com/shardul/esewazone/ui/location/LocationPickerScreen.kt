package com.shardul.esewazone.ui.location

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.shardul.esewazone.data.model.SelectedLocation

private val LocationGreen = Color(0xFF20C300)

@Composable
fun LocationPickerScreen(
    cameraTarget: LatLng?,
    selectedLocation: SelectedLocation?,
    hasLocationPermission: Boolean,
    searchQuery: String,
    suggestions: List<PlaceSuggestion>,

    onSearchQueryChanged: (String) -> Unit,
    onSuggestionClick: (PlaceSuggestion) -> Unit,
    onMyLocationClick: () -> Unit,
    onCameraIdle: (LatLng) -> Unit,
    onConfirmLocation: (LatLng) -> Unit,
    onCameraTargetConsumed: () -> Unit,
    onPoiClick: (PointOfInterest) -> Unit
) {

    val fallbackLocation = LatLng(
        27.7172,
        85.3240
    )

    val initialLocation = remember {
        cameraTarget ?: fallbackLocation
    }

    val cameraPositionState =
        rememberCameraPositionState {
            position =
                CameraPosition.fromLatLngZoom(
                    initialLocation,
                    14f
                )
        }


    LaunchedEffect(cameraTarget) {

        cameraTarget?.let { target ->

            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    target,
                    17f
                ),
                800
            )

            /*
             * Important:
             *
             * Do NOT call onCameraIdle() here.
             *
             * This is a programmatic camera movement,
             * not the user moving the map.
             */
            onCameraTargetConsumed()
        }
    }


    /*
     * Detect actual camera movement finishing.
     *
     * This is where we notify the Fragment that the user
     * has moved the map.
     */
    LaunchedEffect(
        cameraPositionState.isMoving
    ) {

        if (!cameraPositionState.isMoving) {

            val target =
                cameraPositionState.position.target

            onCameraIdle(target)
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        GoogleMap(

            modifier = Modifier.fillMaxSize(),

            cameraPositionState =
                cameraPositionState,

            properties =
                MapProperties(
                    isMyLocationEnabled =
                        hasLocationPermission
                ),

            uiSettings =
                MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                ),

            onPOIClick = { poi ->
                onPoiClick(poi)
            }
        )


        /*
         * SEARCH BAR
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
        ) {

            OutlinedTextField(

                value = searchQuery,

                onValueChange =
                    onSearchQueryChanged,

                modifier =
                    Modifier.fillMaxWidth(),

                leadingIcon = {
                    Icon(
                        imageVector =
                            Icons.Default.Search,
                        contentDescription =
                            "Search"
                    )
                },

                placeholder = {
                    Text(
                        "Search for an address or place"
                    )
                },

                singleLine = true,

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White,

                        focusedBorderColor =
                            LocationGreen,

                        unfocusedBorderColor =
                            Color.Transparent,

                        cursorColor =
                            LocationGreen
                    )
            )


            if (suggestions.isNotEmpty()) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),

                    shape =
                        RoundedCornerShape(12.dp),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                ) {

                    LazyColumn(
                        modifier =
                            Modifier.heightIn(
                                max = 260.dp
                            )
                    ) {

                        items(
                            items = suggestions,
                            key = {
                                it.placeId
                            }
                        ) { suggestion ->

                            Column(

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color.White
                                    )
                                    .clickable {
                                        onSuggestionClick(
                                            suggestion
                                        )
                                    }
                                    .padding(16.dp)
                            ) {

                                Text(
                                    text =
                                        suggestion.primaryText,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyLarge
                                )

                                if (
                                    suggestion
                                        .secondaryText
                                        .isNotBlank()
                                ) {

                                    Text(
                                        text =
                                            suggestion
                                                .secondaryText,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall,

                                        color =
                                            Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }


        /*
         * CENTER MAP PIN
         */
        Icon(
            imageVector =
                Icons.Default.LocationOn,

            contentDescription =
                "Selected location",

            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
                .offset(y = (-54).dp),

            tint = LocationGreen
        )


        /*
         * BOTTOM CONTROLS
         */
        Column(

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .fillMaxWidth()
        ) {

            /*
             * CURRENT LOCATION FAB
             */
            FloatingActionButton(

                onClick =
                    onMyLocationClick,

                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        end = 16.dp,
                        bottom = 16.dp
                    ),

                containerColor =
                    Color.White
            ) {

                Icon(

                    imageVector =
                        Icons.Default.MyLocation,

                    contentDescription =
                        "Use my location",

                    tint =
                        LocationGreen
                )
            }


            /*
             * LOCATION INFORMATION CARD
             */
            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),

                shape =
                    RoundedCornerShape(20.dp),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    )
            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    val location =
                        selectedLocation


                    Text(
                        text =
                            "Selected location",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )


                    Text(
                        text =
                            location?.name
                                ?: "Fetching location...",

                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge
                    )


                    location?.address?.let { address ->

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text = address,

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    Button(

                        onClick = {

                            onConfirmLocation(
                                cameraPositionState
                                    .position
                                    .target
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            location != null,

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    LocationGreen,

                                contentColor =
                                    Color.White
                            )
                    ) {

                        Text(
                            "Confirm location"
                        )
                    }
                }
            }
        }
    }
}