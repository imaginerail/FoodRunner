package com.aneeq.foodrunner.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.fragment.HomeFragment

class OkayActivity : AppCompatActivity() {
    lateinit var btnOkay: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_okay)
        btnOkay = findViewById(R.id.btnOkay)
        btnOkay.setOnClickListener {

            val intent = Intent(this@OkayActivity, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
            Toast.makeText(
                this@OkayActivity,
                "Your Delivery is on the way!",
                Toast.LENGTH_LONG
            )
                .show()
            ClearAll(this@OkayActivity).execute()
        }
    }
}
