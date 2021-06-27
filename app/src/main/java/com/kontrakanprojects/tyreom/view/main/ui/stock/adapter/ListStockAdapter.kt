package com.kontrakanprojects.tyreom.view.main.ui.stock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.tyreom.databinding.RvItemStockBarangBinding
import com.kontrakanprojects.tyreom.model.stock_barang.ResultsStockBarang
import com.kontrakanprojects.tyreom.utils.formateDate

class ListStockAdapter : RecyclerView.Adapter<ListStockAdapter.ListStockViewHolder>() {

    private var onItemClickCallBack: OnItemClickCallBack? = null
    private val listStockBarang = ArrayList<ResultsStockBarang>()

    private val TAG = ListStockAdapter::class.simpleName

    fun setData(stockBarangs: List<ResultsStockBarang>?) {
        if (stockBarangs == null) return
        listStockBarang.clear()
        listStockBarang.addAll(stockBarangs)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStockViewHolder {
        val binding =
            RvItemStockBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStockViewHolder, position: Int) {
        holder.bind(listStockBarang[position])
    }

    override fun getItemCount() = listStockBarang.size

    inner class ListStockViewHolder(private val binding: RvItemStockBarangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultsStockBarang: ResultsStockBarang) {
            with(binding) {
                val dayNumber = formateDate(resultsStockBarang.tanggal!!, "d", TAG!!)
                val month = formateDate(resultsStockBarang.tanggal, "MMM", TAG)
                val yearNumber = formateDate(resultsStockBarang.tanggal, "yyyy", TAG)

                tvCodeSize.text = resultsStockBarang.codeSize.toString()
                tvQuantity.text = resultsStockBarang.quantity
                tvWeekly.text = resultsStockBarang.weekly.toString()
                tvDayNumber.text = dayNumber
                tvMonthNumber.text = month
                tvYearNumber.text = yearNumber

            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsStockBarang) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsStockBarang: ResultsStockBarang)
    }
}