package com.shardul.esewazone.ui.favourites

import androidx.compose.foundation.Image
import com.shardul.esewazone.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shardul.esewazone.data.model.FavouriteItem
import com.shardul.esewazone.viewmodel.FavouriteViewModel
import java.util.Locale


@Composable
fun FavouritesScreen(
    viewModel: FavouriteViewModel,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onDeleteAll: () -> Unit,
    onDelete: (Set<Int>) -> Unit
) {

    val favourites by
    viewModel.favourites.collectAsState()

    val isLoading by
    viewModel.isLoading.collectAsState()

    val error by
    viewModel.error.collectAsState()

    var selectedItems by remember {
        mutableStateOf<Set<Int>>(
            emptySet()
        )
    }
    val allSelected =
        favourites.isNotEmpty() &&
                selectedItems.size == favourites.size
    val isSelected =
        favourites.isNotEmpty() &&
                selectedItems.size == 1
    val selectionMode =
        selectedItems.isNotEmpty()
    val hasSelection = selectedItems.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF7F9FC)
            )
    ) {
        FavouriteTopBar(
            onBackClick = onBackClick,
            onCartClick = onCartClick
        )
        FavouriteSelectionBar(
            itemCount = favourites.size,
            allSelected = allSelected,
            onSelectAll = {
                if (allSelected) {
                    selectedItems =
                        emptySet()
                } else {
                    selectedItems =
                        favourites
                            .map {
                                it.productId
                            }
                            .toSet()
                }
            },
            hasSelection = hasSelection,
            onDeleteSelected = {
                if(allSelected){
                    onDeleteAll()
                }else{
                    onDelete(selectedItems)
                }
                selectedItems = emptySet()
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(
                                Alignment.Center
                            )
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Something went wrong",
                        modifier = Modifier
                            .align(
                                Alignment.Center
                            )
                            .padding(24.dp),
                        fontSize = 15.sp
                    )
                }
                favourites.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp, horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .background(
                                            color = Color(0xFFF5F7FA),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Image(
                                        painter = painterResource(id = R.drawable.empty_favourite),
                                        contentDescription = "No favourites",
                                        modifier = Modifier.size(80.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "No favorites yet",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E2538)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Add your favorites to wishlist and\nthey will show here.",
                                    fontSize = 14.sp,
                                    color = Color(0xFF8C96A6),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF2ABB00)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = ButtonDefaults.ContentPadding
                                ) {
                                    Text(
                                        text = "CONTINUE SHOPPING",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = favourites,
                            key = {
                                it.productId
                            }
                        ) { item ->
                            val isSelected =
                                selectedItems
                                    .contains(
                                        item.productId
                                    )
                            FavouriteItemCard(
                                item = item,
                                isSelected =
                                    isSelected,
                                showCartButton =
                                    isSelected,
                                onSelectionChange = {
                                    selectedItems =
                                        if (isSelected) {
                                            selectedItems -
                                                    item.productId
                                        } else {
                                            selectedItems +
                                                    item.productId
                                        }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
