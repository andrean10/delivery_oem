package com.kontrakanprojects.tyreom.view.main.ui.karyawan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.tyreom.databinding.RvItemKaryawanBinding
import com.kontrakanprojects.tyreom.model.pegawai.ResultsKaryawan

class ListKaryawanAdapter : RecyclerView.Adapter<ListKaryawanAdapter.ListKaryawanViewHolder>() {

    private var onItemClickCallBack: OnItemClickCallBack? = null
    private val listKaryawan = ArrayList<ResultsKaryawan>()

    fun setData(karyawans: List<ResultsKaryawan>?) {
        if (karyawans == null) return
        listKaryawan.clear()
        listKaryawan.addAll(karyawans)
        notifyDataSetChanged()
    }

    fun getData() = listKaryawan

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListKaryawanViewHolder {
        val binding =
            RvItemKaryawanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListKaryawanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListKaryawanViewHolder, position: Int) {
        holder.bind(listKaryawan[position])
    }

    override fun getItemCount() = listKaryawan.size

    inner class ListKaryawanViewHolder(private val binding: RvItemKaryawanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultsKaryawan: ResultsKaryawan) {
            with(binding) {
                tvNameKaryawan.text = resultsKaryawan.namaKaryawan
                tvUsernameKaryawan.text = resultsKaryawan.nik.toString()
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsKaryawan) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsKaryawan: ResultsKaryawan)
    }
}