package com.aneeq.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.model.History
import com.aneeq.foodrunner.model.Menu
import com.aneeq.foodrunner.util.ConnectionManager
import org.json.JSONException

class HistoryRecyclerAdapter(val context: Context, val historyList: ArrayList<History>) :
    RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtHotelName: TextView = view.findViewById(R.id.txtHotelName)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
        val recycleInside: RecyclerView = view.findViewById(R.id.recycleInside)
        val txtTotal: TextView = view.findViewById(R.id.txtTotal)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_history_single_row, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.txtHotelName.text = history.naam_e_hotel
        holder.txtTotal.text="Total Cost: Rs ${history.pooraCost}"
        var formatDate=history.waqt
        formatDate=formatDate.replace("-","/")
        formatDate=formatDate.substring(0,6)+"20"+formatDate.substring(6,8)
        holder.txtTime.text = formatDate
        val orderList = arrayListOf<Menu>()
        val sharedPreferences =
            context.getSharedPreferences("Food Preferences", Context.MODE_PRIVATE)
        val urId = sharedPreferences.getString("user_id", "URId")
        val queue = Volley.newRequestQueue(context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/"

        if (ConnectionManager().checkConnection(context)) {

//creating a json object
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url + urId, null, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {

                            //parsing the JSOn objects key/value pair

                            val resArray = data.getJSONArray("data")
                            val foodObject = resArray.getJSONObject(position)
                            orderList.clear()
                            val foodArray = foodObject.getJSONArray("food_items")
                            for (j in 0 until foodArray.length()) {
                                val eachFood = foodArray.getJSONObject(j)
                                val itemtObject = Menu(
                                    eachFood.getString("food_item_id").toInt(),
                                    eachFood.getString("name"),
                                    eachFood.getString("cost").toInt(),
                                    0
                                )
                                orderList.add(itemtObject)

                            }  //server sends the link for image,coz image will load slowly

                            val cartRecyclerAdapter =
                                CartRecyclerAdapter(context, orderList)

                            //attach dashboard fragment to adapter(bridge between data and view)

                            holder.recycleInside.adapter = cartRecyclerAdapter
                            val mLayoutManager = LinearLayoutManager(context)
                            holder.recycleInside.layoutManager = mLayoutManager


                        } else {
                            Toast.makeText(
                                context,
                                "Some Error Occurred",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            "Some UnExpected Error Occurred",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }


                },
                Response.ErrorListener {
                    //println("Error is $it")
                    Toast.makeText(
                        context,
                        "Volley Error Occurred",
                        Toast.LENGTH_LONG
                    )
                        .show()

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
            Toast.makeText(
                context,
                "internet issue",
                Toast.LENGTH_LONG
            )
                .show()


        }


    }
}