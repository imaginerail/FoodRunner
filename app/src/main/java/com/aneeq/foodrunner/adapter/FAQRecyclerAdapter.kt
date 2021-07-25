package com.aneeq.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.model.FAQitems

class FAQRecyclerAdapter(val context: Context, val faqList: List<FAQitems>) :
    RecyclerView.Adapter<FAQRecyclerAdapter.FAQViewHolder>() {
    class FAQViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llFAQContent)
        val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
        val txtAnswers: TextView = view.findViewById(R.id.txtAnswers)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_faq_single_row, parent, false)
        return FAQRecyclerAdapter.FAQViewHolder(view)

    }

    override fun getItemCount(): Int {
        return faqList.size

    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faQitems = faqList[position]
        holder.txtQuestion.text = faQitems.question
        holder.txtAnswers.text = faQitems.answers
    }
}