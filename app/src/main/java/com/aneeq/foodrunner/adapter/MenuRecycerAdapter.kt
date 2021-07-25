package com.aneeq.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.activity.CartActivity
import com.aneeq.foodrunner.database.OrderDatabase
import com.aneeq.foodrunner.database.OrderEntity
import com.aneeq.foodrunner.model.Menu
import kotlin.collections.ArrayList

class MenuRecyclerAdapter(
    val context: Context,
    val menuList: ArrayList<Menu>,
    val btnProceedToCart: Button
) : RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {
    var itemSelectedCount: Int = 0


    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItemSno: TextView = view.findViewById(R.id.txtItemSno)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val btnItemAdd: Button = view.findViewById(R.id.btnItemAdd)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]

        holder.txtItemSno.text = "${position + 1}"
        holder.txtItemName.text = menu.name
        holder.txtItemPrice.text = "Rs. ${menu.cost_for_one.toString()}"

        val listOfItems = GetAllItemsAsyncTask2(context).execute().get()


        if (listOfItems.isNotEmpty() && listOfItems.contains(menu.id.toString())) {
            holder.btnItemAdd.text = "Remove"
            val favColor = ContextCompat.getColor(context, R.color.colorNoCart)
            holder.btnItemAdd.setBackgroundColor(favColor)


        } else {
            holder.btnItemAdd.text = "Add"
            val favColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            holder.btnItemAdd.setBackgroundColor(favColor)
        }


/////////////////////////////////////////////////////////////////////////////
        holder.btnItemAdd.setOnClickListener {
            val orderEntity = OrderEntity(
                menu.id,
                menu.name,
                menu.cost_for_one.toString(),
                menu.hotelId
            )
            if (!DBAsyncTask2(context, orderEntity, 1).execute().get()) {
                val result = DBAsyncTask2(context, orderEntity, 2).execute().get()
                //val result = async.get()
                if (result) {
                    Toast.makeText(context, "Order Added to Cart", Toast.LENGTH_LONG).show()
                    holder.btnItemAdd.text = "Remove"
                    val favColor = ContextCompat.getColor(context, R.color.colorNoCart)
                    holder.btnItemAdd.setBackgroundColor(favColor)
                    itemSelectedCount++

                }
            } else {
                val result = DBAsyncTask2(context, orderEntity, 3).execute().get()
                // val result = async.get()
                if (result) {
                    Toast.makeText(context, "Order Removed from Cart", Toast.LENGTH_LONG).show()
                    holder.btnItemAdd.text = "Add"
                    val favColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                    holder.btnItemAdd.setBackgroundColor(favColor)
                    itemSelectedCount--
                }
            }
            if (itemSelectedCount > 0) {
                btnProceedToCart.visibility = View.VISIBLE

            } else {
                btnProceedToCart.visibility = View.GONE
            }

        }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        btnProceedToCart.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)

        })
    }


    fun getSelectedItemsCount(): Int {

        return itemSelectedCount

    }
}

////////////////////////////////////////////////////////////////////
class DBAsyncTask2(val context: Context, val orderEntity: OrderEntity, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {
    /* mode1=check the db if the book is fav or not
    mode2=save the book as fav
    mode3=remove the  book from fav
     */
    val db = Room.databaseBuilder(context, OrderDatabase::class.java, "ord-db").build()

    override fun doInBackground(vararg p0: Void?): Boolean {
        when (mode) {
            1 -> {
                val ord: OrderEntity? = db.orderDao().getOrderById(orderEntity.ordId.toString())
                db.close()
                return ord != null
            }
            2 -> {
                db.orderDao().insertOrd(orderEntity)
                db.close()
                return true
            }
            3 -> {
                db.orderDao().deleteOrd(orderEntity)
                db.close()
                return true
            }
        }

        return false
    }

}

//////////////////////////////////////////////////////////////////////////////////
class GetAllItemsAsyncTask2(val context: Context) : AsyncTask<Void, Void, ArrayList<String>>() {


    override fun doInBackground(vararg params: Void?): ArrayList<String> {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "ord-db").build()
        val list = db.orderDao().getAllOrders()
        val listOfIds = arrayListOf<String>()
        for (i in list) {
            listOfIds.add(i.ordId.toString())
        }
        return listOfIds
    }
}
