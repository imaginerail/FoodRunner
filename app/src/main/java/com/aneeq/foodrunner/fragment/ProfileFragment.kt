package com.aneeq.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.aneeq.foodrunner.R
import kotlinx.android.synthetic.main.activity_register.*


class ProfileFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences

    lateinit var imgProfileImage: ImageView
    lateinit var txtProfileName: TextView
    lateinit var txtProfileEmailAddress: TextView
    lateinit var txtProfileDeliveryAddress: TextView
    lateinit var txtProfilePhoneNo: TextView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        var rId: String? = "default"
        imgProfileImage = view.findViewById(R.id.imgProfileImage)
        txtProfileName = view.findViewById(R.id.txtProfileName)
        txtProfileEmailAddress = view.findViewById(R.id.txtProfileEmailAddress)
        txtProfileDeliveryAddress = view.findViewById(R.id.txtProfileDeliveryAddress)
        txtProfilePhoneNo = view.findViewById(R.id.txtProfilePhoneNo)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        if (activity != null) {
            progressLayout.visibility = View.GONE
            sharedPreferences =
                context!!.getSharedPreferences("Food Preferences", Context.MODE_PRIVATE)




           val rId = sharedPreferences.getString("user_id", "ID")

            val rName = sharedPreferences.getString("u_name", "Name")
            val rEmail = sharedPreferences.getString("u_email", "Email")
            val rPhone = sharedPreferences.getString("u_mobile_number", "Mobile number")
            val rAddress = sharedPreferences.getString("u_address", "Address")

            txtProfileName.text = rName
            txtProfilePhoneNo.text = rPhone
            txtProfileEmailAddress.text = rEmail
            txtProfileDeliveryAddress.text = rAddress

        }
        return view
    }

}
