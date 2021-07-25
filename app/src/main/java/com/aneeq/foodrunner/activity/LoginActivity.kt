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
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignUp: TextView
    lateinit var btnLogin: Button
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var rlLoading: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Login Page"


        sharedPreferences = getSharedPreferences("Food Preferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_login)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        setContentView(R.layout.activity_login)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtSignUp = findViewById(R.id.txtSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        rlLoading = findViewById(R.id.rlLoading)
        rlLoading.visibility = View.GONE
        progressBar = findViewById(R.id.progressBar)
        setUpToolbar()

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        txtSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnLogin.setOnClickListener {
            if (ConnectionManager().checkConnection(this@LoginActivity)) {
                setLogin()
            } else {
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Failure")
                dialog.setMessage("Internet Connection NOT Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }
        }


    }

    fun savePreferences() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("pass", etPassword.toString()).apply()
    }

    private fun setLogin() {

        val queue = Volley.newRequestQueue(this@LoginActivity)
        val url = "http://13.235.250.119/v2/login/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", etMobileNumber.text)
        jsonParams.put("password", etPassword.text)

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



                                sharedPreferences.edit()
                                    .putString("u_address", ResJsonObject.getString("address"))
                                    .apply()


                                val intent =
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                savePreferences()
                                startActivity(intent)
                                finish()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Successfully Logged In",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                            } else {
                                rlLoading.visibility = View.VISIBLE
                                progressBar.visibility = View.INVISIBLE
                                btnLogin.visibility = View.INVISIBLE
                                val errorMessage = data.getString("errorMessage")
                                print("success is $success")
                                Toast.makeText(
                                    this@LoginActivity,
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
                            this@LoginActivity,
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

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login Page"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

}


