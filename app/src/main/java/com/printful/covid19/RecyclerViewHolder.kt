package com.printful.covid19

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var country:TextView = itemView.findViewById<View>(R.id.countryInputTextView) as TextView
    var vaccines:TextView = itemView.findViewById<View>(R.id.vaccinesInputTextView) as TextView
    var lastDate:TextView = itemView.findViewById<View>(R.id.lastObservationInputView) as TextView
    var source:TextView = itemView.findViewById<View>(R.id.totalDosesInputView) as TextView

    init {
        itemView.setOnClickListener{
            val intent = Intent(it.context, MoreInformationActivity::class.java)
            intent.putExtra("country", country.text)
            it.context.startActivity(intent)
        }
    }
}