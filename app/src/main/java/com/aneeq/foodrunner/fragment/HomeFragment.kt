package com.aneeq.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.adapter.HomeRecyclerAdapter
import com.aneeq.foodrunner.model.Restaurants
import com.aneeq.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


class HomeFragment : Fragment() {
    lateinit var searchEditText: EditText
    lateinit var recycleHome: RecyclerView
    private lateinit var homeRecyclerAdapter: HomeRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var search: SearchView
    lateinit var progressBar: ProgressBar
    val itemList = arrayListOf<Restaurants>()

    var ratingComparator = Comparator<Restaurants> { res1, res2 ->
        if (res1.restaurantRating.compareTo(res2.restaurantRating, ignoreCase = true) == 0) {
            res1.restaurantName.compareTo(res2.restaurantName, ignoreCase = true)
        } //if ratings are same,then sort with name
        else {
            res1.restaurantRating.compareTo(res2.restaurantRating, ignoreCase = true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // show the menu sort
        setHasOptionsMenu(true)

        search = view.findViewById(R.id.search1)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        setUpRecycler(view)
        layoutManager = LinearLayoutManager(activity)
        ////////////////////////////////////////////////////////
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

        //////////////////////////////////////////////////////////
        return view
    }

    //invoking volley and our url
    fun setUpRecycler(view: View) {
        recycleHome = view.findViewById(R.id.recycleHome) as RecyclerView
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnection(activity as Context)) {

//creating a json object
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url, null, Response.Listener {

                    progressLayout.visibility = View.GONE

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {

                            //parsing the JSOn objects key/value pair

                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resJsonObject = resArray.getJSONObject(i)
                                val restaurantObject = Restaurants(
                                    resJsonObject.getString("id").toInt(),
                                    resJsonObject.getString("name"),
                                    resJsonObject.getString("rating"),
                                    resJsonObject.getString("cost_for_one").toInt(),
                                    resJsonObject.getString("image_url")

                                    //server sends the link for image,coz image will load slowly
                                )
                                itemList.add(restaurantObject)


                                //attach dashboard fragment to adapter(bridge between data and view)
                                if (activity != null) {
                                    homeRecyclerAdapter =
                                        HomeRecyclerAdapter(activity as Context, itemList)
                                    val mLayoutManager: LinearLayoutManager =
                                        LinearLayoutManager(activity)
                                    recycleHome.layoutManager = mLayoutManager
                                    recycleHome.itemAnimator = DefaultItemAnimator()
                                    recycleHome.adapter = homeRecyclerAdapter
                                    recycleHome.setHasFixedSize(true)
                                }

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            activity as Context,
                            "Some UnExpected Error Occurred",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }


                },
                Response.ErrorListener {
                    //println("Error is $it")
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }) {
                //headers are required for type and unique token
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "ca571f94be4133"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        } else {
            //creating a dialogue box if there is no internet connection

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure")
            dialog.setMessage("Internet Connection NOT Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort, menu)
        menu.findItem(R.id.action_sort)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sort) {
            Collections.sort(itemList, ratingComparator)
            itemList.reverse()
        }
        homeRecyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}



