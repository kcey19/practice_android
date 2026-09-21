package com.shardul.esewazone.shipping

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

data class SavedShippingAddress(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val address: String,
    val label: String = "Home",
    val defaultShipping: Boolean = true,
    val defaultBilling: Boolean = false
)

class ShippingAddressRepository(
    context: Context,
    userId: String
) {
    private val prefs = context.getSharedPreferences(
        "shipping_addresses",
        Context.MODE_PRIVATE
    )
    private val key = "addresses_$userId"
    private val gson = Gson()
    private val type = object : TypeToken<List<SavedShippingAddress>>() {}.type
    private val _items = MutableStateFlow(read())
    val items: StateFlow<List<SavedShippingAddress>> = _items

    fun save(address: SavedShippingAddress) {
        val list = _items.value
            .filterNot { it.id == address.id }
            .toMutableList()
        val normalized = if (
            address.defaultShipping || list.none { it.defaultShipping }
        ) {
            address.copy(defaultShipping = true)
        } else {
            address
        }

        if (normalized.defaultShipping) {
            for (index in list.indices) {
                list[index] = list[index].copy(defaultShipping = false)
            }
        }

        list.add(normalized)
        write(list)
    }

    fun delete(id: String) {
        write(_items.value.filterNot { it.id == id })
    }

    fun default(): SavedShippingAddress? = _items.value.firstOrNull { it.defaultShipping }

    private fun read(): List<SavedShippingAddress> {
        return prefs.getString(key, null)
            ?.let { gson.fromJson(it, type) }
            ?: emptyList()
    }

    private fun write(list: List<SavedShippingAddress>) {
        val normalized = if (
            list.isNotEmpty() && list.none { it.defaultShipping }
        ) {
            list.mapIndexed { index, item ->
                item.copy(defaultShipping = index == 0)
            }
        } else {
            list
        }
        val ordered = normalized.sortedByDescending { it.defaultShipping }

        prefs.edit()
            .putString(key, gson.toJson(ordered))
            .apply()
        _items.value = ordered
    }
}
