package com.aneeq.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etRegMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etRegPassword: EditText
    lateinit var etRegConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var txtArrow: TextView
    lateinit var rlLoading: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("Food Preferences", Context.MODE_PRIVATE)

        setContentView(R.layout.activity_register)
        title = "Registration Page"
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etRegMobileNumber = findViewById(R.id.etRegMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etRegPassword = findViewById(R.id.etRegPassword)
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        txtArrow = findViewById(R.id.txtArrow)
        rlLoading = findViewById(R.id.rlLoading)
        rlLoading.visibility = View.GONE
        progressBar = findViewById(R.id.progressBar)
        btnRegister.setOnClickListener {
            if (ConnectionManager().checkConnection(this@RegisterActivity)) {
                setUpRegister()
            } else {
                val dialog = AlertDialog.Builder(this@RegisterActivity)
                dialog.setTitle("Failure")
                dialog.setMessage("Internet Connection NOT Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@RegisterActivity)
                }
                dialog.create()
                dialog.show()
            }
        }



        txtArrow.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    private fun setUpRegister() {


        val queue = Volley.newRequestQueue(this@RegisterActivity)
        val url = "http://13.235.250.119/v2/register/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("name", etName.text)
        jsonParams.put("mobile_number", etRegMobileNumber.text)
        jsonParams.put("password", etRegPassword.text)
        jsonParams.put("address", etDeliveryAddress.text)
        jsonParams.put("email", etEmail.text)

        val jsonRequest =
            object :
                JsonObjectRequest(Method.POST,
                    url,
                    jsonParams,
                    Response.Listener {

                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                print("success is $success")
                                val ResJsonObject = data.getJSONObject("data")
                                sharedPreferences.edit()
                                    .putString("user_id", ResJsonObject.getString("user_id"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("u_name", ResJsonObject.getString("name"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("u_email", ResJsonObject.getString("email"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString(
                                        "u_mobile_number",
                                        ResJsonObject.getString("mobile_number")
                                    )
                                    .apply()
                                val regmob = ResJsonObject.getString("mobile_number")
                                sharedPreferences.edit()
                                    .putString("u_address", ResJsonObject.getString("address"))
                                    .apply()


                                val intent = Intent(
                                    Intent(
                                        this@RegisterActivity,
                                        MainActivity::class.java
                                    )
                                )
                                startActivity(intent)
                                finish()
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Successfully registered",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                            } else {
                                rlLoading.visibility = View.VISIBLE
                                progressBar.visibility = View.INVISIBLE
                                btnRegister.visibility = View.INVISIBLE
                                val errorMessage = data.getString("errorMessage")
                                print("success is $success")
                                Toast.makeText(
                                    this@RegisterActivity,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        } catch (e: Exception) {
                            rlLoading.visibility = View.VISIBLE
                            progressBar.visibility = View.INVISIBLE
                            e.printStackTrace()

                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@RegisterActivity,
                            it.message,
                            Toast.LENGTH_LONG
                        )
                            .show()
                        rlLoading.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "ca571f94be4133"
                    return headers


                }

            }
        queue.add(jsonRequest)


        ///////////////////////////////////////////////////////////////////


    }


}




