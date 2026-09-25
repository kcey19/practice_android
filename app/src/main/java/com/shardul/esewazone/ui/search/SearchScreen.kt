package com.shardul.esewazone.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shardul.esewazone.data.model.Product
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

private val Background = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    query: String,
    cartQuantities: Map<Int, Int>,
    favouriteIds: Set<Int>,
    onQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onClearHistory: () -> Unit,
    onRemoveHistoryItem: (String) -> Unit,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onIncrement: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
    onFavouriteClick: (Product) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChanged = onQueryChanged,
                onSearchClick = { onSearchTriggered(query) },
                onBackClick = onBackClick,
                onClearQuery = { onQueryChanged("") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is SearchUiState.Idle -> {
                    SearchIdleContent(
                        history = uiState.searchHistory,
                        popularProducts = uiState.popularSearches,
                        onClearAllHistory = onClearHistory,
                        onRemoveItem = onRemoveHistoryItem,
                        onSuggestionClick = { clickedQuery: String ->
                            onQueryChanged(clickedQuery)
                            onSearchTriggered(clickedQuery)
                        }
                    )
                }
                is SearchUiState.Suggestions -> {
                    SearchSuggestionsList(
                        suggestions = uiState.suggestions,
                        onSuggestionClick = { suggestion: String ->
                            onQueryChanged(suggestion)
                            onSearchTriggered(suggestion)
                        }
                    )
                }
                is SearchUiState.Results -> {
                    SearchResultsContent(
                        products = uiState.products,
                        totalCount = uiState.totalCount,
                        cartQuantities = cartQuantities,
                        favouriteIds = favouriteIds,
                        onFilterClick = { showFilterSheet = true },
                        onSortClick = { showSortSheet = true },
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onIncrement = onIncrement,
                        onDecrement = onDecrement,
                        onFavouriteClick = onFavouriteClick
                    )
                }
                is SearchUiState.Empty -> {
                    SearchEmptyState(
                        onSearchAgainClick = { onQueryChanged("") }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onClearQuery: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        placeholder = { Text("Search products...", fontSize = 14.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2ABB00),
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color(0xFFF8F9FA),
                            unfocusedContainerColor = Color(0xFFF8F9FA)
                        ),
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                if (query.isNotEmpty()) {
                                    IconButton(
                                        onClick = onClearQuery,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear query",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = onSearchClick,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color(0xFF2ABB00),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.DarkGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    }
}

@Composable
fun SearchIdleContent(
    history: List<String>,
    popularProducts: List<String>,
    onClearAllHistory: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onSuggestionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (history.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Searches", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
                TextButton(onClick = onClearAllHistory) {
                    Text("Clear All", color = Color(0xFF2ABB00), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            history.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(item) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item, fontSize = 13.sp, color = Color.Gray)
                    IconButton(onClick = { onRemoveItem(item) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (popularProducts.isNotEmpty()) {
            Text("Popular Searches", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            popularProducts.forEach { popItem ->
                TextButton(onClick = { onSuggestionClick(popItem) }) {
                    Text(popItem, color = Color(0xFF2ABB00), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun SearchSuggestionsList(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        items(suggestions) { suggestion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionClick(suggestion) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(suggestion, fontSize = 14.sp, color = Color.DarkGray)
            }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
        }
    }
}

@Composable
fun SearchEmptyState(
    onSearchAgainClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No results found", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSearchAgainClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00))
            ) {
                Text("Search Again", color = Color.White)
            }
        }
    }
}

@Composable
private fun SearchResultsContent(
    products: List<Product>,
    totalCount: Int,
    cartQuantities: Map<Int, Int>,
    favouriteIds: Set<Int>,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onIncrement: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
    onFavouriteClick: (Product) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onFilterClick) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF2ABB00))
                Spacer(modifier = Modifier.width(4.dp))
                Text("FILTER ($totalCount)", color = Color.DarkGray, fontSize = 12.sp)
            }
            TextButton(onClick = onSortClick) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null, tint = Color(0xFF2ABB00))
                Spacer(modifier = Modifier.width(4.dp))
                Text("SORT BY", color = Color.DarkGray, fontSize = 12.sp)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(products) { product ->
                val qty = cartQuantities[product.id] ?: 0
                val isFav = favouriteIds.contains(product.id)

                ProductItemCard(
                    product = product,
                    quantity = qty,
                    isFavourite = isFav,
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                    onFavouriteClick = onFavouriteClick
                )
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    quantity: Int,
    isFavourite: Boolean,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onIncrement: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
    onFavouriteClick: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.title,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.category.uppercase(LocalLocale.current.platformLocale),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rs. ${product.price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2ABB00)
                )

                Spacer(modifier = Modifier.height(4.dp))

                IconButton(
                    onClick = { onFavouriteClick(product) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavourite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                if (quantity > 0) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                        color = Color(0xFF2ABB00)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = { onDecrement(product) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = String.format(LocalLocale.current.platformLocale, "%02d", quantity),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { onIncrement(product) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                        color = Color(0xFF2ABB00),
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { onAddToCart(product) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add to Cart",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}