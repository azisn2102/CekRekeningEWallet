package com.azissn.darinol

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView

class ElectronicWalletArrayAdapter(context: Context, private val items: List<ElectronicWallet>) : ArrayAdapter<ElectronicWallet>(context, R.layout.dropdown_item, items) {

    private var filteredItems: List<ElectronicWallet> = items

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.dropdown_item, parent, false)

        val textView = view.findViewById<TextView>(R.id.textViewItem)
        textView.text = filteredItems[position].namaBank

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val suggestions = if (constraint.isNullOrEmpty()) {
                    items
                } else {
                    items.filter { it.namaBank.contains(constraint, true) }
                }

                filterResults.values = suggestions
                filterResults.count = suggestions.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = if (results?.values != null) {
                    results.values as List<ElectronicWallet>
                } else {
                    items
                }
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as? ElectronicWallet)?.namaBank ?: ""
            }
        }
    }

    override fun getItem(position: Int): ElectronicWallet? {
        return filteredItems[position]
    }

    override fun getCount(): Int {
        return filteredItems.size
    }
}