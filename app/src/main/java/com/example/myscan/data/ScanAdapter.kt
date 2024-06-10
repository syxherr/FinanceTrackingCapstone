package com.example.myscan.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myscan.ui.DetailActivity
import com.example.myscan.databinding.ListItemBinding

class ScanAdapter (private val scanList: ArrayList<ScanModel>) : RecyclerView.Adapter<ScanAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return scanList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = scanList[position]

        holder.binding.apply {
            nominal.text = currentItem.resultScan

            val date = Date(currentItem.timestamp)
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            tanggal.text = formattedDate

            // Menggunakan Glide untuk menampilkan gambar dari URL
            Glide.with(root.context)
                .load(currentItem.image)
                .into(imgPhoto)

            // Menambahkan onClickListener untuk membuka DetailActivity
            root.setOnClickListener {
                val intentDetail = Intent(root.context, DetailActivity::class.java)
                // Menggunakan bundleOf untuk mengirimkan data ke DetailActivity
                intentDetail.putExtras(bundleOf("item_list" to currentItem))
                root.context.startActivity(intentDetail)
            }
        }
    }
}