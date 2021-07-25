package com.aneeq.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.adapter.HomeRecyclerAdapter
import com.aneeq.foodrunner.database.RestaurantDatabase
import com.aneeq.foodrunner.database.RestaurantEntity
import com.aneeq.foodrunner.model.Restaurants


class FavouritesFragment : Fragment() {
    lateinit var searchEditText: EditText
    lateinit var recycleHome: RecyclerView
    lateinit var rlLoading: RelativeLayout
    lateinit var rlNoFavorites: RelativeLayout
    lateinit var rlFavorites: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var homeRecyclerAdapter: HomeRecyclerAdapter
    lateinit var search: SearchView
    var itemList = arrayListOf<Restaurants>()
    var dbResList = listOf<RestaurantEntity>()
    val searchList = arrayListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

//initialization
        rlLoading = view.findViewById(R.id.rlLoading)
        rlNoFavorites = view.findViewById(R.id.rlNoFavorites)
        rlFavorites = view.findViewById(R.id.rlFavorites)
        progressBar = view.findViewById(R.id.progressBar)
        rlLoading.visibility = View.VISIBLE
        recycleHome = view.findViewById(R.id.recycleHome)
        search = view.findViewById(R.id.search1)
        setUpRecycler(view)

        search.queryHint = "Type the name of Restaurant here"
        searchEditText = view.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(context!!, R.color.coloredit))
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                homeRecyclerAdapter.filter.filter(newText)
                return false
            }

        })
        return view
    }

    private fun setUpRecycler(view: View) {
        recycleHome = view.findViewById(R.id.recycleHome)


        dbResList = RetrieveFavourites(activity as Context).execute().get()

        //attach adapter to recycler
        if (dbResList.isEmpty()) {
            rlLoading.visibility = View.GONE
            rlFavorites.visibility = View.GONE
            rlNoFavorites.visibility = View.VISIBLE
        } else {
            rlFavorites.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
            rlNoFavorites.visibility = View.GONE
            for (i in dbResList) {
                itemList.add(
                    Restaurants(
                        i.resId,
                        i.resName,
                        i.resRating,
                        i.resPrice.toInt(),
                        i.resImage
                    )
                )
            }

            homeRecyclerAdapter = HomeRecyclerAdapter(activity as Context, itemList)
            val mLayoutManager = LinearLayoutManager(activity)
            recycleHome.layoutManager = mLayoutManager
            recycleHome.itemAnimator = DefaultItemAnimator()
            recycleHome.adapter = homeRecyclerAdapter
            recycleHome.setHasFixedSize(true)
        }
    }


}
//retrieving the favourites booklist from database using AsyncTaskClass

class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>() {
    override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        return db.restaurantDao().getAllRestaurants()
    }

}
