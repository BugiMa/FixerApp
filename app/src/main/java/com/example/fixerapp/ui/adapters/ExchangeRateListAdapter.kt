package com.example.fixerapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fixerapp.databinding.ItemDateBinding
import com.example.fixerapp.databinding.ItemExchangeRateBinding
import com.example.fixerapp.domain.ExchangeRate
import com.example.fixerapp.ui.fragments.ExchangeListFragmentDirections

class ExchangeRateListAdapter(
    private val rates: ArrayList<ExchangeRate>,
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class DateViewHolder(private val itemBinding: ItemDateBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(date: String) {
            itemBinding.date.text = date
        }
    }

    inner class ExchangeRateViewHolder(private val itemBinding: ItemExchangeRateBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(currency: String, exchangeRate: Double) {
            itemBinding.currency.text = currency
            itemBinding.exchangeRate.text = exchangeRate.toString()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position % 169
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val itemBinding =
                ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DateViewHolder(itemBinding)
        } else {
            val itemBinding =
                ItemExchangeRateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ExchangeRateViewHolder(itemBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 0) {
            (holder as DateViewHolder).bind(rates[position].date)
        } else {
            (holder as ExchangeRateViewHolder).bind(rates[position].rate.first, rates[position].rate.second)
            holder.itemView.setOnClickListener {
                val directions = ExchangeListFragmentDirections
                    .actionExchangeListFragmentToExchangeDetailsFragment(position)
                it.findNavController().navigate(directions)
            }
        }
    }

    override fun getItemCount(): Int {
        return rates.size
    }

    fun updateData(data: ArrayList<ExchangeRate>) {
        this.rates.addAll(data)
        this.notifyItemRangeInserted(itemCount, data.size)
    }


}