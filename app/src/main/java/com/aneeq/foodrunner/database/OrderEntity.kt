package com.aneeq.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order")
data class OrderEntity(
    @PrimaryKey val ordId: Int,
    @ColumnInfo(name = "order_name") val ordName: String,
    @ColumnInfo(name = "order_price") val ordPrice: String,
    @ColumnInfo(name = "selected_hotel") val ordHotel: Int
)
