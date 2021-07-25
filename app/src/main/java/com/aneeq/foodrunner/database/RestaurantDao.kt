package com.aneeq.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {


    @Insert
    fun insertRes(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRes(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurant")
    fun getAllRestaurants(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurant WHERE resId=:resId ")
    fun getRestaurantById(resId: String): RestaurantEntity

    @Query("SELECT res_name FROM restaurant")
    fun getRestaurantByName(): String
}
