package com.aneeq.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [OrderEntity::class], version = 3)
abstract class OrderDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}