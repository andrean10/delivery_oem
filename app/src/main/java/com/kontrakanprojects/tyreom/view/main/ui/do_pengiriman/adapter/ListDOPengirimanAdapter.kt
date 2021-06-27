package com.kontrakanprojects.tyreom.view.main.ui.do_pengiriman.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.tyreom.databinding.RvItemDoPengirimanBinding
import com.kontrakanprojects.tyreom.model.do_pengiriman.ResultsDOPengiriman
import com.kontrakanprojects.tyreom.utils.formateDate

class ListDOPengirimanAdapter : RecyclerView.Adapter<ListDOPengirimanAdapter.ListDOViewHolder>() {

    private var onItemClickCallBack: OnItemClickCallBack? = null
    private val listDOPengiriman = ArrayList<ResultsDOPengiriman>()

    private val TAG = ListDOPengirimanAdapter::class.simpleName

    fun setData(doPengiriman: List<ResultsDOPengiriman>?) {
        if (doPengiriman == null) return
        listDOPengiriman.clear()
        listDOPengiriman.addAll(doPengiriman)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDOViewHolder {
        val binding =
            RvItemDoPengirimanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListDOViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListDOViewHolder, position: Int) {
        holder.bind(listDOPengiriman[position])
    }

    override fun getItemCount() = listDOPengiriman.size

    inner class ListDOViewHolder(private val binding: RvItemDoPengirimanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultsDOPengiriman: ResultsDOPengiriman) {
            with(binding) {
                val dayNumber = formateDate(resultsDOPengiriman.tanggalDo!!, "d", TAG!!)
                val month = formateDate(resultsDOPengiriman.tanggalDo, "MMM", TAG)
                val yearNumber = formateDate(resultsDOPengiriman.tanggalDo, "yyyy", TAG)

                tvDoPengiriman.text = resultsDOPengiriman.noDo.toString()
                tvQuantity.text = resultsDOPengiriman.quantity
                tvCodeSize.text = resultsDOPengiriman.codeSize.toString()
                tvDayNumber.text = dayNumber
                tvMonthNumber.text = month
                tvYearNumber.text = yearNumber
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsDOPengiriman) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsDOPengiriman: ResultsDOPengiriman)
    }
}