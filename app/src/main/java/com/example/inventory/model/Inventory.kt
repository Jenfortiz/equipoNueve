package com.example.inventory.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "inventory_items")
data class Inventory(
    @PrimaryKey
    var id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
) : Parcelable
