package com.kontrakanprojects.tyreom.view.main.ui.estimasi_pengiriman.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.tyreom.databinding.RvItemEstimasiPengirimanBinding
import com.kontrakanprojects.tyreom.model.estimasi_pengiriman.ResultsEstimasiPengiriman
import com.kontrakanprojects.tyreom.utils.formateDate

class ListEstimasiPengirimanAdapter :
    RecyclerView.Adapter<ListEstimasiPengirimanAdapter.ListEstimasiViewHolder>() {

    private var onItemClickCallBack: OnItemClickCallBack? = null
    private val listEstimasiPengiriman = ArrayList<ResultsEstimasiPengiriman>()

    private val TAG = ListEstimasiPengirimanAdapter::class.simpleName

    fun setData(estimasiPengiriman: List<ResultsEstimasiPengiriman>?) {
        if (estimasiPengiriman == null) return
        listEstimasiPengiriman.clear()
        listEstimasiPengiriman.addAll(estimasiPengiriman)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEstimasiViewHolder {
        val binding =
            RvItemEstimasiPengirimanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ListEstimasiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListEstimasiViewHolder, position: Int) {
        holder.bind(listEstimasiPengiriman[position])
    }

    override fun getItemCount() = listEstimasiPengiriman.size

    inner class ListEstimasiViewHolder(private val binding: RvItemEstimasiPengirimanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultsEstimasiPengiriman: ResultsEstimasiPengiriman) {
            with(binding) {
                val dayNumber = formateDate(resultsEstimasiPengiriman.tanggal!!, "d", TAG!!)
                val month = formateDate(resultsEstimasiPengiriman.tanggal, "MMM", TAG)
                val yearNumber = formateDate(resultsEstimasiPengiriman.tanggal, "yyyy", TAG)

                tvCodeSize.text = resultsEstimasiPengiriman.codeSize.toString()
                tvQuantity.text = resultsEstimasiPengiriman.quantity
                tvDayNumber.text = dayNumber
                tvMonthNumber.text = month
                tvYearNumber.text = yearNumber
            }

            itemView.setOnClickListener {
                onItemClickCallBack?.onItemClicked(
                    resultsEstimasiPengiriman
                )
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsEstimasiPengiriman: ResultsEstimasiPengiriman)
    }
}