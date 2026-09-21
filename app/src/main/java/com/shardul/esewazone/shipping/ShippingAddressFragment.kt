package com.shardul.esewazone.shipping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.shardul.esewazone.R
import com.shardul.esewazone.ui.location.LocationPickerFragment
import java.util.UUID

private val BrandGreen = Color(0xFF20C300)
private val ScreenBackground = Color(0xFFF7F7F9)
private val FieldBackground = Color(0xFFF6F6F7)
private val SwitchPill = Color(0xFFFEAF9E6)
private val FieldLabel = Color(0xFF686B80)
private val Placeholder = Color(0xFFC8CAD8)
private val SourceSans = FontFamily(Font(R.font.source_sans_pro))

class ShippingAddressFragment : Fragment() {
    private lateinit var repository: ShippingAddressRepository
    private var pickedAddress by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = ShippingAddressRepository(
            requireContext(),
            FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        )

        parentFragmentManager.setFragmentResultListener(
            LocationPickerFragment.REQUEST_KEY,
            this
        ) { _, result ->
            pickedAddress = result.getString(LocationPickerFragment.RESULT_ADDRESS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = androidx.compose.ui.platform.ComposeView(requireContext()).apply {
        setViewCompositionStrategy(
            androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle(fontFamily = SourceSans)
                ) {
                    ShippingAddressContent(
                        repository = repository,
                        pickedAddress = pickedAddress,
                        onBack = { findNavController().popBackStack() },
                        onPickLocation = {
                            findNavController().navigate(
                                R.id.action_shippingAddressFragment_to_locationPickerFragment
                            )
                        },
                        onSave = {
                            repository.save(it)
                            pickedAddress = null
                        },
                        onDelete = repository::delete
                    )
                }
            }
        }
    }
}

@Composable
private fun ShippingAddressContent(
    repository: ShippingAddressRepository,
    pickedAddress: String?,
    onBack: () -> Unit,
    onPickLocation: () -> Unit,
    onSave: (SavedShippingAddress) -> Unit,
    onDelete: (String) -> Unit
) {
    val addresses by repository.items.collectAsStateWithLifecycle()
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var isAdding by rememberSaveable { mutableStateOf(false) }
    val editingAddress = addresses.firstOrNull { it.id == editingId }

    if (isAdding || editingAddress != null) {
        AddressEditor(
            existingAddress = editingAddress,
            pickedAddress = pickedAddress,
            onClose = {
                editingId = null
                isAdding = false
            },
            onPickLocation = onPickLocation,
            onSave = {
                onSave(it)
                editingId = null
                isAdding = false
            },
            onDelete = {
                onDelete(it)
                editingId = null
            }
        )
    } else {
        AddressList(
            addresses = addresses,
            onBack = onBack,
            onAdd = { isAdding = true },
            onEdit = { editingId = it.id }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressList(
    addresses: List<SavedShippingAddress>,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (SavedShippingAddress) -> Unit
) {
    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Shipping Address", fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                windowInsets = WindowInsets(0.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (addresses.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onAdd,
                    icon = { Icon(Icons.Default.Add, null, Modifier.size(18.dp)) },
                    text = { Text("ADD ADDRESS", fontWeight = FontWeight.SemiBold) },
                    containerColor = BrandGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        if (addresses.isEmpty()) {
            EmptyAddressState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(top = 46.dp),
                onAdd = onAdd
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(addresses, key = { it.id }) { address ->
                    AddressCard(address = address, onClick = { onEdit(address) })
                }
            }
        }
    }
}

@Composable
private fun EmptyAddressState(
    modifier: Modifier = Modifier,
    onAdd: () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier.padding(horizontal = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(74.dp),
                tint = BrandGreen
            )
            Spacer(Modifier.height(10.dp))
            Text("No address added yet!", fontWeight = FontWeight.SemiBold)
            Text(
                "You have not added any shipping address yet.",
                color = Color(0xFFACB0BE)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text("ADD ADDRESS NOW")
            }
        }
    }
}

