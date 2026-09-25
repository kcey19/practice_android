package com.shardul.esewazone.checkout


import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.shardul.esewazone.R
import com.shardul.esewazone.data.model.CheckoutItem
import com.shardul.esewazone.ui.activities.ConfirmationActivity


private val Green = Color(0xFF2ABB00)
private val Background = Color(0xFFF7F8FA)
private val DarkText = Color(0xFFF1FFED)


@Composable
fun CheckoutScreen(
    state: CheckoutState,
    onBackClick: () -> Unit,
    onAddressClick: () -> Unit,
    onPromoCodeClick: (String) -> Unit,
    onRemovePromoCode: () -> Unit,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onPlaceOrder: () -> Unit
) {
    var showPromoSheet by rememberSaveable {
        mutableStateOf(false)
    }
    var showAddressSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current


//    fun proceedToConfirmation(paymentMethod: String) {
//        val intent = Intent(context, ConfirmationActivity::class.java).apply {
//            putExtra("PAYMENT_OPTION", paymentMethod)
//            putExtra("DELIVERY_ADDRESS", state.shippingAddress.toString())
//            putExtra("DELIVERY_CHARGE", state.shippingCharge)
//            putExtra("TOTAL_AMOUNT", state.grandTotal)
//
//
//            putParcelableArrayListExtra("CART_ITEMS", ArrayList<CheckoutCartItem>(state.cartItems))
//        }
//        context.startActivity(intent)
//    }

    fun proceedToConfirmation(paymentMethod: String) {

        val rawAddress = state.shippingAddress?.address
        val shortAddress = rawAddress?.split(",")?.take(2)?.joinToString(",")?.trim()

        val confirmationItems =
            state.cartItems.map { item ->
                CheckoutItem(
                    name = item.title,
                    price = item.price
                )
            }
        val formattedTotal = String.format(java.util.Locale.getDefault(), "%.2f", state.grandTotal)
        val intent =
            Intent(context, ConfirmationActivity::class.java).apply {

                putExtra(
                    "PAYMENT_OPTION",
                    paymentMethod
                )

                putExtra(
                    "DELIVERY_ADDRESS",
                    shortAddress
                )

                putExtra(
                    "DELIVERY_CHARGE",
                    state.shippingCharge.toString()
                )

                putExtra(
                    "TOTAL_AMOUNT",
                    formattedTotal
                )

                putParcelableArrayListExtra(
                    "CART_ITEMS",
                    ArrayList(confirmationItems)
                )
            }

        context.startActivity(intent)
    }


    Scaffold(
        containerColor = Background,
        topBar = {
            CheckoutTopBar(
                onBackClick = onBackClick
            )
        },

        bottomBar = {
            CheckoutBottomBar(
                state = state,
                onPlaceOrder = {
                    if (state.shippingAddress == null) {
                        showAddressSheet = true
                    } else {
                        onPlaceOrder()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(
                10.dp
            )
        ) {

            item {
                DeliveryAddressCard(
                    address = state.shippingAddress,
                    onClick = onAddressClick
                )
            }
            item {
                Text(
                    text = "Order Summary (${state.cartItems.size})",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }
            items(
                items = state.cartItems,
                key = {
                    it.productId
                }
            ) { item ->

                CheckoutProductCard(
                    item = item
                )
            }
            item {
                PromoCodeButton(
                    appliedCode = state.promoCode,
                    onClick = {
                        showPromoSheet = true
                    },
                    onRemoveClick = onRemovePromoCode
                )
            }
            item {

                Text(
                    text = "Choose Your Payment Option",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.W300
                )
            }
            item {
                PaymentMethodCard(
                    selectedMethod = state.selectedPaymentMethod,
                    onPaymentMethodSelected = { method ->

                        if (state.shippingAddress == null) {
                            showAddressSheet = true
                        } else {
                            onPaymentMethodSelected(method)
                            val paymentOptionName = if (method == PaymentMethod.CASH_ON_DELIVERY) {
                                "Cash on Delivery"
                            } else {
                                "Pay with eSewa"
                            }
                            proceedToConfirmation(paymentOptionName)
                        }
                    }
                )
            }
            item {
                SafePaymentText()
            }
        }
    }


    if (showPromoSheet) {
        PromoCodeSheet(
            onDismiss = {
                showPromoSheet = false
            },
            onApply = { code ->
                onPromoCodeClick(code)
                showPromoSheet = false
            }
        )
    }
    if (showAddressSheet) {
        AddressRequiredSheet(
            onSetAddress = {
                showAddressSheet = false
                onAddressClick()
            },
            onDismiss = {
                showAddressSheet = false
            }
        )
    }
}

fun putParcelableArrayListExtra(
    name: String,
    value: ArrayList<CheckoutCartItem>
) {
}

@Composable
private fun CheckoutTopBar(
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector =
                        Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color(0xFF28293D)
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Checkout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(
                modifier = Modifier.width(48.dp)
            )
        }
    }
}

@Composable
private fun DeliveryAddressCard(
    address: ShippingAddress?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        Color(0xFFE8F7E5),
                        CircleShape
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    imageVector =
                        Icons.Default.AddLocation,
                    contentDescription = null,
                    tint = Green,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(
                modifier = Modifier.width(10.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            )
            {
                Text(
                    text = "Delivery Address",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(9.dp))
                Text(
                    text = address?.let {
                        "${it.address}, ${it.city}"
                    } ?: "Add Shipping Address",

                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .size(25.dp)
                    .background(
                        Green,
                        RoundedCornerShape(6.dp)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    imageVector =
                        if (address == null)
                            Icons.Default.Add
                        else
                            Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}


@Composable
private fun CheckoutProductCard(
    item: CheckoutCartItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            AsyncImage(
                model = item.imageUrl,
                contentDescription =
                    item.title,
                modifier = Modifier
                    .size(65.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(Color(0xFFB6B2A5)),
                contentScale =
                    ContentScale.Fit
            )
            Spacer(
                modifier = Modifier.width(10.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow =
                        TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "QTY: ${item.quantity}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text =
                        "Rs. ${
                            "%.2f".format(
                                item.price *
                                        item.quantity
                            )
                        }",
                    fontSize = 15.sp,
                    color = Green,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PromoCodeButton(
    appliedCode: String,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    if (appliedCode.isEmpty()) {
        OutlinedButton(
            onClick = onClick,
            border = androidx.compose.foundation.BorderStroke(1.dp, Green),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "HAVE A PROMOCODE?",
                color = Green,
                fontFamily = FontFamily(
                    Font(R.font.source_sans_pro)
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    } else {

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF85D973),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF85D973))
        ) {
            Row(
                modifier = Modifier
                    .clickable { onClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$appliedCode APPLIED",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Promocode",
                    tint = Color(0xFFB2E0A8),
                    modifier = Modifier
                        .size(14.dp)
                        .clickable { onRemoveClick() }
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    selectedMethod: PaymentMethod,
    onPaymentMethodSelected:
        (PaymentMethod) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {
            PaymentRow(
                title = "Cash on Delivery",
                selected =
                    selectedMethod ==
                            PaymentMethod.CASH_ON_DELIVERY,
                onClick = {
                    onPaymentMethodSelected(
                        PaymentMethod.CASH_ON_DELIVERY
                    )
                },
                iconId = R.drawable.group
            )
            HorizontalDivider(thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))
            PaymentRow(
                title = "Pay with eSewa",
                selected =
                    selectedMethod ==
                            PaymentMethod.ESEWA,
                onClick = {
                    onPaymentMethodSelected(
                        PaymentMethod.ESEWA
                    )
                },
                iconId = R.drawable.e
            )
        }
    }
}

@Composable
private fun PaymentRow(
    title: String,
    iconId: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 12.dp,
                vertical = 15.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = title,
            modifier = Modifier.size(32.dp)
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
//        Icon(
//            imageVector =
//                Icons.Default.ArrowForward,
//            contentDescription = null,
//            tint = Color.Gray
//        )
        Image(
            painter = painterResource(R.drawable.arrow_right_24),
            contentDescription = null
        )
    }
}


@Composable
private fun SafePaymentText() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.Center,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                Icons.Default.Info,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(15.dp)
        )
        Spacer(
            modifier = Modifier.width(5.dp)
        )
        Column {

            Text(
                text =
                    "SAFE AND SECURE PAYMENTS.",
                fontSize = 10.sp,
                color = Color.Gray
            )
            Text(
                text =
                    "100% AUTHENTIC PRODUCTS.",
                fontSize = 10.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun CheckoutBottomBar(
    state: CheckoutState,
    onPlaceOrder: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth(),

//            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CostRow(
                            label = "Sub Total (${state.cartItems.sumOf { it.quantity }} Items)",
                            amount = state.subtotal
                        )
                        CostRow(
                            label = "Tax",
                            amount = state.tax
                        )
                        CostRow(
                            label = "Shipping Charge",
                            amount = state.shippingCharge
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                            color = Color(0xFFF0F0F0)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Grand Total ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF28293D)
                            )
                            Text(
                                text = "*included TAX",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Text(
                        text = "Rs. ${"%.2f".format(state.grandTotal)}",
                        fontSize = 18.sp,
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

            }
        }
        Surface(
            onClick = { isExpanded = !isExpanded },
            shape = CircleShape,
            color = Green,
            shadowElevation = 4.dp,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowDown
                    else
                        Icons.Default.KeyboardArrowUp,
                    contentDescription = "Toggle order breakdown",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CostRow(
    label: String,
    amount: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color.Gray
        )
        Text(
            text = "Rs. ${"%.2f".format(amount)}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF28293D)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromoCodeSheet(
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var promoCode by rememberSaveable {
        mutableStateOf("")
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Promocode",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Text(
                text = "Enter promocode",
                fontSize = 9.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            OutlinedTextField(
                value = promoCode,
                onValueChange = {
                    promoCode = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Promocode",
                        fontSize = 11.sp
                    )
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier =
                        Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF555770)
                    )
                ) {
                    Text(
                        "CANCEL",
                    )
                }
                Button(
                    onClick = {
                        onApply(promoCode)
                    },
                    modifier =
                        Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2ABB00)
                    )
                ) {
                    Text(
                        "APPLY",
                        modifier = Modifier.background(Color(0xFF2ABB00))
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressRequiredSheet(
    onSetAddress: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        Color(0xFFE8F7E5),
                        CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.AddLocation,
                    contentDescription = null,
                    tint = Green,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "No address added yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF28293D),
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Please add your shipping address before placing your order.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Button(
                onClick = onSetAddress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

                shape = RoundedCornerShape(10.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Green
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AddLocation,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "SET ADDRESS",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "CANCEL",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
private fun ValidationAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector = Icons.Default.AddLocation
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFE8F7E5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Green,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF28293D),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    Text(
                        text = confirmButtonText.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CANCEL",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}




