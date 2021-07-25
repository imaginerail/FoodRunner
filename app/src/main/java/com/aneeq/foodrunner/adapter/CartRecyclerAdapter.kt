package com.aneeq.foodrunner.adapter

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.model.Menu

class CartRecyclerAdapter(val context: Context, val menuList: ArrayList<Menu>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val menu = menuList[position]

        holder.txtItemName.text = menu.name
        holder.txtItemPrice.text = "Rs. ${menu.cost_for_one.toString()}"

    }
}






