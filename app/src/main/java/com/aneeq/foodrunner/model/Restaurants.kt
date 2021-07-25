package com.aneeq.foodrunner.model


data class Restaurants(
    val resId: Int,
    val restaurantName: String,
    val restaurantRating: String,
    val restaurantPrice: Int,
    val restaurantImage: String


){
    fun getName(): String? {
        return restaurantName
    }
}
