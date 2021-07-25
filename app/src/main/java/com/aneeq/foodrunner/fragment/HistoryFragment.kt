package com.aneeq.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.adapter.HistoryRecyclerAdapter
import com.aneeq.foodrunner.model.History
import com.aneeq.foodrunner.util.ConnectionManager
import org.json.JSONException

class HistoryFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recycleHistory: RecyclerView
    private lateinit var historyRecyclerAdapter: HistoryRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var rlNoHistory: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var txtChoose: TextView
    val historyList = arrayListOf<History>()
    lateinit var view1: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        sharedPreferences = context!!.getSharedPreferences("Food Preferences", Context.MODE_PRIVATE)
        progressLayout = view.findViewById(R.id.progressLayout)
        rlNoHistory = view.findViewById(R.id.rlNoHistory)
        txtChoose = view.findViewById(R.id.txtChoose)
        view1 = view.findViewById(R.id.view1)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.GONE
        rlNoHistory.visibility = View.GONE

            setUpRecycler(view)


        return view
    }

    private fun setUpRecycler(view: View) {
        val urId = sharedPreferences.getString("user_id", "URId")
        recycleHistory = view.findViewById(R.id.recycleHistory) as RecyclerView
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/"

        if (ConnectionManager().checkConnection(activity as Context)) {

//creating a json object
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url + urId, null, Response.Listener {

                    progressLayout.visibility = View.GONE

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {

                            //parsing the JSOn objects key/value pair

                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resJsonObject = resArray.getJSONObject(i)
                                val restaurantObject = History(
                                    resJsonObject.getString("order_id"),
                                    resJsonObject.getString("restaurant_name"),
                                    resJsonObject.getString("order_placed_at"),
                                    resJsonObject.getString("total_cost")
                                )
                                historyList.add(restaurantObject)

                                //server sends the link for image,coz image will load slowly
                                if(historyList.isEmpty()){
                                    rlNoHistory.visibility = View.VISIBLE
                                }

                                //attach dashboard fragment to adapter(bridge between data and view)
                                if (activity != null) {
                                    historyRecyclerAdapter =
                                        HistoryRecyclerAdapter(
                                            activity as Context,
                                            historyList
                                        )
                                    val mLayoutManager = LinearLayoutManager(activity)
                                    recycleHistory.layoutManager = mLayoutManager
                                    recycleHistory.itemAnimator = DefaultItemAnimator()
                                    recycleHistory.adapter = historyRecyclerAdapter
                                    recycleHistory.setHasFixedSize(true)

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
            dialog.setPositiveButton("Open Settings") { text, listener ->
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
}


