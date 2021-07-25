package com.aneeq.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.fragment.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frame: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var collapsingtoolbar: CollapsingToolbarLayout
    lateinit var drawName: TextView
    lateinit var drawPhone: TextView
    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("Food Preferences", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        sharedPreferences.getString("pass", "Food")
        sharedPreferences.getString("user_id", "register")
        val dName = sharedPreferences.getString("u_name", "Your Name")
        val dPhone = sharedPreferences.getString("u_mobile_number", "Mobile Number")

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frame = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        val headerView: View = navigationView.getHeaderView(0)
        drawName = headerView.findViewById(R.id.drawName)
        drawPhone = headerView.findViewById(R.id.drawPhone)
        drawName.text = dName
        drawPhone.text = "+91-$dPhone"
        collapsingtoolbar = findViewById(R.id.collapsingtoolbar)

        setUpToolbar()
        openHome()

        val passOn = intent.getStringExtra("regmob")
        val intent = Intent(this@MainActivity, ResetActivity::class.java)
        intent.putExtra("finalregmob", passOn)

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it        //highlight menu


            when (it.itemId) {
                R.id.itHome -> {
                    openHome()
                    drawerLayout.closeDrawers()

                }
                R.id.itFavouritesRestaurants -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouritesFragment()
                        )

                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourites"
                }
                R.id.itFAQs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FAQFragment()
                        )

                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "FAQs"
                }
                R.id.itProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        )

                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Profile"
                }
                R.id.itOrderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            HistoryFragment()
                        )

                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Order History"
                }
                R.id.itLogOut -> {

                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to Logout?")
                    dialog.setPositiveButton("OK") { _, _ ->
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("Cancel") { _, _ ->
                        dialog.show().cancel()
                    }
                    dialog.create()
                    dialog.show()
                }

            }

            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, HomeFragment())
            .commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.itHome)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is HomeFragment -> openHome()
            else -> {
                super.onBackPressed()
            }
        }
        drawerLayout.closeDrawers()
    }
}

