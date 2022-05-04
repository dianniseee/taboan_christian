package com.example.taboan_capstone.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "customer_cart")
data class CustomerCartModel(
        @PrimaryKey(autoGenerate = true) @NonNull val id: Int,
        val productID: String,
        val productSellerID: String,
        val market_name: String,
        val productName: String,
        val product_Desc: String,
        val product_category: String,
        val price_each: Double,
        val quantity: Double,
        val subtotal: Double
        )


