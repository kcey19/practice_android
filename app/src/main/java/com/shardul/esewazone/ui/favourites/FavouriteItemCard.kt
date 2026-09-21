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
import java.util.Locale
import com.shardul.esewazone.data.model.FavouriteItem

@Composable
fun FavouriteItemCard(
    item: FavouriteItem,
    isSelected: Boolean,
    showCartButton: Boolean,
    onSelectionChange: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(
                Color.White
            )
            .clickable {
                onSelectionChange()
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image,
                contentDescription =
                    item.title,
                contentScale =
                    ContentScale.Fit,
                modifier = Modifier
                    .size(72.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
            )
            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {

                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        Color(0xFF292C3B),
                    maxLines = 1
                )
                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                )
                Text(
                    text =
                        item.category.uppercase(),
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color =
                        Color(0xFFB5B8C5),
                        maxLines = 1
                )
                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                )
                Text(
                    text = String.format(
                        Locale.US,
                        "Rs.%,.2f",
                        item.price
                    ),
                    fontSize = 17.sp,
                    color =
                        Color(0xFF292C3B)
                )
            }
            if (showCartButton) {
                IconButton(
                    onClick = {
                    },
                    modifier = Modifier
                        .size(20.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            Color(0xFF20B900)
                        )

                ) {
                    Icon(
                        imageVector =
                            Icons.Filled.ShoppingCart,
                        contentDescription =
                            "Add to cart",
                        tint =
                            Color.White,
                        modifier =
                            Modifier.size(22.dp)
                    )
                }
            }
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(
                        Alignment.TopStart
                    )
                    .offset(
                        x = (0).dp,
                        y = (0).dp
                    )
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF20B900)
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    imageVector =
                        Icons.Filled.Check,
                    contentDescription =
                        "Selected",
                    tint =
                        Color.White,
                    modifier =
                        Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun FavouriteItemCardPreview(
){
    FavouriteItemCard(
       item = FavouriteItem(
       ),true,true,{ Unit }
    )
}


