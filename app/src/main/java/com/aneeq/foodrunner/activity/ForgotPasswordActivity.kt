package com.aneeq.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etFPMobileNumber: EditText
    lateinit var etFPEmail: EditText
    lateinit var btnFPNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        title = "Forgot Password Page"

        etFPMobileNumber = findViewById(R.id.etFPMobileNumber)
        etFPEmail = findViewById(R.id.etFPEmail)
        btnFPNext = findViewById(R.id.btnFPNext)
        setUpToolbar()
        btnFPNext.setOnClickListener {
            if (ConnectionManager().checkConnection(this@ForgotPasswordActivity)) {
                setForget()
            } else {
                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                dialog.setTitle("Failure")
                dialog.setMessage("Internet Connection NOT Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                }
                dialog.create()
                dialog.show()
            }
        }


    }

    private fun setForget() {
        val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", etFPMobileNumber.text)
        jsonParams.put("email", etFPEmail.text)

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
                            val first_try = data.getBoolean("first_try")
                            if (success) {
                                print("success is $success")
                                val intent =
                                    Intent(this@ForgotPasswordActivity, ResetActivity::class.java)
                                intent.putExtra("phone", etFPMobileNumber.text.toString())
                                startActivity(intent)
                                finish()

                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Reset Now",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                            } else {
                                val errorMessage = data.getString("errorMessage")
                                print("success is $success")
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
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
                            this@ForgotPasswordActivity,
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
        supportActionBar?.title = "Forgot Password Page"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
