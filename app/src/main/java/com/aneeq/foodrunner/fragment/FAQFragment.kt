package com.aneeq.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aneeq.foodrunner.R
import com.aneeq.foodrunner.adapter.FAQRecyclerAdapter
import com.aneeq.foodrunner.model.FAQitems


class FAQFragment : Fragment() {
    lateinit var recyclerAdapter: FAQRecyclerAdapter
    lateinit var recyclefaq: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    val faqList = listOf(
        FAQitems(
            "Q1. Can I place order on call?",
            "A1. Sorry, we don’t accept orders on call. However you can call us on +91-7259085922 for any help related to placing order."
        ),
        FAQitems(
            " Q2.How do I know my order is confirmed?",
            "A2. We will send you a order confirmation email once we receive your order."
        ),
        FAQitems(
            "Q3. How do I edit my order after placing it?",
            " A3. Please call us on +91-7259085922 or write email to packaging@foodrunner.in mentioning your Order #"
        ),
        FAQitems(
            "Q4. How do I cancel my order?",
            "A4. Please refer to our return and cancellation policy here."
        ),
        FAQitems(
            "Q5. How will be my orders delivered?",
            "A5. Orders are delivered directly by the FoodRunner Packaging Assist suppliers. Different items in an order could be fulfilled by different suppliers. We will share the contact details and amount payable for all deliveries via email and SMS."
        ),
        FAQitems(
            "Q6. When is the order delivered?",
            " A6. Our suppliers deliver the products within 3-4 days of orders being placed. For products with your logo printed, lead time could be higher. Please check details at the product page."
        ),
        FAQitems("Q7. Is delivery free?", "A7. Yes. There are no delivery or handling charges."),
        FAQitems(
            "Q8. Why is my location is not serviceable?",
            "A8. We are currently live only in a few cities. We will be expanding soon."
        ),
        FAQitems(
            "Q9. Can I have my order delivered to me by next day?",
            "A9. Please call FoodRunner Partner Support on +91-7259085922 or write email to packaging@foodrunner.in mentioning your Order # and we shall try our best to help you out."
        ),
        FAQitems(
            "Q10. Will I get my entire order in single delivery?",
            "A10. This depends on the items you have ordered. We have partnered with different suppliers for different products to help you get the best pricing."
        ),
        FAQitems(
            "Q11. What if I have a problem with my delivery?",
            "A11. Please call FoodRunner Partner Support on +91-7259085922 or write email to packaging@foodrunner.in mentioning your Order # and they will help you out."
        ),
        FAQitems(
            "Q12. Can I change the address once the order has been placed?",
            "A12. Yes, you can ask for a change in delivery address if our supplier hasn’t dispatched the placed order by calling Swiggy Partner Support on +91-7259085922 or writing to packaging@foodrunner.in mentioning your Order #"
        ),
        FAQitems(
            "Q13. When I have to pay?",
            "A13. You will have to pay at the time of delivery as per the payment details we send to you via email."
        ),
        FAQitems(
            "Q14. What payment modes are accepted?",
            "A14. You can pay by cash or cheque at the time of delivery. We will soon be introducing more payment methods."
        ),
        FAQitems(
            "Q15. Can I buy the goods on credit?",
            "A15. Sorry, we don’t allow a credit on goods currently."
        ),
        FAQitems(
            "Q16. Can I pay online?",
            " A16. You can do NEFT transfer. We will be sharing the details via email."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        recyclefaq = view.findViewById(R.id.recyclefaq)
        layoutManager = LinearLayoutManager(activity)

        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter =
                FAQRecyclerAdapter(activity as Context, faqList)
            recyclefaq.adapter = recyclerAdapter
            recyclefaq.layoutManager = layoutManager
        }
        return view

    }
}
