package com.aneeq.foodrunner.adapter


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.activity.MenuActivity
import com.aneeq.foodrunner.database.RestaurantDatabase
import com.aneeq.foodrunner.database.RestaurantEntity
import com.aneeq.foodrunner.model.Restaurants
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


class HomeRecyclerAdapter(val context: Context, var itemList: ArrayList<Restaurants>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(), Filterable {

    var searchFilterList = ArrayList<Restaurants>()

    init {
        searchFilterList = itemList
    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResName: TextView = view.findViewById(R.id.txtResName)
        val txtResPrice: TextView = view.findViewById(R.id.txtResPrice)
        val txtResRating: TextView = view.findViewById(R.id.txtResRating)
        val imgResImage: ImageView = view.findViewById(R.id.imgResImage)
        val iconFav: ImageButton = view.findViewById(R.id.iconFav)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchFilterList.size
        //return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurants = searchFilterList[position]
        holder.txtResName.text = restaurants.getName()
        holder.txtResPrice.text = "₹${restaurants.restaurantPrice}/person"
        holder.txtResRating.text = restaurants.restaurantRating
        Picasso.get().load(restaurants.restaurantImage).error(R.drawable.logofood)
            .into(holder.imgResImage)

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurants.resId.toString())) {
            holder.iconFav.setImageResource(R.drawable.ic_heart)
        } else {
            holder.iconFav.setImageResource(R.drawable.ic_favouriterestaurants)
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        holder.iconFav.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurants.resId,
                restaurants.restaurantName,
                restaurants.restaurantRating,
                restaurants.restaurantPrice.toString(),
                restaurants.restaurantImage
            )
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val result = DBAsyncTask(context, restaurantEntity, 2).execute().get()
                //val result = async.get()
                if (result) {
                    Toast.makeText(context, "Restaurant Added To Favourites", Toast.LENGTH_LONG)
                        .show()
                    holder.iconFav.setImageResource(R.drawable.ic_heart)
                }
            } else {
                val result = DBAsyncTask(context, restaurantEntity, 3).execute().get()
                // val result = async.get()
                if (result) {
                    Toast.makeText(context, "Restaurant Removed from Favourites", Toast.LENGTH_LONG)
                        .show()
                    holder.iconFav.setImageResource(R.drawable.ic_favouriterestaurants)

                }
            }
        }

        holder.llContent.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("id", restaurants.resId)
            intent.putExtra("name", holder.txtResName.text.toString())
            context.startActivity(intent)

        })
    }
///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                if (charString.isEmpty()) {
                    searchFilterList = itemList
                } else {
                    val filteredList: ArrayList<Restaurants> = ArrayList()

                    for (row in itemList) {
                        if (row.getName()?.toLowerCase(Locale.getDefault())!!.contains(charString)
                        ) {
                            filteredList.add(row)
                        }
                    }

                    searchFilterList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = searchFilterList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                searchFilterList = p1?.values as ArrayList<Restaurants>
                notifyDataSetChanged()
            }

        }
    }
}

//////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {
    /* mode1=check the db if the book is fav or not
    mode2=save the book as fav
    mode3=remove the  book from fav
     */
    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

    override fun doInBackground(vararg p0: Void?): Boolean {
        when (mode) {
            1 -> {
                val res: RestaurantEntity? =
                    db.restaurantDao().getRestaurantById(restaurantEntity.resId.toString())
                db.close()
                return res != null
            }
            2 -> {
                db.restaurantDao().insertRes(restaurantEntity)
                db.close()
                return true
            }
            3 -> {
                db.restaurantDao().deleteRes(restaurantEntity)
                db.close()
                return true
            }
        }

        return false
    }


}

////////////////////////////////////////////////////////////////////////////
class GetAllFavAsyncTask(context: Context) : AsyncTask<Void, Void, List<String>>() {

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
    override fun doInBackground(vararg params: Void?): List<String> {

        val list = db.restaurantDao().getAllRestaurants()
        val listOfIds = arrayListOf<String>()
        for (i in list) {
            listOfIds.add(i.resId.toString())
        }
        return listOfIds
    }
}
//////////////////////////////////////////////////////////////////////////////////////////



