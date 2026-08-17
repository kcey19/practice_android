package com.shardul.esewazone.ui.favourites

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
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
                    Text(
                        text = "No favourites yet",
                        modifier = Modifier
                            .align(
                                Alignment.Center
                            ),
                        fontSize = 16.sp
                    )
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
