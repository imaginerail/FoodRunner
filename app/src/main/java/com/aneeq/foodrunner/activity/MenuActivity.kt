package com.aneeq.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.adapter.MenuRecyclerAdapter
import com.aneeq.foodrunner.database.OrderDatabase
import com.aneeq.foodrunner.model.Menu
import com.aneeq.foodrunner.util.ConnectionManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import org.json.JSONException

class MenuActivity() : AppCompatActivity() {
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frame: FrameLayout
    lateinit var recycleMenu: RecyclerView
    lateinit var collapsingtoolbar: CollapsingToolbarLayout
    private lateinit var menuRecyclerAdapter: MenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var txtChoose: TextView
    lateinit var view1: View
    lateinit var btnProceedToCart: Button
    lateinit var drawerLayout: DrawerLayout
    val menuList = arrayListOf<Menu>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        drawerLayout = findViewById(R.id.drawerLayout)
        progressLayout = findViewById(R.id.progressLayout)
        txtChoose = findViewById(R.id.txtChoose)
        view1 = findViewById(R.id.view1)
        frame = findViewById(R.id.frame)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        btnProceedToCart.visibility = View.GONE
        progressBar = findViewById(R.id.progressBar)
        progressLayout.visibility = View.GONE
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        collapsingtoolbar = findViewById(R.id.collapsingtoolbar)

        setUpToolbar()
        setUpRecycler()
    }

    private fun setUpRecycler() {
        val resId = intent.getIntExtra("id", 0)
        recycleMenu = findViewById<RecyclerView>(R.id.recycleMenu)

        val queue = Volley.newRequestQueue(this@MenuActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnection(this@MenuActivity)) {

            //creating a json object
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url + resId, null, Response.Listener {

                    progressLayout.visibility = View.GONE

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {

                            //parsing the JSOn objects key/value pair

                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resJsonObject = resArray.getJSONObject(i)
                                val restaurantObject = Menu(
                                    resJsonObject.getString("id").toInt(),
                                    resJsonObject.getString("name"),
                                    resJsonObject.getString("cost_for_one").toInt(),
                                    resJsonObject.getString("restaurant_id").toInt()

                                    //server sends the link for image,coz image will load slowly
                                )
                                menuList.add(restaurantObject)


                                //attach dashboard fragment to adapter(bridge between data and view)
                                if (this@MenuActivity != null) {
                                    menuRecyclerAdapter =
                                        MenuRecyclerAdapter(
                                            this@MenuActivity,
                                            menuList,
                                            btnProceedToCart
                                        )
                                    val mLayoutManager = LinearLayoutManager(this@MenuActivity)
                                    recycleMenu.layoutManager = mLayoutManager
                                    recycleMenu.itemAnimator = DefaultItemAnimator()
                                    recycleMenu.adapter = menuRecyclerAdapter
                                    recycleMenu.setHasFixedSize(true)
                                }

                            }
                        } else {
                            Toast.makeText(
                                this@MenuActivity,
                                "Some Error Occurred",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@MenuActivity,
                            "Some UnExpected Error Occurred",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }


                },
                Response.ErrorListener {
                    //println("Error is $it")

                    Toast.makeText(
                        this@MenuActivity,
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

            val dialog = AlertDialog.Builder(this@MenuActivity)
            dialog.setTitle("Failure")
            dialog.setMessage("Internet Connection NOT Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@MenuActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    override fun onBackPressed() {
       if(menuRecyclerAdapter.getSelectedItemsCount()>0){
           val dialog = AlertDialog.Builder(this@MenuActivity)
           dialog.setTitle("Confirmation")
           dialog.setMessage("Going back will clear the cart!")
           dialog.setPositiveButton("Continue") { _, _ ->
               ClearAll(this@MenuActivity).execute()
               finish()

           }
           dialog.setNegativeButton("Cancel") { _, _ ->
               dialog.show().cancel()
           }
           dialog.create()
           dialog.show()
       }
        else{
           super.onBackPressed()
       }


    }

    private fun setUpToolbar() {
        val resName = intent.getStringExtra("name")
        setSupportActionBar(toolbar)
        supportActionBar?.title = "$resName"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (resName != null) {
            val intent = Intent(this@MenuActivity, CartActivity::class.java)
            intent.putExtra("k-on", resName)
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            if(menuRecyclerAdapter.getSelectedItemsCount()>0){
                val dialog = AlertDialog.Builder(this@MenuActivity)
                dialog.setTitle("Confirmation")
                dialog.setMessage("Going back will clear the cart!")
                dialog.setPositiveButton("Continue") { _, _ ->
                    ClearAll(this@MenuActivity).execute()
                    finish()

                }
                dialog.setNegativeButton("Cancel") { _, _ ->
                    dialog.show().cancel()
                }
                dialog.create()
                dialog.show()
            }
            else{
                super.onBackPressed()
            }



        }
        return super.onOptionsItemSelected(item)
    }

}

class ClearAll(val context: Context) : AsyncTask<Void, Void, Int>() {
    override fun doInBackground(vararg p0: Void?): Int {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "ord-db").build()
        return db.orderDao().deleteAll()

    }

}