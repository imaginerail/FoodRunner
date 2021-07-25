package com.aneeq.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {

    @Insert
    fun insertOrd(orderEntity: OrderEntity)

    @Delete
    fun deleteOrd(orderEntity: OrderEntity)

    @Query("SELECT * FROM `order`")
    fun getAllOrders(): List<OrderEntity>

    @Query("SELECT * FROM `order` WHERE ordId=:ordId ")
    fun getOrderById(ordId: String): OrderEntity

    @Query("SELECT selected_hotel FROM `order`")
    fun getHotelById(): Int

    @Query("SELECT SUM(order_price) FROM `order`")
    fun getSum(): Int

    @Query("DELETE FROM `order`")
    fun deleteAll(): Int
}
