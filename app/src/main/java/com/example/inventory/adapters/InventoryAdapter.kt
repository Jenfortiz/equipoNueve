package com.example.inventory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.R
import com.example.inventory.model.Inventory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class InventoryAdapter(
    private var items: List<Inventory>,
    private val onItemClick: (Inventory) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Inventory>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): Inventory {
        return items[position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        private val idTextView: TextView = itemView.findViewById(R.id.tv_item_id)
        private val priceTextView: TextView = itemView.findViewById(R.id.tv_item_price)

        fun bind(item: Inventory) {

            val symbols = DecimalFormatSymbols().apply {
                groupingSeparator = '.'
                decimalSeparator = ','
            }
            val formatter = DecimalFormat("#,##0.00", symbols)
            val formattedPrice = "$ " + formatter.format(item.price)

            nameTextView.text = item.name
            idTextView.text = "Id: ${item.id}"
            priceTextView.text = formattedPrice
        }
    }
}