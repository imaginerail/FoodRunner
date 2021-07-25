package com.aneeq.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneeq.foodrunner.R
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception

class ResetActivity : AppCompatActivity() {
    lateinit var etOTP: EditText
    lateinit var etNewPass: EditText
    lateinit var etConNewPass: EditText
    lateinit var btnReset: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Enter the received OTP below"
        sharedPreferences = getSharedPreferences("Food Preferences", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_reset)

        etOTP = findViewById(R.id.etOTP)
        etConNewPass = findViewById(R.id.etConNewPass)
        etNewPass = findViewById(R.id.etNewPass)
        btnReset = findViewById(R.id.btnReset)
        setUpToolbar()
        btnReset.setOnClickListener {

            setReset()
        }

    }

    private fun setReset() {
        val getMN = intent.getStringExtra("phone")
        val queue = Volley.newRequestQueue(this@ResetActivity)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", getMN)
        jsonParams.put("otp", etOTP.text)
        jsonParams.put("password", etNewPass.text)

        val jsonRequest =
            object :
                JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonParams,
                    Response.Listener {

                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            val message = data.getString("successMessage")
                            if (success) {
                                print("success is $success")
                                Toast.makeText(
                                    this@ResetActivity,
                                    message + "!",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                val intent =
                                    Intent(
                                        this@ResetActivity,
                                        LoginActivity::class.java
                                    )
                                startActivity(intent)
                                finish()
                                sharedPreferences.edit().clear().apply()
                            } else {
                                val errorMessage = data.getString("errorMessage")
                                print("success is $success")
                                Toast.makeText(
                                    this@ResetActivity,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@ResetActivity,
                            it.message,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "ca571f94be4133"
                    return headers


                }

            }
        queue.add(jsonRequest)


    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Reset Password Page"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}