@Composable
private fun AddressCard(
    address: SavedShippingAddress,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = if (address.defaultShipping) BorderStroke(1.dp, BrandGreen) else null,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (address.defaultShipping) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.LocationOn
                },
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = BrandGreen
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(address.name, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        address.label.uppercase(),
                        color = BrandGreen,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    addressTitle(address.address),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    "Nearby: ${addressDetail(address.address)}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Icon(Icons.Default.MoreVert, "Edit", tint = Color.DarkGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressEditor(
    existingAddress: SavedShippingAddress?,
    pickedAddress: String?,
    onClose: () -> Unit,
    onPickLocation: () -> Unit,
    onSave: (SavedShippingAddress) -> Unit,
    onDelete: (String) -> Unit
) {
    var name by rememberSaveable(existingAddress?.id) {
        mutableStateOf(existingAddress?.name.orEmpty())
    }
    var phone by rememberSaveable(existingAddress?.id) {
        mutableStateOf(existingAddress?.phone.orEmpty())
    }
    var address by rememberSaveable(existingAddress?.id) {
        mutableStateOf(existingAddress?.address.orEmpty())
    }
    var label by rememberSaveable(existingAddress?.id) {
        mutableStateOf(existingAddress?.label ?: "Home")
    }
    var defaultShipping by rememberSaveable(existingAddress?.id) {
        mutableStateOf(existingAddress?.defaultShipping ?: true)
    }
    var defaultBilling by rememberSaveable(existingAddress?.id) {
        mutableStateOf(existingAddress?.defaultBilling ?: false)
    }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(pickedAddress) {
        if (pickedAddress != null) address = pickedAddress
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (existingAddress == null) "Add your new address" else "Edit your address",
                        fontWeight = FontWeight.Normal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                windowInsets = WindowInsets(0.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
                        onSave(
                            SavedShippingAddress(
                                id = existingAddress?.id ?: UUID.randomUUID().toString(),
                                name = name,
                                phone = phone,
                                address = address,
                                label = label,
                                defaultShipping = defaultShipping,
                                defaultBilling = defaultBilling
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(14.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text("SAVE", fontWeight = FontWeight.SemiBold)
            }
        }
    ) { padding ->
        Card(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxWidth(),

            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text("Details for shipping", fontWeight = FontWeight.SemiBold)

                AddressTextField(
                    label = "Full Name",
                    value = name,
                    placeholder = "Enter Full Name",
                    onValueChange = { name = it }
                )
                AddressTextField(
                    label = "Mobile Number",
                    value = phone,
                    placeholder = "Enter mobile No.",
                    onValueChange = { phone = it }
                )
                AddressTextField(
                    label = "Address",
                    value = address,
                    placeholder = "Enter Address",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = onPickLocation) {
                            Icon(Icons.Default.LocationOn, "Choose on map")
                        }
                    }
                )

                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("Select a label", color = FieldLabel, style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Home", "Office", "Other").forEach { value ->
                            FilterChip(
                                selected = label == value,
                                onClick = { label = value },
                                label = { Text(value) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFEEF0F4))
                ToggleRow(
                    label = "Make this as a default shipping address",
                    checked = defaultShipping,
                    onCheckedChange = { defaultShipping = it }
                )
                ToggleRow(
                    label = "Make this as a default billing address",
                    checked = defaultBilling,
                    onCheckedChange = { defaultBilling = it }
                )

                if (existingAddress != null) {
                    TextButton(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFE75A51)
                        )
                    ) {
                        Icon(Icons.Default.Delete, null)
                        Spacer(Modifier.width(6.dp))
                        Text("DELETE ADDRESS")
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Address") },
            text = { Text("Are you sure you want to delete this address?") },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("CANCEL")
                }
            },
            confirmButton = {
                Button(
                    onClick = { onDelete(existingAddress!!.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("DELETE")
                }
            }
        )
    }
}

@Composable
private fun AddressTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            text = label,
            color = FieldLabel,
            style = MaterialTheme.typography.labelMedium
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = FieldBackground,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(66.dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = Placeholder)
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = readOnly,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color(0xFF2D3142)
                        )
                    )
                }
                trailingIcon?.invoke()
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            color = FieldLabel,
            style = MaterialTheme.typography.labelMedium
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(
            checkedTrackColor = BrandGreen,
            uncheckedTrackColor = Placeholder,
            checkedThumbColor = SwitchPill,
            uncheckedThumbColor = ScreenBackground
        ))
    }
}

private fun addressTitle(address: String): String = when {
    address.contains("shantinagar", ignoreCase = true) -> "Shantinagar"
    address.contains("pulchowk", ignoreCase = true) -> "Pulchowk, Lalitpur"
    address.contains("lalitpur", ignoreCase = true) -> "Lalitpur"
    address.contains("kathmandu", ignoreCase = true) -> "Kathmandu Ward 34"
    else -> address.substringAfter(',', address).substringBefore(',').trim()
}
private fun addressDetail(address: String): String {
    val parts = address
        .split(',')
        .map(String::trim)
        .filter { it.isNotBlank() && !it.matches(Regex("[A-Z0-9+]{5,}")) }

    return parts.take(2).joinToString(", ").ifBlank { "Selected map location" }
}
