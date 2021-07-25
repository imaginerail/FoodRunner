package com.aneeq.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.aneeq.foodrunner.R
import java.lang.Thread.sleep

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        /*  val thread=Thread(){
              kotlin.run {
                  try{
                      sleep(2000)
                  }
                  catch(e:Exception){
                      e.printStackTrace()
                  }
                  finally {
                      val intent= Intent(this@WelcomeActivity,
                          LoginActivity::class.java)
                      startActivity(intent)
                  }
              }
          }
          thread.start()*/
        Handler().postDelayed({
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
        }, 2000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

