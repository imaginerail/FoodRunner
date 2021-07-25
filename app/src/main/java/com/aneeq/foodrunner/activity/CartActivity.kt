package com.aneeq.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.adapter.CartRecyclerAdapter
import com.aneeq.foodrunner.adapter.MenuRecyclerAdapter
import com.aneeq.foodrunner.database.OrderDatabase
import com.aneeq.foodrunner.database.OrderEntity
import com.aneeq.foodrunner.database.RestaurantDatabase
import com.aneeq.foodrunner.database.RestaurantEntity
import com.aneeq.foodrunner.model.Menu
import com.aneeq.foodrunner.model.Restaurants
import com.aneeq.foodrunner.util.ConnectionManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frame: FrameLayout
    lateinit var collapsingtoolbar: CollapsingToolbarLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recycleCart: RecyclerView
    private lateinit var cartRecyclerAdapter: CartRecyclerAdapter
    private lateinit var menuRecyclerAdapter: MenuRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var txtChoose1: TextView
    lateinit var btnPlaceOrder: Button
    val orderList = arrayListOf<Menu>()
    val res = arrayListOf<Restaurants>()
    var dbOrdList = listOf<OrderEntity>()
    var total: Int = 0
    var idList = arrayListOf<String>()
    var hotelid: Int? = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        sharedPreferences = getSharedPreferences("Food Preferences", Context.MODE_PRIVATE)
        idList = RetrieveOrderIds(this@CartActivity).execute().get()
        drawerLayout = findViewById(R.id.drawerLayout)

        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        progressLayout.visibility = View.GONE
        layoutManager = LinearLayoutManager(this@CartActivity)
        txtChoose1 = findViewById(R.id.txtChoose)
        total = GetOrdersTotalCost(this@CartActivity).execute().get()
        txtChoose1.text = "Ordered items:-"
        hotelid = GetHotelId(this@CartActivity).execute().get()
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frame = findViewById(R.id.frame)
        collapsingtoolbar = findViewById(R.id.collapsingtoolbar)
        setUpToolbar()
        setUpRecycler()

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        btnPlaceOrder.text = "Place Order(Total Rs.$total)"

        btnPlaceOrder.setOnClickListener {
            setUpPlaceOrder()
        }
    }

    private fun setUpRecycler() {
        recycleCart = findViewById<RecyclerView>(R.id.recycleCart)
        dbOrdList = RetrieveOrders(this@CartActivity).execute().get()
        progressLayout.visibility = View.GONE

        for (i in dbOrdList) {
            orderList.add(
                Menu(
                    i.ordId,
                    i.ordName,
                    i.ordPrice.toInt(),
                    i.ordHotel
                )
            )
        }
        cartRecyclerAdapter =
            CartRecyclerAdapter(this@CartActivity, orderList)
        val mLayoutManager = LinearLayoutManager(this@CartActivity)
        recycleCart.layoutManager = mLayoutManager
        recycleCart.itemAnimator = DefaultItemAnimator()
        recycleCart.adapter = cartRecyclerAdapter
        recycleCart.setHasFixedSize(true)

    }

    private fun setUpPlaceOrder() {
        val urId = sharedPreferences.getString("user_id", "URId")
        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        Log.e("hello", "resId=$hotelid\n total=$total\n userid=$urId\n $idList")
        val foodJsonArray = JSONArray()
        for (foodItem in idList) {
            val singleItemObject = JSONObject()
            singleItemObject.put("food_item_id", foodItem)
            foodJsonArray.put(singleItemObject)
        }
        val jsonParams = JSONObject()
        jsonParams.put("user_id", urId)
        jsonParams.put("restaurant_id", hotelid.toString())
        jsonParams.put("total_cost", total.toString())
        jsonParams.put("food", foodJsonArray)
        Log.e("ok", "$foodJsonArray")
        if (ConnectionManager().checkConnection(this@CartActivity)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.POST, url, jsonParams, Response.Listener {

                    progressLayout.visibility = View.GONE
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {


                            //attach dashboard fragment to adapter(bridge between data and view)

                            val intent = Intent(this@CartActivity, OkayActivity::class.java)
                            startActivity(intent)
                            finish()

                            Toast.makeText(
                                this@CartActivity,
                                "Order Successfully Placed",
                                Toast.LENGTH_LONG
                            )
                                .show()

                        } else {
                            Toast.makeText(
                                this@CartActivity,
                                "Some Error Occurred",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@CartActivity,
                            "Some un Error Occurred",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }


                },
                Response.ErrorListener {
                    //println("Error is $it")

                    Toast.makeText(
                        this@CartActivity,
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
            val dialog = AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("Failure")
            dialog.setMessage("Internet Connection NOT Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@CartActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}

class RetrieveOrders(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
    override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "ord-db").build()
        return db.orderDao().getAllOrders()
    }

}

class GetOrdersTotalCost(val context: Context) : AsyncTask<Void, Void, Int>() {
    override fun doInBackground(vararg params: Void?): Int {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "ord-db").build()
        return db.orderDao().getSum()
    }
}

class RetrieveOrderIds(val context: Context) : AsyncTask<Void, Void, ArrayList<String>>() {
    override fun doInBackground(vararg p0: Void?): ArrayList<String> {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "ord-db").build()
        val list = db.orderDao().getAllOrders()
        val listOfIds = arrayListOf<String>()
        for (i in list) {
            listOfIds.add(i.ordId.toString())
        }
        return listOfIds
    }

}

class GetHotelId(val context: Context) : AsyncTask<Void, Void, Int>() {
    override fun doInBackground(vararg p0: Void?): Int {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "ord-db").build()
        return db.orderDao().getHotelById()

    }

}
