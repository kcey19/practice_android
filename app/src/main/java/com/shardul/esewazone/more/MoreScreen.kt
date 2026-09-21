package com.shardul.esewazone.more

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.WindowInsetsRulers
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.annotations.concurrent.Background
import com.shardul.esewazone.R


private val BrandGreen = Color(0xFF20B900)
private val LightGreenPill = Color(0xFFC2F19E)
private val ScreenBackground = Color(0xFFF5F6F8)
private val DarkText = Color(0xFF2D3142)
private val Background = Color(0xFFF7F8FA)
private val SubtitleText = Color(0xFF8A8A8A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    userName:String,
    userEmail:String,
    onBackClick: () -> Unit = {},

    onViewProfileClick: () -> Unit = {},
    onMyProductsClick: () -> Unit = {},
    onShippingAddressClick: () -> Unit = {},
    onMyOrdersClick: () -> Unit = {},
    onMyReturnsClick: () -> Unit = {},
    onMyCancellationsClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onBonusClick: () -> Unit = {},
    onPromoCodeClick: () ->Unit ={},
    onCustomerSupportClick:() -> Unit ={},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onAllLegalClick:() -> Unit = {},
    onFaqClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
                TopAppBar(
                title = {
                    Text(
                        text = "More",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = DarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkText
                        )
                    }
                },
                actions = {
                    Box(modifier= Modifier
                        .padding(12.dp)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF2F3F5))
                        .clickable{},
                        contentAlignment = Alignment.Center
                    ){IconButton(onClick = { /* Menu Overflow */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = DarkText
                        )
                    }}
                },
                windowInsets = WindowInsets(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    UserProfileHeader(
                        name = userName,
                        email = userEmail,
                        onViewProfileClick = onViewProfileClick
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionTitle("My Profile")
                    MenuItemRow(
                        icon = R.drawable.my_products,
                        title = "My Products",
                        badgeCount = 2,
                        onClick = onMyProductsClick
                    )
                    MenuItemRow(
                        icon = R.drawable.shippping_address,
                        title = "Shipping Address",
                        onClick = onShippingAddressClick
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SectionTitle("My Order")
                    MenuItemRow(
                        icon = R.drawable.my_products,
                        title = "My Order",
                        badgeCount = 2,
                        onClick = onMyOrdersClick
                    )
                    MenuItemRow(
                        icon = R.drawable.ic_return_order,
                        title = "My Return",
                        onClick = onMyReturnsClick
                    )
                    MenuItemRow(
                        icon = R.drawable.cancel_order,
                        title = "My Cancellation",
                        onClick = onMyCancellationsClick
                    )

                    SectionDivider()
                    SectionTitle("Reward & Promotions")
                    MenuItemRow(
                        icon = R.drawable.reward_points,
                        title = "Rewards Points",
                        onClick = onRewardsClick
                    )
                    MenuItemRow(
                        icon = R.drawable.ic_promocode,
                        title = "Apply Promocode",
                        onClick = onPromoCodeClick
                    )
                    MenuItemRow(
                        icon = R.drawable.group,
                        title = "Daily Buy Bonus",
                        onClick = onBonusClick
                    )

                    SectionDivider()

                    SectionTitle("Help")
                    SimpleTextRow(title = "About us", onClick = onAboutUsClick)
                    SimpleTextRow(title = "Customer Support", onClick = onCustomerSupportClick)
                    SimpleTextRow(title = "FAQs", onClick = onFaqClick)

                    SectionDivider()

                    SectionTitle("Legal Terms")
                    SimpleTextRow(title = "Terms & Conditions", onClick = onTermsClick)
                    SimpleTextRow(title = "Privacy Policies", onClick = onPrivacyClick)
                    SimpleTextRow(title = "All Legal Policies", onClick = onAllLegalClick)

                    SectionDivider()
                    LogoutMenuItemRow(onClick = onLogOutClick)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "VERSION 1.0.0",
                fontSize = 10.sp,
                color = SubtitleText,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(34.dp))
        }
    }
}

@Composable
private fun UserProfileHeader(
    name: String,
    email: String,
    onViewProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(54.dp)) {
            Image(
                painter = painterResource(id = R.drawable.profile_photo),
                contentDescription = "User Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(BrandGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Verified",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(
                text = email,
                fontSize = 11.sp,
                color = SubtitleText
            )
        }

        Text(
            text = "VIEW PROFILE",
            fontSize = 14.sp,
            color = BrandGreen,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { onViewProfileClick() }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = DarkText,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun MenuItemRow(
    @DrawableRes icon:Int,
    title: String,
    badgeCount: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = title,
            tint = BrandGreen,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            fontSize = 15.sp,
            color = DarkText,
            modifier = Modifier.weight(1f)
        )
        if (badgeCount != null) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(BrandGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SimpleTextRow(
    title: String,
    onClick: () -> Unit
) {
    Text(
        text = title,
        fontSize = 15.sp,
        color = DarkText,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = Color(0xFFF0F0F0)
    )
}

@Composable
private fun LogoutMenuItemRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.ExitToApp,
            contentDescription = "Logout",
            tint = Color(0xFFE53935),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = "Log out",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE53935)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogOutConfirmSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    val logOut by rememberSaveable{
        mutableStateOf("")
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier=Modifier.fillMaxWidth()
        ) {
        }
    }
}